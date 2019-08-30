package com.gongchuang.ethtoken.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;

import java.util.List;

import butterknife.BindView;

public class TransactionDetailActivity extends BaseActivity {
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




    private List<String> strings;
    private MessageCenterAdapter drawerWalletAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.transaction_records);
        ivBtn.setVisibility(View.INVISIBLE);
        rlBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void initDatas() {
        tv_transaction_hash.setText("交易哈希:"+getIntent().getStringExtra("transactionHash"));
        tv_transaction_date.setText(getIntent().getStringExtra("transactionDate"));
        transaction_amount.setText("交易额度:"+getIntent().getStringExtra("transactionAmount"));
        tv_transaction_address_from.setText("发送地址:"+getIntent().getStringExtra("transactionAddressFrom"));
        tv_transaction_address_to.setText("收款地址:"+getIntent().getStringExtra("transactionAddressTo"));
        transaction_note.setText("交易备注:"+getIntent().getStringExtra("transactionNote"));
    }
    public  void configViews(){}
}
