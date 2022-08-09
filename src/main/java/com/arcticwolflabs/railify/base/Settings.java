package com.arcticwolflabs.railify.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    private static final String PREF_FILE_NAME = "app.settings";
    private SharedPreferences prefs;
    private Context context;
    private SharedPreferences.Editor editor;

    private List<String> list_store_time_secs;
    private List<String> list_store_size;
    private List<String> list_update_period_days;

    public Settings(Context context) {
        this.context = context;
        this.prefs = this.context.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        this.editor = this.prefs.edit();

        list_update_period_days = new ArrayList<>();
        list_update_period_days.add("1");
        list_update_period_days.add("2");
        list_update_period_days.add("3");
        list_update_period_days.add("4");
        list_update_period_days.add("5");
        list_update_period_days.add("6");
        list_update_period_days.add("7");

        list_store_time_secs = new ArrayList<>();
        list_store_time_secs.add("60");
        list_store_time_secs.add("120");
        list_store_time_secs.add("300");
        list_store_time_secs.add("600");
        list_store_time_secs.add("1200");
        list_store_time_secs.add("1800");
        list_store_time_secs.add("3600");
        list_store_time_secs.add("7200");
        list_store_time_secs.add("0");

        list_store_size = new ArrayList<>();
        list_store_size.add("64");
        list_store_size.add("128");
        list_store_size.add("256");
        list_store_size.add("512");
        list_store_size.add("1024");
        list_store_size.add("2048");
        list_store_size.add("4096");
        list_store_size.add("0");

    }

    public List<String> getList_store_time_secs() {
        return list_store_time_secs;
    }

    public List<String> getList_store_size() {
        return list_store_size;
    }

    public List<String> getList_update_period_days() {
        return list_update_period_days;
    }

    public int getCacheStoreSize() {
        return this.prefs.getInt("cache_sz_kb", 0);
    }

    public void setCacheStoreSize(int itemnum) {
        editor.putInt("cache_sz_kb", itemnum);
        editor.apply();
    }

    public int getCacheStoreTime() {
        return this.prefs.getInt("cache_time_sec", 2);
    }

    public void setCacheStoreTime(int itemnum) {
        editor.putInt("cache_time_sec", itemnum);
        editor.apply();
    }

    public int getUpdateCheckPeriod() {

        return this.prefs.getInt("updt_intrvl_days", 0);
    }

    public void setUpdateCheckPeriod(int itemnum) {
        editor.putInt("updt_intrvl_days", itemnum);
        editor.apply();
    }
}

