package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.hotspot2.pps.Credential;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.ETHWalletDao;
import com.gongchuang.ethtoken.ui.contract.WalletDetailContract;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.Md5Utils;
import com.gongchuang.ethtoken.utils.SignUtil;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.utils.bip44.Address;
import com.gongchuang.ethtoken.utils.bip44.SignedMessage;
import com.gongchuang.ethtoken.utils.bip44.WrongSignatureException;
import com.gongchuang.ethtoken.view.InputPwdDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.web3j.crypto.RawTransaction.createEtherTransaction;

/**
 * Created by dwq on 2018/3/19/019.
 * e-mail:lomapa@163.com
 */

public class ETCTransferActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.lly_contacts)
    LinearLayout lly_contacts;//联系人
    @BindView(R.id.transfer_amount)
    EditText et_transfer_amount;//金额
    @BindView(R.id.etc_transfer_remark)
    EditText etc_transfer_remark;//备注
    @BindView(R.id.et_transfer_address)
    EditText etTransferAddress;
    String transfer_amount = null;
    String transfer_remark = null;
    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private long walletId;
    private Address address;
    private String walletAddress;
    private String transfer_address;
    private TokenApiService service;

    ETHWallet ethWallet;
    Credentials credentials;
    String finalSignedDatas;
    String token;
    private static final String TAG = "ETCTransferActivity";
    private static final int QRCODE_SCANNER_REQUEST = 1100;
    private static final int CONTACTS_REQUEST = 1112;

    @Override
    public int getLayoutId() {
        return R.layout.activity_etc_transfer;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.etc_transfer_title);
        ivBtn.setImageResource(R.mipmap.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        token = myPreference.getString("TOKEN",null);
        inputPwdDialog = new InputPwdDialog(this);
        ethWallet = WalletDaoUtils.getCurrent();
        walletId = ethWallet.getId();
        walletPwd = ethWallet.getPassword();
        walletAddress = ethWallet.getAddress();
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_btn, R.id.btn_next, R.id.lly_contacts, R.id.transfer_amount})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.rl_btn:
                intent = new Intent(ETCTransferActivity.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.lly_contacts:
                //todo 联系人功能暂时空
                break;
            case R.id.btn_next:
                transfer_address = etTransferAddress.getText().toString();
                transfer_amount = et_transfer_amount.getText().toString();
                transfer_remark =   etc_transfer_remark.getText().toString();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String tp = df.format(System.currentTimeMillis());//时间
                final String ts;//大额或者小额
                if(Integer.parseInt(transfer_amount)>10000){ ts="0"; }else{ ts ="1";}
                if (transfer_amount.length() != 0) {
                    inputPwdDialog.show();
                    inputPwdDialog.setDeleteAlertVisibility(false);
                    inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                        @Override
                        public void onCancel() {
                            inputPwdDialog.dismiss();
                        }
                        @Override
                        public void onConfirm(String pwd) {
                            inputPwdDialog.dismiss();
                            if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                                showDialog("正在转账");
                                //得到签名
                                List<String> rawDataList = new ArrayList<>();
                                rawDataList.add(transfer_amount);
                                rawDataList.add(walletAddress);
                                rawDataList.add(transfer_remark);
                                rawDataList.add(transfer_address);
                                rawDataList.add(tp);
                                rawDataList.add(ts);
                                String privateKey = ETHWalletUtils.derivePrivateKey(WalletDaoUtils.getCurrent().getId(), pwd);
                                String signatureData = SignUtil.getSign(privateKey,rawDataList);


                                Log.i(TAG, "signatureData"+signatureData.length());


                                try {
                                    tansfer(transfer_amount,walletAddress,transfer_remark,transfer_address,signatureData,tp,ts);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);}
                        }
                    });
                } else {
                    Toast.makeText(mContext, "请输入转账金额", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                etTransferAddress.setText(scanResult);
            }
        } else if (requestCode == CONTACTS_REQUEST) {
            if (data != null) {
                String contactsResult = data.getStringExtra("contactsResult");
                etTransferAddress.setText(contactsResult);
            }
        }
    }


    public void tansfer(final String transfer_amount, String moneyFrom, String transfer_remark, String moneyTo, String sig, String tp, String ts) throws JSONException {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .client(builder.build())
                .build();
        service = retrofit.create(TokenApiService.class);
        JSONObject root = new JSONObject();
        root.put("Amount", Float.parseFloat(transfer_amount));
        root.put("In", moneyFrom);
        root.put("Memo", transfer_remark);
        root.put("Out", moneyTo);
        root.put("Sign", sig);
        root.put("Tp", tp);
        root.put("Ts", ts);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.putTransaction(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        String jsonString = response.body().string();
                        Log.i(TAG, jsonString);
                        String banlance  =  new JSONObject(jsonString).get("balance").toString();
                        Toast.makeText(ETCTransferActivity.this, "转账成功!", Toast.LENGTH_SHORT).show();
                        WalletDaoUtils.updateWalletBanlance(ethWallet.getId(),banlance);
                        finish();
                    }catch (IOException e) {
                        Log.e(TAG, "Failed to parse JSON"+e);
                        ToastUtils.showLongToast("转账失败！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                    Toast.makeText(mContext, "转账失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
