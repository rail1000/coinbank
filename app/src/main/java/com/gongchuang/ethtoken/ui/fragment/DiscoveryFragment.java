package com.gongchuang.ethtoken.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseFragment;
import com.gongchuang.ethtoken.ui.activity.PreMoneyAdd;
import com.gongchuang.ethtoken.ui.adapter.DiscoveryNewsAdapter;
import com.gongchuang.ethtoken.utils.GlideImageLoader;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dwq on 2018/3/13/013.
 * e-mail:lomapa@163.com
 */

public class DiscoveryFragment extends BaseFragment {
    @BindView(R.id.lv_discovery)
    ListView lvDiscovery;
    private List<Integer> images;
    private Banner banner;
    private List<String> strings;
    private RecyclerView rvApplication;
    private LinearLayoutManager linearLayoutManager;
    private DiscoveryApplicationAdapter discoveryApplicationAdapter;
    private DiscoveryNewsAdapter discoveryNewsAdapter;
    private View headerView;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_discovery;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        images = new ArrayList<>();
        images.add(R.mipmap.banner1);
        images.add(R.mipmap.banner2);
        images.add(R.mipmap.banner3);
        initHeaderView();
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader(R.drawable.place_img_shape));
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //设置布局管理器
        rvApplication.setLayoutManager(linearLayoutManager);
        strings = new ArrayList<>();
        strings.add(this.getResources().getString(R.string.discovery1));
        strings.add(this.getResources().getString(R.string.discovery2));
        strings.add(this.getResources().getString(R.string.discovery3));
        strings.add(this.getResources().getString(R.string.discovery4));
        strings.add(this.getResources().getString(R.string.discovery5));
        discoveryApplicationAdapter = new DiscoveryApplicationAdapter(strings);
        rvApplication.setAdapter(discoveryApplicationAdapter);

        //listview的绑定holder和adapter在另外的java中
        discoveryNewsAdapter = new DiscoveryNewsAdapter(getContext(), R.layout.list_item_discovery, strings);
        lvDiscovery.setAdapter(discoveryNewsAdapter);
        lvDiscovery.addHeaderView(headerView);
    }

    //设置DiscoveryApplicationHolder
    public class DiscoveryApplicationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView civ_wallet_logo;
        private TextView wallet_func_name;
        int positon;

        public DiscoveryApplicationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_application, parent, false));
            itemView.setOnClickListener(this);
            civ_wallet_logo = (ImageView) itemView.findViewById(R.id.civ_wallet_logo);
            wallet_func_name = (TextView) itemView.findViewById(R.id.wallet_func_name);
        }

        public void bind(String funcName, int picPosition) {
            positon = picPosition;
            switch (positon) {
                case 0:
                    civ_wallet_logo.setImageResource(R.mipmap.func1);
                    break;
                case 1:
                    civ_wallet_logo.setImageResource(R.mipmap.func2);
                    break;
                case 2:
                    civ_wallet_logo.setImageResource(R.mipmap.func3);
                    break;
                case 3:
                    civ_wallet_logo.setImageResource(R.mipmap.func4);
                    break;
                case 4:
                    civ_wallet_logo.setImageResource(R.mipmap.func5);
                    break;
            }
            wallet_func_name.setText(funcName);
        }

        @Override
        public void onClick(View view) {
            if (positon == 0) {
                Intent intent = new Intent(getActivity(), PreMoneyAdd.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "点击功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 设置DiscoveryApplicationAdapter
    public class DiscoveryApplicationAdapter extends RecyclerView.Adapter<DiscoveryApplicationHolder>{
        public List<String> mStrings;
        public DiscoveryApplicationAdapter(List<String> strings){
            mStrings = strings;
        }

        @Override
        //创建视图
        public DiscoveryApplicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DiscoveryApplicationHolder(layoutInflater,parent);
        }

        @Override
        //数据填充
        public void onBindViewHolder(DiscoveryApplicationHolder holder, int position) {
            String string = mStrings.get(position);
            holder.bind(string,position);
        }

        @Override
        //首先得到条数
        public int getItemCount() {
            return mStrings.size();
        }
    }


    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.discovery_list_header, null);
        banner = headerView.findViewById(R.id.banner);
        rvApplication = headerView.findViewById(R.id.rv_application);

    }

    @Override
    public void configViews() {

    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

}
