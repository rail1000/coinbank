package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.CommonAdapter;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.domain.PreDeposit;

import java.util.List;

/**
 * Created by dwq on 2018/3/19/019.
 * e-mail:lomapa@163.com
 */

public class PreDepositAdapter extends CommonAdapter<PreDeposit> {
    public PreDepositAdapter(Context context, List<PreDeposit> depositList, int layoutId) {
        super(context, depositList, layoutId);
    }


    @Override
    public void convert(ViewHolder holder, PreDeposit preDeposit) {
        holder.setText(R.id.predeposit_name,preDeposit.getHash());
        holder.setText(R.id.predeposit_adress,preDeposit.getPreamount());
        holder.setText(R.id.predeposit_demo,preDeposit.getPredemo());
    }
}
