package com.gongchuang.ethtoken.api;

import java.math.BigInteger;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dwq on 2018/3/13/013.
 * e-mail:lomapa@163.com
 */

public interface TokenApiService {
    //请求登录
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/login")
    Call<ResponseBody> getLogin(@Body RequestBody body);

    //虚拟注册
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/newVirAccount")
    Call<ResponseBody> getVirtual(@Body RequestBody body);

    // 请求所有钱包的余额
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/getAllBalance")
    Call<ResponseBody> getWalletBalance(@Body RequestBody body);

    //交易请求
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/newsTx")
    Call<ResponseBody> putTransaction(@Body RequestBody body);

    //充值
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/newCoinBase")
    Call<ResponseBody> newCoinBase(@Body RequestBody body);

    //提现
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/newCoinOutBase")
    Call<ResponseBody> newCoinOutBase(@Body RequestBody body);

    //请求当前钱包的交易记录（最近50条）
    @POST("user/getMyTx")
    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    Call<ResponseBody> getCurrentWalletRecentTransaction(@Body RequestBody body);

    @Headers({
            "Accept: application/json",
    })
    @POST("user/updateMainChain")
    Call<ResponseBody> getPubBlockHeadInfo(@Query("height") BigInteger height);

    @Headers({
            "Accept: application/json",
    })
    @POST("user/updateIMainChain")
    Call<ResponseBody> getPerBlockHeadInfo(@Body RequestBody body);


    @Headers({
            "Accept: application/json",
            "content-type: application/json"
    })
    @POST("user/checkTX")
    Call<ResponseBody> checkTX(@Body RequestBody body);

}
