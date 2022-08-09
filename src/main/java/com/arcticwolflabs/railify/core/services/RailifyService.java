package com.arcticwolflabs.railify.core.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.Settings;
import com.arcticwolflabs.railify.base.netapi.UpdateAPI;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.UpdateActivity;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class RailifyService extends Service {

    private int UPDATE_INTRVL_MS;
    private Timer timer = new Timer();

    public RailifyService() {
        UPDATE_INTRVL_MS = 60*60*24000;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    getString(R.string.upd_channel_id),
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void showNotification(String text) {
        //Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        Intent intent=new Intent();
        /*intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);*/

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.upd_channel_id))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notification.build());
        }
    }

    private void checkUpdates() {
        UpdateAPI updr = new UpdateAPI(this);
        if (updr.check()) {
            Queue<UpdateAPI.UpdateInfo> updts = updr.getUpdates();
            for (UpdateAPI.UpdateInfo cinfo : updts) {
                showNotification(cinfo.getInfo());
            }
        }
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        TimerTask atask = new TimerTask() {
            @Override
            public void run() {
                checkUpdates();
            }
        };
        timer.schedule(atask, 10, UPDATE_INTRVL_MS);
        createNotificationChannel();
        showNotification("database updated successfully");
        return START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Settings settings = new Settings(getApplicationContext());
        UPDATE_INTRVL_MS = Integer.valueOf(
                settings.getList_update_period_days().get(settings.getCacheStoreTime())
        )*60*60*24000;
    }

    public void stopService() {
        if (timer != null) timer.cancel();
        stopSelf();
    }
}
