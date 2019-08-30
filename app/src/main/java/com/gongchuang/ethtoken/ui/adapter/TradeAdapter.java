package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;
import android.view.View;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.CommonAdapter;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.domain.TransactionRecord;

import java.util.List;

public class TradeAdapter extends CommonAdapter<TransactionRecord> {
    public TradeAdapter(Context context, List<TransactionRecord> transactions, int layoutId) {
        super(context, transactions, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, TransactionRecord transactionRecord) {
        holder.setText(R.id.tv_tx_hash,"交易哈希:"+transactionRecord.getTxHash());
        holder.setText(R.id.tv_amount,"金额:"+transactionRecord.getType()+" "+transactionRecord.getAmount());
        holder.setText(R.id.tv_message_time,transactionRecord.getTxDate());
    }
}
