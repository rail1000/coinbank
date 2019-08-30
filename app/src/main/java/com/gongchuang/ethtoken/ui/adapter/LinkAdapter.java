package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;
import android.view.View;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.CommonAdapter;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.domain.TransactionRecord;

import java.util.List;

public class LinkAdapter extends CommonAdapter<PubBlockHeadInfo> {
    public LinkAdapter(Context context, List<PubBlockHeadInfo> pubBlockHeadInfos, int layoutId) {
        super(context, pubBlockHeadInfos, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, PubBlockHeadInfo pubBlockHeadInfo) {
        holder.setText(R.id.tv_BlockHeight,pubBlockHeadInfo.getPubBlockHeight().toString());
        holder.setText(R.id.tv_pubHeadHash,pubBlockHeadInfo.getPubBlockHash());
        holder.setText(R.id.tv_link_time,pubBlockHeadInfo.getPubMerkleRoot());
    }
}
