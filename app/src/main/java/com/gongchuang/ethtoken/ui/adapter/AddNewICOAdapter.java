package com.gongchuang.ethtoken.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.ViewHolder;
import com.gongchuang.ethtoken.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwq on 2018/3/16/016.
 * e-mail:lomapa@163.com
 */

public class AddNewICOAdapter extends BaseAdapter{
    private Context mContext;
    private int layoutId;
    private List<String> datas;
    private  List<String> newKinds = new ArrayList<>();
    private List<String> foreKinds;

    public AddNewICOAdapter(Context context, List<String> datas,List<String> foreKinds,int layoutId) {
        this.mContext = context;
        this.layoutId = layoutId;
        this.datas = datas;
        this.foreKinds = foreKinds;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<String> getKinds() {
        return newKinds;
    }
    public boolean contains(List<String> Kinds,String kind) {
        for (String temp : Kinds) {
            if (kind.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = ViewHolder.get(mContext, convertView, parent,layoutId, position);
        if (position == 0) {
            holder.getView(R.id.lly_item).setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.setImageResource(R.id.civ_ico_logo, R.mipmap.b1);
            holder.setText(R.id.tv_ico_name, datas.get(position));
            holder.setVisible(R.id.add_switch, false);
        } else {
            LinearLayout lly_item = holder.getView(R.id.lly_item);
            lly_item.setBackgroundColor(mContext.getResources().getColor(R.color.add_property_gray_bg_color));
            holder.setVisible(R.id.add_switch, true);
            switch (position) {
                case 1:
                    holder.setText(R.id.tv_ico_name, datas.get(position));
                    holder.setImageResource(R.id.civ_ico_logo, R.mipmap.b2);
                    if(contains(foreKinds,"港元")) {
                        //newKinds.add("港元");
                        holder.setVisible(R.id.add_switch, false);
                    }
                    break;
                case 2:
                    holder.setText(R.id.tv_ico_name, datas.get(position));
                    holder.setImageResource(R.id.civ_ico_logo, R.mipmap.b3);
                    if(contains(foreKinds,"美元")) {
                        //newKinds.add("美元");
                        holder.setVisible(R.id.add_switch, false);
                    }
                    break;
                case 3:
                    holder.setText(R.id.tv_ico_name, datas.get(position));
                    holder.setImageResource(R.id.civ_ico_logo, R.mipmap.b4);
                    if(contains(foreKinds,"英镑")) {
                       // newKinds.add("英镑");
                        holder.setVisible(R.id.add_switch, false);
                    }
                    break;
                case 4:
                    holder.setText(R.id.tv_ico_name, datas.get(position));
                    holder.setImageResource(R.id.civ_ico_logo, R.mipmap.b5);
                    if(contains(foreKinds,"澳元")) {
                       // newKinds.add("澳元");
                        holder.setVisible(R.id.add_switch, false);
                    }
                    break;
            }
        }
        holder.setOnClickListener(R.id.add_switch,new View.OnClickListener() {
            int mPosition = holder.getPosition();
            @Override
            public void onClick(View view) {
                //Todo  写个对话框，开通资产功能不可撤销，确定？取消
                switch (mPosition) {
                    case 1:
                        newKinds.add("港元");
                        break;
                    case 2:
                        newKinds.add("美元");
                        break;
                    case 3:
                        newKinds.add("英镑");
                        break;
                    case 4:
                        newKinds.add("澳元");
                        break;
                }
            }
        });
        return holder.getConvertView();
    }


}
