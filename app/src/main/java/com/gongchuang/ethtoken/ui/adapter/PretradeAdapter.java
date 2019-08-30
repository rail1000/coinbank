package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;
import android.view.View;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.CommonAdapter;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.domain.PreDeposit;
import com.gongchuang.ethtoken.domain.TransactionRecord;

import java.util.List;

public class PretradeAdapter extends CommonAdapter<PreDeposit> {
    public PretradeAdapter(Context context, List<PreDeposit> transactions, int layoutId) {
        super(context, transactions, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, PreDeposit preDeposit) {
        holder.setText(R.id.tv_tx_hash,"交易哈希:"+preDeposit.getHash());
        holder.setText(R.id.tv_amount,"金额:"+preDeposit.getPreamount());
        holder.setText(R.id.tv_message_time,preDeposit.getExtractime());
    }
}
