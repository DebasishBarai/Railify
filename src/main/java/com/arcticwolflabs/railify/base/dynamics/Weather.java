package com.arcticwolflabs.railify.base.dynamics;

import android.graphics.Bitmap;

public class Weather {

    public static final int REGION_TYPE_EASTERN_HIMALAYAS = 0;
    public static final int REGION_TYPE_WESTERN_HIMALAYAS = 1;
    public static final int REGION_TYPE_NORTHEASTERN_RANGE = 2;
    public static final int REGION_TYPE_EASTERN_PLAINS = 3;
    public static final int REGION_TYPE_NORTHERN_PLAINS = 4;
    public static final int REGION_TYPE_WESTERN_PLAINS = 5;
    public static final int REGION_TYPE_CENTRAL_HIGHLANDS = 6;
    public static final int REGION_TYPE_EAST_DECCAN = 7;
    public static final int REGION_TYPE_NORTH_DECCAN = 8;
    public static final int REGION_TYPE_SOUTH_DECCAN = 9;
    public static final int REGION_TYPE_EASTERN_GHATS = 10;
    public static final int REGION_TYPE_WESTERN_GHATS = 11;
    public static final int REGION_TYPE_EAST_COAST = 12;
    public static final int REGION_TYPE_WEST_COAST = 13;
    public static final int REGION_TYPE_ISLANDS = 14;
    public static final String[] regions_names = {
            "Eastern Himalayas", "Western Himalayas", "North-Eastern Range",
            "Eastern Plains", "Northern Plains", "Western Plains",
            "Central Highlands", "East Deccan", "North Deccan",
            "South Deccan", "Eastern Ghats", "Western Ghats",
            "East Coast", "West Coast", "Islands"};
    private String id;
    private int region;
    private String weather;
    private double temp;
    private double wind;
    private double humidity;
    private double pressure;
    private Bitmap icon_img;

    public Weather(String id, int region, String weather, double temp, double wind, double humidity, double pressure, Bitmap icon_img) {
        this.id = id;
        this.region = region;
        this.weather = weather;
        this.temp = temp;
        this.wind = wind;
        this.humidity = humidity;
        this.pressure = pressure;
        this.icon_img = icon_img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public Bitmap getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(Bitmap icon_img) {
        this.icon_img = icon_img;
    }
}
