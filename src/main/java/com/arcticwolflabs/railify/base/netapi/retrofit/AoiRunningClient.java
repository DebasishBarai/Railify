package com.arcticwolflabs.railify.base.netapi.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;

public class AoiRunningClient {


    private static Retrofit retrofit = null;

    static Retrofit getClient() {


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://enquiry.indianrail.gov.in")
                .build();

        return retrofit;
    }

}
