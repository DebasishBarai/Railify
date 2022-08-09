package com.arcticwolflabs.railify.base.netapi.retrofit.apiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface retrofit_running_api {
    @GET("ntes/NTES")
    Call<ResponseBody> loadRunningData(@Query("action") String action, @Query("trainNo") String num, @Query("t") Long time);
}