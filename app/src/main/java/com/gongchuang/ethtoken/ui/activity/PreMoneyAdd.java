package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.PreDeposit;
import com.gongchuang.ethtoken.ui.adapter.PreDepositAdapter;
import com.gongchuang.ethtoken.ui.adapter.PretradeAdapter;
import com.gongchuang.ethtoken.ui.contract.WalletManagerContract;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by dwq on 2018/3/19/019.
 * e-mail:lomapa@163.com
 */

public class PreMoneyAdd extends BaseActivity{

    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int DETAIL_PRE_PAY = 1102;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.predeposit_lv)
    ListView predeposit_lv;
    List<PreDeposit> depositList=new ArrayList<>();
    private PreDepositAdapter preDepositAdapter;
    private PretradeAdapter drawerWalletAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pre_money_add;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("预付工资管理");
    }

    @Override
    public void initDatas() {
        depositList =  TokenApi.pay_in_advanc_record(WalletDaoUtils.getCurrent().getAddress());
        drawerWalletAdapter = new PretradeAdapter(this,depositList, R.layout.list_item_trade_center);
        predeposit_lv.setAdapter(drawerWalletAdapter);
    }

    @Override
    public void configViews() {
        predeposit_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PreMoneyAdd.this, PreMoneyAddRecord.class);
                PreDeposit preDeposit = depositList.get(position);
                intent.putExtra("transactionHash", preDeposit.getHash());
                intent.putExtra("transactionDate", preDeposit.getPredemo());
                intent.putExtra("transactionAmount", preDeposit.getPreamount());
                intent.putExtra("transactionAddressFrom", preDeposit.getPreaddress_from());
                intent.putExtra("transactionAddressTo", preDeposit.getPreaddress_to());
                intent.putExtra("transactionNote", preDeposit.getPredemo());
                startActivityForResult(intent,DETAIL_PRE_PAY);
            }
        });
    }

    @OnClick({R.id.pay_in_advance, R.id.extraction_in_advance})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.pay_in_advance:
                intent = new Intent(this, PreMoneyCun.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                break;
            case R.id.extraction_in_advance:
                intent = new Intent(this, PreMoneyQu.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_PRE_PAY) {
            finish();;
        }
    }

    public void onResume(){
        super.onResume();
        initDatas();
    }

}
