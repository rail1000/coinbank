package com.gongchuang.ethtoken.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.ETHWalletDao;
import com.gongchuang.ethtoken.ui.contract.CreateWalletContract;
import com.gongchuang.ethtoken.ui.presenter.CreateWalletPresenter;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.SignUtil;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.security.Key;
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

/**
 * Created by dwq on 2018/3/14/014.
 * e-mail:lomapa@163.com
 */

public class CreateWalletActivity extends BaseActivity implements CreateWalletContract.View {

    private static final int CREATE_WALLET_RESULT = 2202;
    private static final int LOAD_WALLET_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.btn_create_wallet)
    TextView btnCreateWallet;

    private static final String TAG = "CreateWalletActivity";
    private static final int REQUEST_WALLET_CREATE = 1100;
    private CreateWalletContract.Presenter mPresenter;
    private TokenApiService service;
    private String walletPwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.property_drawer_create_wallet);
    }

    @Override
    public void initDatas() {
        mPresenter = new CreateWalletPresenter(this);
    }

    @Override
    public void configViews() {
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        btnCreateWallet.setEnabled(isChecked);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_WALLET_CREATE) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .navigationBarColor(R.color.white)
                .init();
    }

    String TAG_SERVICE="tag";
    private static int REQUEST_PERMISSION_CODE = 2;
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
    }



    @OnClick({R.id.tv_agreement, R.id.btn_create_wallet
            , R.id.lly_wallet_agreement, R.id.btn_input_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_agreement:
                break;
            case R.id.btn_create_wallet:
                checkPermission();
                String walletName = etWalletName.getText().toString().trim();
                walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(walletName, walletPwd, confirmPwd, pwdReminder);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.creating_wallet_tip));
                    mPresenter.createWallet(walletName, walletPwd, confirmPwd, pwdReminder);
                }
                break;
            case R.id.lly_wallet_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                } else {
                    cbAgreement.setChecked(true);
                }
                break;
            case R.id.btn_input_wallet:
                Intent intent = new Intent(this, LoadWalletActivity.class);
                startActivityForResult(intent, LOAD_WALLET_REQUEST);
                break;
        }
    }

    private boolean verifyInfo(String walletName, String walletPwd, String confirmPwd, String pwdReminder) {
        if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_repeat_tips);
            // 同时不可重复
            return false;
        } else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_input_tips);
            // 同时不可重复
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }


    @Override
    public void showError(String errorInfo) {
        dismissDialog();
        LogUtils.e("CreateWalletActivity", errorInfo);
        ToastUtils.showToast(errorInfo);
    }

    @Override
    public void complete() {

    }

    @Override
    public void jumpToWalletBackUp(ETHWallet wallet) {
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String cardnumber  = myPreference.getString("ACCOUNT_NUMBER",null);
        String token  = myPreference.getString("TOKEN",null);
        String privateKey = ETHWalletUtils.derivePrivateKey(wallet.getId(),walletPwd);
        String publicKey = ETHWalletUtils.derivePublicKey(wallet.getId(),walletPwd);
        //得到签名
        List<String> rawDataList = new ArrayList<>();
        rawDataList.add(cardnumber);
        rawDataList.add(wallet.getAddress());
        rawDataList.add(publicKey);
        rawDataList.add(token);
        String signatureData = SignUtil.getSign(privateKey,rawDataList);
        try {
            getVirtualAccount(wallet,cardnumber,wallet.getAddress(),publicKey,signatureData,token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        finish();
    }
    //钱包注册api
    public void getVirtualAccount(final ETHWallet wallet, String bankAccount, String adress, String publicKey,String sig,String token) throws JSONException {
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
        root.put("AccountNumber", bankAccount);
        root.put("Address", adress);
        LogUtils.i("CreateWalletActivity","Address"+adress.length());
        root.put("Pubkey", publicKey);
        LogUtils.i("CreateWalletActivity","Pubkey"+publicKey.length());
        root.put("Signature", sig);
        LogUtils.i("CreateWalletActivity","Signature"+sig.length());
        root.put("Token", token);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.getVirtual(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    ToastUtils.showToast("注册钱包成功");
                    dismissDialog();
                    EventBus.getDefault().post(wallet);
                    setResult(CREATE_WALLET_RESULT, new Intent());
                    Intent intent = new Intent(CreateWalletActivity.this, WalletBackupActivity.class);
                    intent.putExtra("walletId", wallet.getId());
                    intent.putExtra("walletPwd", wallet.getPassword());
                    intent.putExtra("walletAddress", wallet.getAddress());
                    intent.putExtra("walletName", wallet.getName());
                    intent.putExtra("walletMnemonic", wallet.getMnemonic());
                    startActivity(intent);
                    try {
                        LogUtils.i("CreateWalletActivity","注册钱包成功"+response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    Log.i(TAG, "response.code()=" + response.code());
                    ToastUtils.showLongToast("虚拟注册失败！");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return;
    }



}
