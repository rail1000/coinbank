package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.ui.adapter.AddNewICOAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dwq on 2018/3/16/016.
 * e-mail:lomapa@163.com
 */

public class AddNewPropertyActivity extends BaseActivity {
    private static final int SEARCH_ICO_TOKEN_REQUEST = 1000;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_ico)
    ListView lvIco;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    private List<String> strings;
    private AddNewICOAdapter addNewICOAdapter;
    private  List<String>  newkinds;
    private List<String> foreKinds = new ArrayList<>(); {
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_new_property;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
        rlBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initDatas() {
        strings = new ArrayList<>();
        strings.add(this.getResources().getString(R.string.assetkind1));
        strings.add(this.getResources().getString(R.string.assetkind2));
        strings.add(this.getResources().getString(R.string.assetkind3));
        strings.add(this.getResources().getString(R.string.assetkind4));
        strings.add(this.getResources().getString(R.string.assetkind5));

        SharedPreferences myPreference = getSharedPreferences("assetsPreference", Context.MODE_PRIVATE);
        if(myPreference.contains("港元")){ foreKinds.add("港元"); }
        if(myPreference.contains("美元")){ foreKinds.add("美元"); }
        if(myPreference.contains("英镑")){ foreKinds.add("英镑"); }
        if(myPreference.contains("澳元")){ foreKinds.add("澳元"); }
        addNewICOAdapter = new AddNewICOAdapter(this, strings,foreKinds, R.layout.list_item_add_ico_property);
        newkinds = addNewICOAdapter.getKinds();
        lvIco.setAdapter(addNewICOAdapter);
    }

    @Override
    public void callBack() {
        LinearLayout llyBack = (LinearLayout) findViewById(R.id.lly_back);
        if (llyBack != null) {
            llyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("newKinds", (ArrayList<String>) newkinds);
                    setResult(1102, intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void configViews() {

    }
}
