package com.gongchuang.ethtoken.ui.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.base.BaseFragment;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.ETHWalletDao;
import com.gongchuang.ethtoken.event.DeleteWalletSuccessEvent;
import com.gongchuang.ethtoken.ui.activity.AddNewPropertyActivity;
import com.gongchuang.ethtoken.ui.activity.BankTopUpActivity;
import com.gongchuang.ethtoken.ui.activity.BankWithdrawActivity;
import com.gongchuang.ethtoken.ui.activity.CreateWalletActivity;
import com.gongchuang.ethtoken.ui.activity.ETCTransferActivity;
import com.gongchuang.ethtoken.ui.activity.GatheringQRCodeActivity;
import com.gongchuang.ethtoken.ui.activity.PropertyDetailActivity;
import com.gongchuang.ethtoken.ui.activity.QRCodeScannerActivity;
import com.gongchuang.ethtoken.ui.activity.WalletDetailActivity;
import com.gongchuang.ethtoken.ui.activity.WalletMangerActivity;
import com.gongchuang.ethtoken.ui.adapter.DrawerWalletAdapter;
import com.gongchuang.ethtoken.ui.adapter.RecyclerAdapter;
import com.gongchuang.ethtoken.ui.contract.PropertyContract;
import com.gongchuang.ethtoken.ui.presenter.PropertyPresenter;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dwq on 2018/3/13/013.
 * e-mail:lomapa@163.com
 */

public class PropertyFragment extends BaseFragment implements View.OnClickListener, PropertyContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.tv_thumb_property)
    TextView tvThumbProperty;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.lv_wallet)
    ListView lvWallet;
    private List<String> strings = new ArrayList<>();
    private List<String> newKinds = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter recyclerAdapter;
    private View headView;

    private int bannerHeight = 300;
    private View mIv;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private static final int QRCODE_SCANNER_REQUEST = 1100;
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int ADD_NEW_PROPERTY_REQUEST = 1102;
    private static final int WALLET_DETAIL_REQUEST = 1104;
    private static final int BANK_TOPUP = 1105;
    private static final int BANK_WITHDRAW = 1106;

    private DrawerWalletAdapter drawerWalletAdapter;
    public static boolean isActive; //全局变量
    private PropertyContract.Presenter mPresenter;
    private TextView tvWalletName;
    private TextView tvWalletAddress;
    private TextView tv_total_amount;
    String current_banlance = null;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_property;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        //更新值
        stringUpdate();
        if(strings.size()==0) {
            strings.add("人民币");
        }else{
            strings.addAll(newKinds);
        }
        //删除重复值
        strings = removeDuplicateWithOrder(strings);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //设置布局管理器
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        ETHWallet ethWallet = WalletDaoUtils.getCurrent();
        if(ethWallet==null){
            current_banlance = "0";
        }else{
            current_banlance = WalletDaoUtils.getCurrent().getBanlance();
        }
        recyclerAdapter = new RecyclerAdapter(R.layout.list_item_property, strings,current_banlance);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("assets_kinds", strings.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mPresenter = new PropertyPresenter(this);
        mPresenter.loadAllWallets();
    }

    private void initTinklingLayoutListener() {
        ViewGroup.LayoutParams bannerParams = mIv.getLayoutParams();
        ViewGroup.LayoutParams titleBarParams = mToolbar.getLayoutParams();
        bannerHeight = bannerParams.height - titleBarParams.height - ImmersionBar.getStatusBarHeight(getActivity());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                ImmersionBar immersionBar = ImmersionBar.with(PropertyFragment.this)
                        .addViewSupportTransformColor(mToolbar, R.color.colorPrimary);
                if (totalDy <= bannerHeight) {
                    float alpha = (float) totalDy / bannerHeight;
                    immersionBar.statusBarAlpha(alpha)
                            .init();
                    // 设置资产文字alpha值
                    if (totalDy >= bannerHeight / 2) {
                        float tvPropertyAlpha = (float) (totalDy - bannerHeight / 2) / (bannerHeight / 2);
                        tvThumbProperty.setAlpha(tvPropertyAlpha);
                        int top = (int) (mToolbar.getHeight() - mToolbar.getHeight() * alpha);
                        tvThumbProperty.setPadding(0, top, 0, 0);
                    } else {
                        tvThumbProperty.setPadding(0, mToolbar.getHeight(), 0, 0);
                        tvThumbProperty.setAlpha(0);
                    }
                } else {
                    immersionBar.statusBarAlpha(1.0f)
                            .init();
                    tvThumbProperty.setAlpha(1.0f);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        mToolbar.setVisibility(View.VISIBLE);
                        ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(false).init();
                    }
                }, 2000);
            }

            @Override
            public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
                mToolbar.setVisibility(View.GONE);
                ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(true).init();
            }

            @Override
            public void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
                if (Math.abs(fraction - 1.0f) > 0) {
                    mToolbar.setVisibility(View.VISIBLE);
                    ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(false).init();
                } else {
                    mToolbar.setVisibility(View.GONE);
                    ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(true).init();
                }
            }
        });
    }

    private void addHeaderView() {
        ProgressLayout headerView = new ProgressLayout(getContext());
        refreshLayout.setHeaderView(headerView);
        headView = LayoutInflater.from(getContext()).inflate(R.layout.list_header_item, (ViewGroup) recyclerView.getParent(), false);
        mIv = headView.findViewById(R.id.iv);
        headView.findViewById(R.id.lly_add_ico).setOnClickListener(this);
        headView.findViewById(R.id.lly_wallet_address).setOnClickListener(this);
        headView.findViewById(R.id.civ_wallet_logo).setOnClickListener(this);
        tv_total_amount = headView.findViewById(R.id.tv_total_amount);//钱包总额
        tvWalletName = headView.findViewById(R.id.tv_wallet_name);
        tvWalletAddress = headView.findViewById(R.id.tv_wallet_address);
        recyclerAdapter.addHeaderView(headView);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
        configViews();
        ImmersionBar.with(this)
                .titleBar(mToolbar, false)
                .navigationBarColor(R.color.colorPrimary)
                .init();
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
//            Log.i("ACTIVITY", "程序从后台唤醒");
            // 刷新列表
        }
    }

    @Override
    public void configViews() {
        addHeaderView();
        initTinklingLayoutListener();
        drawer.setScrimColor(getContext().getResources().getColor(R.color.property_drawer_scrim_bg_color));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @OnClick({R.id.lly_menu, R.id.lly_qrcode_scanner, R.id.lly_create_wallet,R.id.lly_in_wallet,R.id.lly_out_wallet})
    public void onClick(View view) {
        Intent intent = null;
        ETHWallet wallet = null;
        switch (view.getId()) {
            case R.id.lly_menu:
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_in_wallet://充值
                intent = new Intent(mContext, BankTopUpActivity.class);
                startActivityForResult(intent, BANK_TOPUP);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_out_wallet://提现
                intent = new Intent(mContext, BankWithdrawActivity.class);
                startActivityForResult(intent, BANK_WITHDRAW);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_qrcode_scanner:// 二维码扫描
                intent = new Intent(mContext, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_create_wallet:// 创建钱包
                intent = new Intent(mContext, CreateWalletActivity.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_add_ico:// 跳转添加资产
                intent = new Intent(mContext, AddNewPropertyActivity.class);
                startActivityForResult(intent, ADD_NEW_PROPERTY_REQUEST);
                break;
            case R.id.lly_wallet_address:// 跳转收款码
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                wallet = WalletDaoUtils.getCurrent();
                if (wallet == null) {
                    return;
                }
                intent.putExtra("walletAddress", wallet.getAddress());
                startActivity(intent);
                break;
            case R.id.civ_wallet_logo:// 跳转钱包详情
                intent = new Intent(mContext, WalletDetailActivity.class);
                wallet = WalletDaoUtils.getCurrent();
                if (wallet == null) {
                    return;
                }
                intent.putExtra("walletId", wallet.getId());
                intent.putExtra("walletPwd", wallet.getPassword());
                intent.putExtra("walletAddress", wallet.getAddress());
                intent.putExtra("walletName", wallet.getName());
                intent.putExtra("walletMnemonic", wallet.getMnemonic());
                intent.putExtra("walletIsBackup", wallet.getIsBackup());
                startActivityForResult(intent, WALLET_DETAIL_REQUEST);
                break;

        }

    }

    // 打开关闭DrawerLayout
    private void openOrCloseDrawerLayout() {
        boolean drawerOpen = drawer.isDrawerOpen(Gravity.END);
        if (drawerOpen) {
            drawer.closeDrawer(Gravity.END);
        } else {
            drawer.openDrawer(Gravity.END);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                Intent intent = new Intent(getActivity(),ETCTransferActivity.class);
                intent.putExtra("scan_result",scanResult);
                startActivity(intent);
            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
                mPresenter.loadAllWallets();
                startActivity(new Intent(mContext, WalletMangerActivity.class));
            }
        }else if(requestCode == ADD_NEW_PROPERTY_REQUEST){
            if (data != null) {
                newKinds = data.getStringArrayListExtra("newKinds");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        mPresenter.loadAllWallets();
        showWallet(wallet);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(DeleteWalletSuccessEvent deleteWalletSuccessEvent) {
        WalletDaoUtils.setCurrentAfterDelete();
        mPresenter.loadAllWallets();
    }


    @Override
    public void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
//            Log.i("ACTIVITY", "程序进入后台");
        }
        super.onStop();
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager
                    .getRunningAppProcesses();
        }
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void showWallet(ETHWallet wallet) {
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getAddress());
        tv_total_amount.setText(current_banlance);
    }

    @Override
    public void showDrawerWallets(final List<ETHWallet> ethWallets) {
        for (int i = 0; i < ethWallets.size(); i++) {
            LogUtils.i("PropertyFragment", "Ethwallets" + ethWallets.get(i).toString());
        }
        drawerWalletAdapter = new DrawerWalletAdapter(getContext(), ethWallets, R.layout.list_item_drawer_property_wallet);
        lvWallet.setAdapter(drawerWalletAdapter);
        lvWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.switchCurrentWallet(position, ethWallets.get(position).getId());
                openOrCloseDrawerLayout();
            }
        });
    }

    @Override
    public void switchWallet(int currentPosition, ETHWallet wallet) {
        drawerWalletAdapter.setCurrentWalletPosition(currentPosition);
        openOrCloseDrawerLayout();
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getAddress());
        current_banlance = wallet.getBanlance();
        tv_total_amount.setText(current_banlance);
        initDatas();
        configViews();
    }

    @Override
    public void showCurrentWalletProrperty(String balance) {

    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, state);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    private List<String> removeDuplicateWithOrder(List<String> list) {
        for(int i=0;i<5;i++){
            for(int j=i+1;j<list.size();j++){
                if(list.get(i).equals(list.get(j))){
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }


    private void stringUpdate() {
        //传入值&添加选定值
        SharedPreferences myPreference = mContext.getSharedPreferences("assetsPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreference.edit();
        //存入键值对数据，注意此处的put[type]("key",value);
        for (int i = 0; i < newKinds.size(); i++) {
            editor.putString(newKinds.get(i), "1");
        }
        editor.apply();
        if (myPreference.contains("港元")) {
            newKinds.add("港元");
        }
        if (myPreference.contains("美元")) {
            newKinds.add("美元");
        }
        if (myPreference.contains("英镑")) {
            newKinds.add("英镑");
        }
        if (myPreference.contains("澳元")) {
            newKinds.add("澳元");
        }
    }
}
