package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.event.LoadWalletSuccessEvent;
import com.gongchuang.ethtoken.ui.adapter.HomePagerAdapter;
import com.gongchuang.ethtoken.ui.fragment.DiscoveryFragment;
import com.gongchuang.ethtoken.ui.fragment.MineFragment;
import com.gongchuang.ethtoken.ui.fragment.PropertyFragment;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.PerBlockHeadUtils;
import com.gongchuang.ethtoken.utils.PubBlockHeadUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.view.NoScrollViewPager;
import com.icbc.api.internal.util.internal.util.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.vp_home)
    NoScrollViewPager vpHome;
    @BindView(R.id.iv_mall)
    ImageView ivMall;
    @BindView(R.id.tv_mall)
    TextView tvMall;
    @BindView(R.id.lly_mall)
    LinearLayout llyMall;
    @BindView(R.id.iv_news)
    ImageView ivNews;
    @BindView(R.id.tv_news)
    TextView tvNews;
    @BindView(R.id.lly_news)
    LinearLayout llyNews;
    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lly_mine)
    LinearLayout llyMine;

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;
    private static final String TAG = "MainActivity";
    String address=null;
    private HomePagerAdapter homePagerAdapter;
    private TokenApiService service;
    private List<ETHWallet> wallets = WalletDaoUtils.loadAll();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        // 链头数据库更新
        initBlockHead();
        initBalance();
    }

    @Override
    public void configViews() {
        ivMall.setSelected(true);
        tvMall.setSelected(true);

        llyNews.setOnClickListener(this);
        llyMall.setOnClickListener(this);
        llyMine.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(10);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PropertyFragment());
        fragmentList.add(new DiscoveryFragment());
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall:// 商场
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                break;
            case R.id.lly_news:// 资讯
                ivNews.setSelected(true);
                tvNews.setSelected(true);
                vpHome.setCurrentItem(1, false);
                break;
            case R.id.lly_mine:// 我的
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(2, false);
                break;
        }
    }

    private void setAllUnselected() {
        ivNews.setSelected(false);
        tvNews.setSelected(false);
        ivMall.setSelected(false);
        tvMall.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        vpHome.setCurrentItem(0, false);
        setAllUnselected();
        ivMall.setSelected(true);
        tvMall.setSelected(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(LoadWalletSuccessEvent loadWalletSuccessEvent) {
        startActivity(new Intent(this, WalletMangerActivity.class));
        vpHome.setCurrentItem(0, false);
        setAllUnselected();
        ivMall.setSelected(true);
        tvMall.setSelected(true);
    }


    //数据API部分
    public void initBlockHead(){
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String token = myPreference.getString("TOKEN",null);
        Long perHeight = PerBlockHeadUtils.loadRecentPerHeight();
        Long pubHeight = PubBlockHeadUtils.loadRecentPubHeight();
        try {
            if(WalletDaoUtils.getCurrent()!=null){
                address = WalletDaoUtils.getCurrent().getAddress();
                downloadPerBlock(perHeight.toString(),address,token);
            }
            downloadPubBlock(pubHeight.toString(),token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initBalance(){
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String token = myPreference.getString("TOKEN",null);
        String username = myPreference.getString("USER_NAME",null);
        try {
            getAllWalletBalance(username,token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTokenApi() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .client(builder.build())
                .build();
        service = retrofit.create(TokenApiService.class);
    }

    public void getAllWalletBalance(String username, String token) throws JSONException {
        if(wallets.size()!=0||(wallets!=null)){
            getTokenApi();
            JSONObject root = new JSONObject();
            root.put("Token",token);
            root.put("User",username);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
            Call<ResponseBody> call = service.getWalletBalance(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            String jsonString = response.body().string();
                            parseAllWalletBalance(jsonString);
                        } catch (JSONException je) {
                            Log.i(TAG, "返回所有钱包余额失败!JSON");
                        } catch (IOException ioe) {
                            Log.i(TAG, "返回所有钱包余额失败！IO");
                        }
                    }else{
                        Log.i(TAG, "response.code()="+response.code());
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        return;
    }

    public void downloadPubBlock(String height,String token) throws JSONException {
        getTokenApi();
        Call<ResponseBody> call = service.getPubBlockHeadInfo(new BigInteger("0"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String jsonString = response.body().string();
                        parsetPubBlock(jsonString);
                        Log.i(TAG, " downloadPubBlock sucessed！");
                    } catch (JSONException je) {
                        Log.i(TAG, "下载公共链条失败Json");
                    } catch (IOException ioe) {
                        Log.i(TAG, "下载公共链条失败IO");
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void downloadPerBlock(String height,String adress, String token) throws JSONException {
        getTokenApi();
        JSONObject root = new JSONObject();
        root.put("Address",adress);
        root.put("Height",height);
        root.put("Token",token);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.getPerBlockHeadInfo(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String jsonString = response.body().string();
                        parsetPerBlock(jsonString);
                        Log.i(TAG, " downloadPerBlock sucessed！");
                    } catch (JSONException je) {
                        Log.i(TAG, "下载个人链条失败Json");
                    } catch (IOException ioe) {
                        Log.i(TAG, "下载个人链条失败IO");
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void parseAllWalletBalance(String jsonString) throws IOException, JSONException {
        LogUtils.i("MainActivity","更新所有钱包余额"+jsonString);
        JSONObject jsonBody = new JSONObject(jsonString).getJSONObject("balance");
        for(int i=0;i<wallets.size();i++){
            WalletDaoUtils.updateWalletBanlance(wallets.get(i).getId(),jsonBody.getString(wallets.get(i).getAddress()));
        }
    }

    private void parsetPubBlock(String jsonString) throws IOException, JSONException {
        try {
        //返回数据Json解析
            LogUtils.i("MainActivity","更新公共链"+jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            JSONArray jsonArray = jsonBody.getJSONObject("blockchain").getJSONArray("block");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PubBlockHeadInfo pubBlockHeadInfo = new PubBlockHeadInfo(Long.valueOf(jsonObject.getString("height")).longValue(),jsonObject.getString("hash"),jsonObject.getString("merkleRoot"));
                PubBlockHeadUtils.insertNewPubBlockHead(pubBlockHeadInfo);
            }
        }catch (SQLiteConstraintException e){}
    }

    private void parsetPerBlock(String jsonString) throws IOException, JSONException {
        try {
            //返回数据Json解析
            LogUtils.i("MainActivity","更新私人链"+jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            JSONArray jsonArray = jsonBody.getJSONObject("blockchainAccount").getJSONArray("block");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                PerBlockHeadInfo perBlockHeadInfo = new PerBlockHeadInfo(Long.valueOf(jsonObject2.getString("height")).longValue(),jsonObject2.getString("hash"),jsonObject2.getString("serialNumber"));
                PerBlockHeadUtils.insertNewPerBlockHead(perBlockHeadInfo);
            }
        }catch (SQLiteConstraintException e){}
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }
}
