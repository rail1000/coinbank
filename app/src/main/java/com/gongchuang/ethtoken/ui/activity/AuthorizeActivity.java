package com.gongchuang.ethtoken.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.api.TokenApi;
import com.gongchuang.ethtoken.api.TokenApiService;
import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthorizeActivity extends Activity {
   private WebView wvShowDetails;
    private ProgressBar pbComplete;
    private String rootUrl = "https://sandbox.apihub.citi.com/gcb/api/authCode/oauth2/authorize?" +
            "response_type=code" +
            "&client_id=f4571479-566a-4ad9-9f84-3d6f79d59cc0" +
            "&scope=accounts_details_transactions payees personal_domestic_transfers internal_domestic_transfers customers_profiles" +
            "&countryCode=HK" +
            "&businessCode=GCB" +
            "&locale=en_US" +
            "&state=12346" +
            "&redirect_uri=http://47.52.71.78:8080/v1/object/getCode";
    /**
     * 花旗账户授权以及获取用户信息
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        if(myPreference.contains("ACCOUNT_NUMBER")){
            LogUtils.i("AuthorizeActivity","非第一次登陆");
            Intent intent = new Intent();
            intent.setClass(AuthorizeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            LogUtils.i("AuthorizeActivity","myPreference.contains(\"ACCONT_NUMBER\")"+myPreference.contains("ACCOUNT_NUMBER"));
            setContentView(R.layout.activity_authorize);
            wvShowDetails = findViewById(R.id.wvShowDetails);
            pbComplete = findViewById(R.id.pbComplete);
            setWebView(rootUrl);
            myOnclick();
        }
    }
    /**
     * 加载webView的方法
     */
    private void setWebView(String url) {
        //      对webView的设置
        WebSettings webSettings = wvShowDetails.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //     设置进度条
        wvShowDetails.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pbComplete.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == pbComplete.getVisibility()) {
                        pbComplete.setVisibility(View.VISIBLE);
                    }
                    pbComplete.setProgress(newProgress);
                }
            }
        });
        /**
         * 拿到当前页面的路径，若是已经重定向则获取code
         */
        wvShowDetails.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String myurl="http://47.52.71.78:8080/v1/object/getCode?code=";
                if(url.contains(myurl)) {
                    String code = url.substring(myurl.length(),url.indexOf("&state"));
                    try {
                        LogUtils.i("AuthorizeActivity","授权码"+code);
                        ToastUtils.showLongToast("授权成功！");
                        logIn(code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                isTitleChange(url);
                return super.shouldOverrideUrlLoading(view, url);
            }


        });
        wvShowDetails.loadUrl(url);

    }


    private void myOnclick(){
//      监听返回键
        wvShowDetails.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && wvShowDetails.canGoBack()) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { //只处理一次
                        myLastUrl();
                    }
                    return true;
                }
                return false;
            }

        });
    }

    /**
     * 拿到上一页的路径
     */
    private  void myLastUrl(){
        WebBackForwardList backForwardList = wvShowDetails.copyBackForwardList();
        if (backForwardList != null && backForwardList.getSize() != 0) {
            //当前页面在历史队列中的位置
            int currentIndex = backForwardList.getCurrentIndex();
            WebHistoryItem historyItem =
                    backForwardList.getItemAtIndex(currentIndex - 1);
            if (historyItem != null) {
                String backPageUrl = historyItem.getUrl();
                wvShowDetails.goBack();
//              重新判断设置标题
                isTitleChange(backPageUrl);
            }
        }
    }

    /**
     * 判断标题是否改变
     */
    private void isTitleChange(String url){
        String myurl="https://sandbox";
        if(url.contains(myurl)) {
//                  包含说明是内页
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int top=dip2px(this,-35f);
            lp.setMargins(0,top,0,0);
            wvShowDetails.setLayoutParams(lp);
        }else{
//                  不包含说明是外面页
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(0,0,0,0);
            wvShowDetails.setLayoutParams(lp);
        }
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //数据API部分
    private void logIn(String code) throws JSONException {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .client(builder.build())
                .build();
        TokenApiService service = retrofit.create(TokenApiService.class);
        JSONObject root = new JSONObject();
        root.put("Code", code);
        root.put("Height", 0);
        root.put("IHeight", 0);
        root.put("Password", "66666");
        root.put("Username", "66666");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
        Call<ResponseBody> call = service.getLogin(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        String jsonString = response.body().string();
                        parselogIn(jsonString);
                        Intent intent = new Intent();
                        intent.setClass(AuthorizeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException je) {
                        ToastUtils.showLongToast("登录信息获取失败J！");
                    } catch (IOException ioe) {
                        ToastUtils.showLongToast("登录信息获取失败I！");
                    }
                }else{
                    Log.i("AuthorizeActivity", "response.code()="+response.code());
                    Toast.makeText(AuthorizeActivity.this, "信息不正确，请重新绑定！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AuthorizeActivity.this, "连接失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parselogIn(String StringjsonBody) throws IOException, JSONException {
        Log.i("AuthorizeActivity", StringjsonBody);

        JSONObject jsonBody = new JSONObject(StringjsonBody);
        JSONObject data  = jsonBody.getJSONObject("data");
        String accountNumeber = data.getString("accountNumeber");
        String userName = data.getString("customername");
        String token = data.getString("token");

        SharedPreferences myPreference = getSharedPreferences("cardPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreference.edit();
        editor.putString("ACCOUNT_NUMBER", accountNumeber);
        editor.putString("USER_NAME", "66666");
        editor.putString("TOKEN", token);
        editor.commit();

    }
}
