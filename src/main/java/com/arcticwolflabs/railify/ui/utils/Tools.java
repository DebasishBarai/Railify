package com.arcticwolflabs.railify.ui.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arcticwolflabs.railify.BuildConfig;
import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Journey;

import java.util.ArrayList;

public class Tools {

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int get_travel_time(String _boarding_time, String _deboarding_time, int _from_day, int _to_day) {
        int boarding_time, deboarding_time;
        boarding_time = (((Integer.parseInt(_boarding_time.substring(0, 2))) + ((_from_day - 1) * 24)) * 60) + (Integer.parseInt(_boarding_time.substring(3, 5)));
        deboarding_time = (((Integer.parseInt(_deboarding_time.substring(0, 2))) + ((_to_day - 1) * 24)) * 60) + (Integer.parseInt(_deboarding_time.substring(3, 5)));
        int diff_time = deboarding_time - boarding_time;
        return diff_time;
    }

    public static String extractStationID(String stationListName) {
        int idx = stationListName.indexOf(',');
        if (idx >= 0) {
            return stationListName.substring(0, idx);
        }
        return "";
    }

    public static String extractStationName(String stationListName) {
        int idx = stationListName.indexOf(',');
        if (idx >= 0 && idx < stationListName.length()) {
            return stationListName.substring(idx);
        }
        return "";
    }

    public static String extractTrainNo(String trainNoName) {
        int idx = trainNoName.indexOf(',');
        if (idx >= 0) {
            return trainNoName.substring(0, trainNoName.indexOf(','));
        }
        return "";
    }

    public static String extractTrainName(String trainNoName) {
        int idx = trainNoName.indexOf(',');
        if (idx >= 0 && idx < trainNoName.length()) {
            return trainNoName.substring(idx + 1);
        }
        return "";
    }

    public static String extractRunDaysFromDecimal(Context context, int decRunDay, int day) {
        int i = 0;
        String res = "MoTuWeThFrSaSu";
        String.format(res, context.getResources().getColor(android.R.color.holo_orange_dark));
        int labelColor = context.getResources().getColor(R.color.primary_text);
        String сolorString = String.format("%X", labelColor).substring(2);

        for (i = 0; i < 7; i++) {
            if (((generateRotatingInt(decRunDay) >>> (i + day)) & 1) == 1) {
                switch (i) {
                    case 0:
                        res = res.replace("Su", String.format("<font color=\"#%s\">Su</font>", сolorString));
                        break;
                    case 1:
                        res = res.replace("Sa", String.format("<font color=\"#%s\">Sa</font>", сolorString));
                        break;
                    case 2:
                        res = res.replace("Fr", String.format("<font color=\"#%s\">Fr</font>", сolorString));
                        break;
                    case 3:
                        res = res.replace("Th", String.format("<font color=\"#%s\">Th</font>", сolorString));
                        break;
                    case 4:
                        res = res.replace("We", String.format("<font color=\"#%s\">We</font>", сolorString));
                        break;
                    case 5:
                        res = res.replace("Tu", String.format("<font color=\"#%s\">Tu</font>", сolorString));
                        break;
                    case 6:
                        res = res.replace("Mo", String.format("<font color=\"#%s\">Mo</font>", сolorString));
                        break;
                }
            }
        }
        return res;
    }

    //Bitwise sfifting of the final data to be done in right side only. Left side shifting will result in error.
    public static int generateRotatingInt(int day) {
        int i = 0;
        for (i = 0; i < 4; i++) {
            day = (day << 7) | day;
        }
        return day;
    }

    public static int getValidRunDays(int runDay_1, int rightShift_1, int runDay_2, int rightShift_2) {
        runDay_1 = generateRotatingInt(runDay_1);
        runDay_2 = generateRotatingInt(runDay_2);
        runDay_1 = runDay_1 >> rightShift_1;
        runDay_2 = runDay_2 >> rightShift_2;
        return (runDay_1 & runDay_2);
    }

    public String[] extract_arr_dep(int arrhr, int arrmin, int dephr, int depmin) {
        String halt, sch_arr, sch_dep;
        int day;
        halt = "None";
        String a, b, c, d;

        if ((arrhr + 256) != 255) {
            day = (arrhr / 24) + 1;
            if ((arrhr % 24) < 10) {
                a = "0" + Integer.toString(arrhr % 24);
            } else {
                a = Integer.toString(arrhr % 24);
            }
            if (arrmin < 10) {
                b = "0" + Integer.toString(arrmin);
            } else {
                b = Integer.toString(arrmin);
            }
            sch_arr = a + ":" + b;

        } else {
            day = 1;
            sch_arr = "None";
        }
        if ((dephr + 256) != 255) {
            if ((dephr % 24) < 10) {
                c = "0" + Integer.toString(dephr % 24);
            } else {
                c = Integer.toString(dephr % 24);
            }
            if (depmin < 10) {
                d = "0" + Integer.toString(depmin);
            } else {
                d = Integer.toString(depmin);
            }

            sch_dep = c + ":" + d;

        } else {
            sch_dep = "None";
        }
        if (((arrhr + 256) != 255) && ((dephr + 256) != 255)) {
            halt = Integer.toString(((dephr * 60) + depmin) - ((arrhr * 60) + arrmin));

        }
        String[] res = {sch_arr, sch_dep, halt, Integer.toString(day)};
        return res;
    }

    public static int get_double_journey_total_time(Journey.JourneyMinimal[] jrny){
        int jrny_t1 = jrny[0].getTravel_time();
        int halt_t = Tools.get_travel_time(jrny[0].getArr(), jrny[jrny.length - 1].getDep(), 1, 1);
        if (halt_t < 0) {
            halt_t=Tools.get_travel_time(jrny[0].getArr(), jrny[jrny.length - 1].getDep(), 1, 2);
        }
        int jrny_t2 = jrny[jrny.length - 1].getTravel_time();
        int tot_jrny_t = jrny_t1 + halt_t + jrny_t2;
        return tot_jrny_t;
    }

    public static String get_hrmin_from_min(int min){
        String hrmin;
        int hr;
        int mn;
        hr=min/60;
        mn=min%60;
        hrmin=(hr==0)?(Integer.toString(mn)+" min"):(Integer.toString(hr)+" hr "+Integer.toString(mn)+" min");
        return hrmin;
    }

    public static boolean checkLocationPermissions(Context context) {
        return ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED));
    }
    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static String readSharedSetting(Context ctx, String preferenceFileName, String settingName, String defaultValue, int mode) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(preferenceFileName, mode);
        return sharedPref.getString(settingName, defaultValue);
    }
}
