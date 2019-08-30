package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.gongchuang.ethtoken.utils.ETHMnemonic;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.Md5Utils;
import com.gongchuang.ethtoken.utils.SignUtil;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.view.InputPwdDialog;

import org.json.JSONException;
import org.json.JSONObject;
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

public class BankWithdrawActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.card_number)
    TextView tvCardNumber;
    @BindView(R.id.available_balance)
    TextView available_balance;
    @BindView(R.id.btn_confirm_withdraw)
    TextView buttonWithdraw;
    @BindView(R.id.editText)
    TextView editText;
    private static final int REQUEST_ACCOUNT_MANAGE = 1100;
    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private long walletId;
    ETHWallet ethWallet;
    String finalSignedDatas;
    String adress;
    String balance;
    String token;
    String cardNumber;
    String accountID;
    String userName;
    private TokenApiService service;
    private static final String TAG = "BankTopUpActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_bank_withdraw;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.withdraw);
        ivBtn.setVisibility(View.INVISIBLE);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        token = myPreference.getString("TOKEN",null);
        userName = myPreference.getString("USER_NAME",null);
        cardNumber = myPreference.getString("ACCOUNT_NUMBER",null);
        inputPwdDialog = new InputPwdDialog(this);
        ethWallet = WalletDaoUtils.getCurrent();
        adress = ethWallet.getAddress();
        walletPwd = ethWallet.getPassword();
        balance = ethWallet.getBanlance();
    }

    @Override
    public void configViews() {
        tvCardNumber.setText("尾号"+cardNumber.substring(6,10));
        available_balance.setText(balance);
    }

    @OnClick({R.id.btn_confirm_withdraw})
    public void onClick(View view) {
        final String amount = editText.getText().toString().trim();
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
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog("正在提现");
                            //得到签名
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

        }
    }

    public void tansfer(String adress, final String amount, String sig, String userName) throws JSONException {
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
        root.put("Amount", Float.parseFloat(amount));
        root.put("Sig", sig);
        root.put("Token", token);
        root.put("Username", userName);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.newCoinOutBase(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        String jsonString = null;
                        jsonString = response.body().string();
                        LogUtils.i("BankWithdrawActivity",jsonString);
                        String banlance  =  new JSONObject(jsonString).get("vbalance").toString();
                        WalletDaoUtils.updateWalletBanlance(ethWallet.getId(),banlance);
                        Toast.makeText(BankWithdrawActivity.this, "提现成功!", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                    Toast.makeText(mContext, "提现失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
