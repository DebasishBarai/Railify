package com.arcticwolflabs.railify.base.netapi.retrofit;

import android.content.Context;
import android.util.Log;

import com.arcticwolflabs.railify.base.dynamics.Availability;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.netapi.retrofit.apiInterface.retrofit_avl_api;
import com.arcticwolflabs.railify.core.CoreSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitAvailabiltyAPI {

    Context context;
    CoreSystem core_system;
    public retrofit_avl_api apiInterface;

    public RetrofitAvailabiltyAPI(Context _context, CoreSystem _core_system) {
        context=_context;
        core_system=_core_system;
        this.apiInterface = AoiAvlClient.getClient().create(retrofit_avl_api.class);
    }

    private String getAvlData (Journey.JourneyMinimal jrny, String quotatype, Calendar cal, int date_add){

        final String[] res = {""};
        String train_no=String.format("%05d", jrny.getTrain_num())+"+-+"+(((jrny.getTrain_name()).trim()).replaceAll("( +) ", " ")).replaceAll(" ", "+");
        train_no=train_no.toUpperCase();
        String src_stn=core_system.getStationNameFromCode(jrny.getFrom_id()).replaceAll(" ","+")+"+-+"+jrny.getFrom_id();
        src_stn=src_stn.toUpperCase();
        String dst_stn=core_system.getStationNameFromCode(jrny.getTo_id()).replaceAll(" ","+")+"+-+"+jrny.getTo_id();
        dst_stn=dst_stn.toUpperCase();
        String jrny_class="SL";
        int month=cal.get(Calendar.MONTH) +1;
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int year=cal.get(Calendar.YEAR);
        String date=String.format("%02d", day)+"-"+String.format("%02d", month)+"-"+year;
        String seat="SEAT";
        String lang="en";
        Calendar systemCalendar = Calendar.getInstance();
        Call<ResponseBody> call = apiInterface.loadAvlData(train_no, date, src_stn, dst_stn, jrny_class, quotatype, seat, lang, systemCalendar.getTimeInMillis());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("RESPONSE", "retrofit response "+response.body());
                String resource = "";
                try {
                    resource = response.body().string();
                    res[0] =resource;
                } catch (IOException e) {
                    Log.d("RESPONSE", "retrofit response fail "+response.body());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RESPONSE ERROR", t.getMessage() + "");
                call.cancel();
            }
        });

        return res[0];
    }

    private ArrayList<Availability> parse_result(String res){
        return new ArrayList<Availability>();
    }

    public ArrayList<Availability> query(Journey.JourneyMinimal jrny, String quotatype, Calendar cal, int date_add) {
        return parse_result(getAvlData(jrny, quotatype, cal, date_add));
    }
}
