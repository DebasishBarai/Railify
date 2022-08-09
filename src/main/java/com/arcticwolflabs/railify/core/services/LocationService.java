package com.arcticwolflabs.railify.core.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import java.util.TimerTask;

public class LocationService extends Service {

    LocationProvider locationProvider;
    public LocationService() {

}
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        locationProvider=new LocationProvider(getApplicationContext());
        if (checkLocationPermissions()){
            locationProvider.startLocation();
        }
        return START_NOT_STICKY;
    }

    public void stopService() {

        locationProvider.stopLocationUpdates();
        stopSelf();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
