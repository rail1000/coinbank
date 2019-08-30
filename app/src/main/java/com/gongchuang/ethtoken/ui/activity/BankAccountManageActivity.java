package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.view.InputPwdDialog;

import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.OnClick;

public class BankAccountManageActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.card_number)
    TextView tvCardNumber;
    @BindView(R.id.lly_card_to_wallet)
    LinearLayout llyCardToWallet;
    @BindView(R.id.lly_wallet_to_card)
    LinearLayout llyWalletToCard;
    @BindView(R.id.lly_bank_link)
    LinearLayout llyBankLink;
    @BindView(R.id.lly_exit_bank_account)
    LinearLayout llyExit;
    private static final int REQUEST_ACCOUNT_MANAGE = 1100;


    @Override
    public int getLayoutId() {
        return R.layout.activity_bank_account_manage;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_personal_center);
        ivBtn.setVisibility(View.INVISIBLE);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
    }


    @Override
    public void configViews() {
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String cardNumber = myPreference.getString("ACCOUNT_NUMBER", "*** *** ****");
        tvCardNumber.setText("    ***   ***   " + cardNumber.substring(6,10));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_MANAGE) {
            finish();
        }
    }

    @OnClick({R.id.lly_card_to_wallet, R.id.lly_wallet_to_card, R.id.lly_bank_link
            , R.id.lly_exit_bank_account})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_card_to_wallet://充值
               intent = new Intent(this, BankTopUpActivity.class);
               startActivity(intent);
                break;
            case R.id.lly_wallet_to_card://提现
                intent = new Intent(this, BankWithdrawActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_bank_link://链接
                intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                String data="https://www.citibank.com.cn/sim/index.htm";
                intent.setData(Uri.parse(data));
                startActivity(intent);
                break;
            case R.id.lly_exit_bank_account://退出
                //Todo 对话框确认钱包已经经过备份或者导出
                break;
        }
    }
}
