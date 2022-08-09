package com.arcticwolflabs.railify;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.arcticwolflabs.railify.base.statics.Station;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.core.services.ServiceSystem;

public class Railify extends Application {
    private CoreSystem coreSystem;
    private ServiceSystem serviceSystem;
    private Location location;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long t1,t2;

    public void setSharedPreferences (Context context) {
        sharedPreferences=context.getSharedPreferences("preferencesFile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        location=new Location("dummyProvider");
        if(!(sharedPreferences.getString("LatitudePreference","").equalsIgnoreCase("")&&sharedPreferences.getString("LongitudePreference","").equalsIgnoreCase(""))){
            location.setLatitude(Double.parseDouble(sharedPreferences.getString("LatitudePreference","")));
            location.setLongitude(Double.parseDouble(sharedPreferences.getString("LongitudePreference","")));
        }
        sharedPreferences.getString("nearestStationCode","");
    }

    public long getT1() {
        return t1;
    }

    public void setT1(long t1) {
        this.t1 = t1;
    }

    public long getT2() {
        return t2;
    }

    public void setT2(long t2) {
        this.t2 = t2;
    }

    public CoreSystem getCoreSystem() {
        return coreSystem;
    }

    public void setCoreSystem(CoreSystem coreSystem) {
        this.coreSystem = coreSystem;
    }

    public ServiceSystem getServiceSystem() {
        return serviceSystem;
    }

    public void copyAssets(){
        coreSystem.copyAssets();
    }
    public void loadGraph(){
        coreSystem.loadGraph();
    }
    public void setServiceSystem(ServiceSystem serviceSystem) {
        this.serviceSystem = serviceSystem;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {


        //For dummy location setting
        /*if (!(location==null)){
            location.setLatitude(22.028540);
            location.setLongitude(88.059951);
        }*/

        if(!(location==null)){
            this.location = location;
            /*editor.putString("LatitudePreference", Double.toString(22.028540));
            editor.putString("LongitudePreference", Double.toString(88.059951));
            editor.commit();*/
            editor.putString("LatitudePreference", Double.toString(location.getLatitude()));
            editor.putString("LongitudePreference", Double.toString(location.getLongitude()));
            editor.commit();
            Station nearestStation=coreSystem.getNearestStation();
            if(!(nearestStation==null)){
                editor.putString("nearestStationCode", nearestStation.toString());
                editor.commit();
            }

        }
    }

}
