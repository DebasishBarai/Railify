package com.arcticwolflabs.railify.base.netapi.retrofit.apiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface retrofit_avl_api {
    @GET("CommonCaptcha")
    Call<ResponseBody> loadAvlData(@Query(value="trainNo", encoded = true) String train_no, @Query("dt") String date, @Query(value="sourceStation", encoded = true) String src_stn, @Query(value="destinationStation",encoded = true) String dst_stn, @Query("classc") String jrny_class, @Query("quota") String jrny_quota, @Query("inputPage") String seat, @Query("language") String lang, @Query("_") Long time);

}