package com.gongchuang.ethtoken.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.TransactionRecord;
import com.gongchuang.ethtoken.ui.adapter.TradeAdapter;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gyf.barlibrary.ImmersionBar;

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
 * Created by dwq on 2018/3/19/019.
 * e-mail:lomapa@163.com
 */

public class PropertyDetailActivity extends BaseActivity {

    @BindView(R.id.assets_kind)
    TextView tv_assets_kind;
    @BindView(R.id.lv_transaction_records)
    ListView lv_transaction_records;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    private static final String TAG = "PropertyDetailActivity";

    private TradeAdapter drawerWalletAdapter;
    private String wallet_adress;
    private TokenApiService service;

    @Override
    public int getLayoutId() {
        return R.layout.activity_property_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        Bundle bundle = this.getIntent().getExtras();
        tv_assets_kind.setText(bundle.getString("assets_kinds"));
        tv_amount.setText(WalletDaoUtils.getCurrent().getBanlance());
        wallet_adress = WalletDaoUtils.getCurrent().getAddress();
        initRecordShow();
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
    }

    @OnClick({R.id.lly_back, R.id.lly_transfer, R.id.lly_gathering})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.lly_transfer:
                intent = new Intent(mContext, ETCTransferActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_gathering:
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                intent.putExtra("walletAddress",wallet_adress);
                startActivity(intent);
                break;
        }
    }

    public void initRecordShow(){
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        String token = myPreference.getString("TOKEN",null);
        try {
            getCurrentWalletRecentTransaction(wallet_adress,token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentWalletRecentTransaction(String walletAdress,String token) throws JSONException {
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
        root.put("Address",walletAdress);
        root.put("Token",token);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.getCurrentWalletRecentTransaction(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        String jsonString = response.body().string();
                        parsetWalletRecentTx(jsonString);
                    } catch (JSONException je) {
                        Log.e(TAG, "Failed to parse JSON"+je);
                    } catch (IOException ioe) {
                        Log.e(TAG, "Failed to fetch items"+ioe);
                    }
                }else{
                    Log.i(TAG, "response.code()="+response.code());
                    Toast.makeText(mContext, "返回近期交易记录失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return;
    }

    private void parsetWalletRecentTx(String jsonString) throws IOException, JSONException {
        LogUtils.i("PropertyDetailActivity","近期交易记录"+jsonString);
        JSONObject jsonBody = new JSONObject(jsonString);
        JSONArray jsonArray = jsonBody.getJSONArray("transactions");

        final List<TransactionRecord> transactionRecords = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            String vin=jsonObject2.getString("vin");
            String vout=jsonObject2.getString("vout");
            String type;
            if(vin.equals(wallet_adress)){
                type = "-";
            }else{
                type = "+";
            }
            TransactionRecord transactionRecord = new TransactionRecord(jsonObject2.getString("TxSN"),jsonObject2.getString("txHash"),type,vin,vout,jsonObject2.getString("amount"),jsonObject2.getString("memo"));
            transactionRecords.add(transactionRecord);
        }

        drawerWalletAdapter = new TradeAdapter(this,transactionRecords, R.layout.list_item_trade_center);
        lv_transaction_records.setAdapter(drawerWalletAdapter);

        lv_transaction_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PropertyDetailActivity.this, TransactionDetailActivity.class);
                TransactionRecord transaction = transactionRecords.get(position);
                intent.putExtra("transactionHash", transaction.getTxHash());
                intent.putExtra("transactionDate", transaction.getTxDate());
                intent.putExtra("transactionAmount", transaction.getType()+" "+ transaction.getAmount());
                intent.putExtra("transactionAddressFrom", transaction.getAddressFrom());
                intent.putExtra("transactionAddressTo", transaction.getAddressTo());
                intent.putExtra("transactionNote", transaction.getNote());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

}
