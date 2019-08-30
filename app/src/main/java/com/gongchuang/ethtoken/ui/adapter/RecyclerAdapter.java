package com.gongchuang.ethtoken.ui.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gongchuang.ethtoken.R;

import java.util.List;

/**
 * Created by dwq on 2018/3/13/013.
 * e-mail:lomapa@163.com
 */

public class RecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    List<String> data;
    String balance;

    public RecyclerAdapter(int layoutResId, @Nullable List<String> data,String balance) {
        super(layoutResId, data);
        this.data = data;
        this.balance = balance;
    }

    @Override
    protected void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.tv_ico_name,item);
        int i = holder.getAdapterPosition();
        if(i==1){
            holder.setText(R.id.tv_property_usd,balance);
        }
    }

}
