package com.gongchuang.ethtoken.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.utils.Md5Utils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.view.InputPwdDialog;

import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PreMoneyAddRecord  extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.transaction_hash)
    TextView tv_transaction_hash;
    @BindView(R.id.transaction_address_from)
    TextView tv_transaction_address_from;
    @BindView(R.id.transaction_address_to)
    TextView tv_transaction_address_to;
    @BindView(R.id.transaction_amount)
    TextView transaction_amount;
    @BindView(R.id.transaction_note)
    TextView transaction_note;
    @BindView(R.id.transaction_date)
    TextView tv_transaction_date;

    private InputPwdDialog inputPwdDialog;
    private static final int DETAIL_PRE_PAY = 1102;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pre_money_search;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.pre_transaction_records);
        ivBtn.setVisibility(View.INVISIBLE);
        rlBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void initDatas() {

        inputPwdDialog = new InputPwdDialog(this);

        tv_transaction_hash.setText("交易哈希:"+getIntent().getStringExtra("transactionHash"));
        tv_transaction_date.setText("截止时间"+getIntent().getStringExtra("transactionDate"));
        transaction_amount.setText("交易额度:"+getIntent().getStringExtra("transactionAmount"));
        tv_transaction_address_from.setText("发送地址:"+getIntent().getStringExtra("transactionAddressFrom"));
        tv_transaction_address_to.setText("收款地址:"+getIntent().getStringExtra("transactionAddressTo"));
        transaction_note.setText("交易备注:"+getIntent().getStringExtra("transactionNote"));
    }
    public  void configViews(){}

    @OnClick({R.id.btn_stop})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_stop:
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
                        if (TextUtils.equals(WalletDaoUtils.getCurrent().getPassword(), Md5Utils.md5(pwd))) {
                            showDialog("正在停止");
                            Boolean stop_success_or = TokenApi.stop_pay_in_advance(getIntent().getStringExtra("transactionAddressFrom"),getIntent().getStringExtra("transactionHash"));
                            if(stop_success_or){
                                Toast.makeText(PreMoneyAddRecord.this, "停止成功!", Toast.LENGTH_SHORT).show();
                                setResult(DETAIL_PRE_PAY,null);
                                finish();
                            }else {
                                Toast.makeText(PreMoneyAddRecord.this, "停止失败!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });



        }
    }

}

