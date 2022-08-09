package com.arcticwolflabs.railify.core.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.arcticwolflabs.railify.ui.utils.Tools;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ServiceSystem{
    Context context;
    private boolean isLocationServiceStarted;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final String TAG_LOCATION ="TagLocation";

    public ServiceSystem(Context _context) {
        context = _context;
        isLocationServiceStarted = false;
    }
/*
    public void startLocationService(Activity activity) {
        Log.d("service system", "startLocationService started");
        if (Tools.checkLocationPermissions(context)) {

            if (!isLocationServiceStarted) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        context.startService(new Intent(context, LocationService.class));
                    }
                };
                thread.start();
                isLocationServiceStarted = true;
                Log.d("Location Service", "Location Service started");
            }
        } else {
            requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            if (Tools.checkLocationPermissions(context)) {

                if (!isLocationServiceStarted) {

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            context.startService(new Intent(context, LocationService.class));
                        }
                    };
                    thread.start();
                    isLocationServiceStarted = true;
                    Log.d("Location Service", "Location Service started after granting permission");
                }
            }

        }
    }
*/
    public void startLocationWorker() {
        try {

            if ((Tools.checkLocationPermissions(context))&&(!isWorkScheduled(WorkManager.getInstance().getWorkInfosByTag(TAG_LOCATION).get()))) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build();
                // START Worker
                PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                        .addTag(TAG_LOCATION)
                        .setConstraints(constraints)
                        .build();
                WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
                isLocationServiceStarted = true;
                Log.d("Location Service", "Location worker started");
            }
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopLocationWorker(){
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG_LOCATION);
    }

 /*   public void stopLocationService() {

        context.stopService(new Intent(context, LocationService.class));
        Log.d("Location Service", "Location Service stopped");
    }

    public void check_database_service() {
        WorkRequest dbDownloadWorkRequest =
                new OneTimeWorkRequest.Builder(DbDownloader.class)
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(dbDownloadWorkRequest);
    }

    public Location getLocation() {

        //TODO: get the data from LocationService
        Location location = null;
        //return the Howrah Station Dummy location
        location.setLatitude(22.5835032884945);
        location.setLongitude(88.3422660827637);
        return location;
    }

    public Station getNearestStation() {
        Location currentLocation= ((Railify)context.getApplicationContext()).getLocation();
        return new Station();
    }*/

    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }
        private boolean checkLocationPermissions() {
            int permissionState = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            return permissionState == PackageManager.PERMISSION_GRANTED;
        }
}
