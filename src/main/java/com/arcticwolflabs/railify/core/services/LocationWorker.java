package com.arcticwolflabs.railify.core.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.Railify;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class LocationWorker extends Worker {

    LocationProvider locationProvider;
    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        locationProvider=new LocationProvider(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        if (Tools.checkLocationPermissions(getApplicationContext())){
            locationProvider.startLocation();
        }
        return null;
    }
}
