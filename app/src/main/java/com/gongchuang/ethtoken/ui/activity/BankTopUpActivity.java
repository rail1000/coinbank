package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.ETHWalletDao;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.Md5Utils;
import com.gongchuang.ethtoken.utils.SignUtil;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.view.InputPwdDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
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

public class BankTopUpActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.top_up_amount)
    EditText topup;
    @BindView(R.id.btn_confirm_topup)
    TextView btn_confirm_topup;
    private static final int FINISH = 1100;
    private InputPwdDialog inputPwdDialog;
    private String adress;
    private String walletPwd;

    ETHWallet ethWallet;
    String token;
    String userName;
    private TokenApiService service;
    private static final String TAG = "BankTopUpActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_bank_top_up;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("充值到钱包");
        ivBtn.setVisibility(View.INVISIBLE);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        userName = myPreference.getString("USER_NAME",null);
        token = myPreference.getString("TOKEN",null);
        inputPwdDialog = new InputPwdDialog(this);
        ethWallet = WalletDaoUtils.getCurrent();
        adress = ethWallet.getAddress();
        walletPwd = ethWallet.getPassword();
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.btn_confirm_topup})
    public void onClick(View view) {
        final String amount = topup.getText().toString().trim();
        if(amount.length()!=0){
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
                        //得到签名
                        showDialog("正在充值");
                        List<String> rawDataList = new ArrayList<>();
                        rawDataList.add(adress);
                        rawDataList.add(amount);
                        rawDataList.add(userName);
                        String privateKey = ETHWalletUtils.derivePrivateKey(WalletDaoUtils.getCurrent().getId(), pwd);
                        String signatureData = SignUtil.getSign(privateKey,rawDataList);

                        try {
                            tansfer(adress,amount,signatureData,userName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                    }
                }
            });
        }else{
            Toast.makeText(mContext, "请输入金额", Toast.LENGTH_SHORT).show();
        }
    }

    public void tansfer(String adress, final String transfer_amount, String signatureData, String username) throws JSONException {
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
        root.put("Address", adress);
        root.put("Amount", Float.parseFloat(transfer_amount));
        root.put("Sign", signatureData);
        root.put("Token", token);
        root.put("Username", username);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.newCoinBase(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        String jsonString = response.body().string();
                        Log.i(TAG, "充值"+jsonString);
                        String banlance  = new JSONObject(jsonString).get("vbalance").toString();
                        WalletDaoUtils.updateWalletBanlance(ethWallet.getId(),banlance);
                        Toast.makeText(BankTopUpActivity.this, "充值成功!", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ToastUtils.showLongToast("充值失败！");
                        Log.e(TAG, "Failed to parse JSON", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                    Toast.makeText(mContext, "充值失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}


