package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.domain.TransactionRecord;
import com.gongchuang.ethtoken.ui.adapter.LinkAdapter;
import com.gongchuang.ethtoken.ui.adapter.LinkAdapterPer;
import com.gongchuang.ethtoken.ui.adapter.TradeAdapter;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.PerBlockHeadUtils;
import com.gongchuang.ethtoken.utils.PubBlockHeadUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.utils.bip44.Sha256Hash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by dwq on 2018/3/20/020.
 * e-mail:lomapa@163.com
 */

public class TradingRecordActivity extends BaseActivity {
    private static final int SWITCH_WALLET_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.lv_trading_record)
    ListView lvTradingRecord;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private LinkAdapter drawerWalletAdapter;
    private LinkAdapterPer drawerWalletAdapter2;
    List<PubBlockHeadInfo> pubBlockHeadInfos;
    List<PerBlockHeadInfo> perBlockHeadInfos;
    Boolean if_pub;
    private TokenApiService service;
    private static final String TAG = "TradingRecordActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_trading_record;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.trading_pub_title);
        ivBtn.setImageResource(R.mipmap.ic_acount_switch);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        varify();
        if_pub = true;
        pubBlockHeadInfos = PubBlockHeadUtils.loadAll();
        drawerWalletAdapter = new LinkAdapter(this, pubBlockHeadInfos, R.layout.list_item_link_center);
        lvTradingRecord.setAdapter(drawerWalletAdapter);
    }

    @Override
    public void configViews() {
    }

    public void varify() {
        String adress = WalletDaoUtils.getCurrent().getAddress();
        if(adress!=null){
            initRecordShow();
        }
    }

    @OnClick(R.id.rl_btn)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                if(if_pub){
                    tvTitle.setText(R.string.trading_per_title);
                    perBlockHeadInfos = PerBlockHeadUtils.loadAll();
                    drawerWalletAdapter2 = new LinkAdapterPer(this, perBlockHeadInfos, R.layout.list_item_link_center2);
                    lvTradingRecord.setAdapter(drawerWalletAdapter2);
                    if_pub = false;
                }else{
                    tvTitle.setText(R.string.trading_pub_title);
                    initDatas();
                }
                break;
        }
    }

    public void initRecordShow(){
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String TxSerialNumber = myPreference.getString("TxSerialNumber",null);
        if(TxSerialNumber!=null) {
            try {
                getCurrentTxSerialNumber(TxSerialNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getCurrentTxSerialNumber(String TxSerialNumber) throws JSONException {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .client(builder.build())
                .build();
        service = retrofit.create(TokenApiService.class);

        JSONObject root = new JSONObject();
        root.put("TxSerialNumber",TxSerialNumber);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.checkTX(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonString = response.body().string();
                    parsecheckTX(jsonString);
                } catch (JSONException je) {
                    ToastUtils.showLongToast("返回近期交易记录失败！");
                    Log.e(TAG, "Failed to parse JSON", je);
                } catch (IOException ioe) {
                    ToastUtils.showLongToast("返回近期交易记录失败！");
                    Log.e(TAG, "Failed to fetch items", ioe);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return;
    }

    private void parsecheckTX(String jsonString) throws IOException, JSONException {
        LogUtils.i(TAG, "路径" + jsonString);
        JSONObject jsonBody = new JSONObject(jsonString);
        String height = jsonBody.getString("height");
        JSONArray jsonArray = jsonBody.getJSONArray("merkle check");
        List<String> hashs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String hash = jsonArray.getString(i);
            hashs.add(hash);
        }
        verify(hashs,PubBlockHeadUtils.pubBlockHeadInfos.load(Long.parseLong(height)).getPubBlockHash());
    }

    public void verify(List<String> hashs,String hashTxDownLoad){
        String hashTx = hashs.get(0);
        for (int i = 1; i < hashs.size(); i++) {
            hashTx = Sha256Hash.fromString(hashTx + "" + hashs.get(i)).toString();
        }
        if(hashTx.equals(hashTxDownLoad)){
            ToastUtils.showToast("验证成功！");
        }else {
            ToastUtils.showToast("验证失败！");
        }
    }

}

