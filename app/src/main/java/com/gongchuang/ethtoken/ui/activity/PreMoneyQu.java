package com.gongchuang.ethtoken.ui.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.domain.PreDeposit;
import com.gongchuang.ethtoken.ui.adapter.PretradeAdapter;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gyf.barlibrary.ImmersionBar;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class PreMoneyQu  extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.lv_extract_records)
    ListView lv_extract_records;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    private PretradeAdapter drawerWalletAdapter;
    List<PreDeposit> preDeposits = new ArrayList<>();



    @Override
    public int getLayoutId() {
        return R.layout.activity_pre_money_qu;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.pre_money_qu);
        ivBtn.setVisibility(View.VISIBLE);
        rlBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initDatas() {
        preDeposits = TokenApi.extract_money_record(WalletDaoUtils.getCurrent().getAddress());
        drawerWalletAdapter = new PretradeAdapter(this,preDeposits, R.layout.list_item_trade_center);
        lv_extract_records.setAdapter(drawerWalletAdapter);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
        lv_extract_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PreMoneyQu.this, TransactionDetailActivity.class);
                PreDeposit preDeposit = preDeposits.get(position);
                intent.putExtra("transactionHash", preDeposit.getHash());
                intent.putExtra("transactionDate", preDeposit.getPredemo());
                intent.putExtra("transactionAmount", preDeposit.getPreamount());
                intent.putExtra("transactionAddressFrom", preDeposit.getPreaddress_from());
                intent.putExtra("transactionAddressTo", preDeposit.getPreaddress_to());
                intent.putExtra("transactionNote", preDeposit.getPredemo());
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .navigationBarColor(R.color.white)
                .init();
    }
}
