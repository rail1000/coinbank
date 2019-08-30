package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;
import android.view.View;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.CommonAdapter;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.domain.TransactionRecord;

import java.util.List;

public class LinkAdapterPer extends CommonAdapter<PerBlockHeadInfo> {
    public LinkAdapterPer(Context context, List<PerBlockHeadInfo> perBlockHeadInfos, int layoutId) {
        super(context, perBlockHeadInfos, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, PerBlockHeadInfo perBlockHeadInfos) {
        holder.setText(R.id.tv_BlockHeight,perBlockHeadInfos.getPerBlockHeight().toString());
        holder.setText(R.id.tv_pubHeadHash,perBlockHeadInfos.getPerBlockHash());
        holder.setText(R.id.tv_link_time,perBlockHeadInfos.getPerBlockdate());
    }
}
