package com.arcticwolflabs.railify.base.netapi.retrofit;

import android.content.Context;
import android.util.Log;

import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.retrofit.apiInterface.retrofit_running_api;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitRunningStatusApi {
    Context context;
    public retrofit_running_api apiInterface;

    public RetrofitRunningStatusApi(Context context) {
        this.context = context;
        this.apiInterface = AoiRunningClient.getClient().create(retrofit_running_api.class);
    }
    public Train getRunningStatus(CoreSystem coreSystem, Train train, Date start_date) {

        final String[] res = {""};
        Calendar systemCalendar = Calendar.getInstance();
        Log.d("RESPONSE", "retrofit response ");
        Call<ResponseBody> call = apiInterface.loadRunningData("getTrainData", Integer.toString(train.getTrainNum()), systemCalendar.getTimeInMillis());

        //Retrofit asynchronously fetching data
        /*call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("RESPONSE", "retrofit response " + response.body());
                String resource = "";
                try {
                    resource = response.body().string();
                    res[0] = resource;
                    Log.d("RESPONSE", "retrofit response " + resource);
                } catch (IOException e) {
                    Log.d("RESPONSE", "retrofit response fail " + response.body());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RESPONSE ERROR", t.getMessage() + "");
                call.cancel();
            }
        });*/

        //Retrofit synchronously fetching data
        try{
            ResponseBody responseBody=call.execute().body();
            res[0]=responseBody.string().trim();
        }catch (Exception e){
            e.printStackTrace();
        }
        Train train_res=parseResult(coreSystem, train,res[0], start_date);
        return (train_res==null)?train:train_res;
    }

    public Train parseResult(CoreSystem coreSystem, Train train, String res, Date start_date){

        String date_string="startDate:\""+new SimpleDateFormat("d MMM yyyy", Locale.US).format(start_date)+"\"";
        if (!(res.contains("trainRunningDataFound"))){
            return null;
        }else{
            //modify train schedule and running information
            if(res.contains(date_string)){
                String res_running= res.substring(res.indexOf(date_string));
                res_running=res_running.substring(0, res_running.indexOf("isRunningDataAvailable:"));
                String run_data=res_running.substring(0, res_running.indexOf("stations"));
                String stations=res_running.substring(res_running.indexOf("["), res_running.indexOf("]")+1);
                run_data=run_data.replaceAll("(\\\"(.*?)\\\"|(\\w+))(\\s*:\\s*(\\\".*?\\\"|.))","\"$2$3\"$4");
                run_data=run_data.replaceAll("\"null","\"");
                run_data=run_data.trim();
                run_data="{"+run_data.substring(0,run_data.length()-1)+"}";
                JSONObject runData;
                boolean isDeparted, isTerminated;
                isDeparted=false;
                isTerminated=false;
                String currentStaion, departed, terminated;
                try{
                    runData=new JSONObject(run_data);
                    currentStaion=runData.getString("curStn");
                    train.setCurrentStation(currentStaion);
                    departed=runData.getString("departed");
                    if(departed.equalsIgnoreCase("true")){
                        isDeparted=true;
                    }
                    train.setDeparted(isDeparted);
                    terminated=runData.getString("terminated");
                    if(terminated.equalsIgnoreCase("true")){
                        isTerminated=true;
                    }
                    train.setTerminated(isTerminated);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stations=stations.replaceAll("(\\\"(.*?)\\\"|(\\w+))(\\s*:\\s*(\\\".*?\\\"|.))","\"$2$3\"$4");
                stations=stations.replaceAll("\"null","\"");
                stations=stations.trim();
                train.setStationStops(getStopsFromString(coreSystem, stations));
            }
            res=res.substring(res.indexOf("stations"));
        }
        return null;
    }

    public ArrayList<Stop> getStopsFromString(CoreSystem coreSystem, String res){
        ArrayList<Stop> stops=new ArrayList<Stop>();
        String stnCode, stnName, actArr, actDep, schArr, schDep, pf, halt, travelledString;
        int nonStoppageIdx, dist, day;
        Stop.Travelled travelled;
        JSONObject station;
        try{
            JSONArray stations=new JSONArray(res);
            int i;
            for (i=0;i<stations.length();i++) {
                station=stations.getJSONObject(i);
                stnCode=station.getString("stnCode");
                stnName= coreSystem.getStationNameFromCode(stnCode);
                actArr=station.getString("actArr");
                actDep=station.getString("actDep");
                schArr=station.getString("schArrTime");
                schDep=station.getString("schDepTime");
                if(actArr.equalsIgnoreCase("00:00")){
                    actArr="None";
                }
                if(schArr.equalsIgnoreCase("00:00")){
                    schArr="None";
                }
                if(actDep.equalsIgnoreCase("00:00")){
                    actDep="None";
                }
                if(schDep.equalsIgnoreCase("00:00")){
                    schDep="None";
                }
                nonStoppageIdx=station.getInt("sr")-1;
                dist=station.getInt("distance");
                day=station.getInt("dayCnt")+1;
                pf=Integer.toString(station.getInt("pfNo"));
                if((actArr.equalsIgnoreCase("None"))||(actDep.equalsIgnoreCase("None"))){
                    halt="None";
                }else{

                halt=Integer.toString(Tools.get_travel_time(actArr,actDep,1,1));
            }
                travelledString=station.getString("travelled");
                if(travelledString.equalsIgnoreCase("true")){
                    travelled= Stop.Travelled.TRUE;
                }else{
                    travelled= Stop.Travelled.FALSE;
                }
                stops.add(new Stop(i,stnCode,stnName,schArr,actArr,schDep,actDep,day,halt,dist,pf,nonStoppageIdx, travelled));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return stops;

    }
}