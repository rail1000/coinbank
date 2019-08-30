package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.PreDeposit;
import com.gongchuang.ethtoken.ui.contract.CreateWalletContract;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.Md5Utils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.utils.bip44.Address;
import com.gongchuang.ethtoken.view.InputPwdDialog;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dwq on 2018/3/14/014.
 * e-mail:lomapa@163.com
 */

public class PreMoneyCun extends BaseActivity{
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.et_transfer_address)
    EditText transferaddress;
    @BindView(R.id.transferamount)
    EditText transferamount;
    @BindView(R.id.receievetime)
    EditText receievetime;
    @BindView(R.id.transferdemo)
    EditText transferdemo;
    @BindView(R.id.btn_confirm_deposit)
    TextView confirmdeposit;

    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private long walletId;
    private String walletAddress;
    private TokenApiService service;

    ETHWallet ethWallet;
    Credentials credentials;
    String finalSignedDatas;
    String tranaddress;
    String tranamount;
    String trandemo;
    String extraTime;
    ArrayList<PreDeposit> depositList=new ArrayList<>();
    private static final int QRCODE_SCANNER_REQUEST = 1100;
    @Override
    public int getLayoutId() {
        return R.layout.activity_pre_money_cun;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.pre_money_cun);
        ivBtn.setImageResource(R.mipmap.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        inputPwdDialog = new InputPwdDialog(this);
        ethWallet = WalletDaoUtils.getCurrent();
        walletId = ethWallet.getId();
        walletPwd = ethWallet.getPassword();
        walletAddress = ethWallet.getAddress();
        credentials =  ETHWalletUtils.deriveCredentials(walletId,walletPwd);
    }

    @Override
    public void configViews() {


    }
    @Override
    protected void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .navigationBarColor(R.color.white)
                .init();
    }

    @OnClick({R.id.rl_btn,R.id.btn_confirm_deposit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                Intent intent = new Intent(PreMoneyCun.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.btn_confirm_deposit:
                tranaddress = transferaddress.getText().toString().trim();
                tranamount = transferamount.getText().toString().trim();
                extraTime = receievetime.getText().toString().trim();
                trandemo = transferdemo.getText().toString().trim();
                if (tranamount.length() != 0) {
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
                                showDialog("正在进行预付工资");
                                //签名的原始数据，中只包含了金额以及收款地址
                                RawTransaction rawTransaction = RawTransaction.createEtherTransaction(new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), tranaddress, new BigInteger(tranamount));
                                //todo 得到签名信息
                                //finalSignedDatas = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction,credentials));
                                finalSignedDatas = "testsig";
                                Boolean suceess_or_not = TokenApi.pay_in_advance(walletAddress, tranaddress, tranamount, trandemo, finalSignedDatas);
                                if (suceess_or_not) {
                                    Toast.makeText(PreMoneyCun.this, "预付成功!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(PreMoneyCun.this, "预付失败!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext, "请输入预付金额", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
