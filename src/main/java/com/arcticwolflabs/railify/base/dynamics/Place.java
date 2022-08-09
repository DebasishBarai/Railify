package com.arcticwolflabs.railify.base.dynamics;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.arcticwolflabs.railify.base.statics.Address;

public class Place implements Comparable<Place>{
    private String id;
    private String title;
    private double distance;
    private Address address;
    private Bitmap icon_img;
    public Place(String id, String title, Double distance, Address address, Bitmap icon_img) {
        this.id = id;
        this.title = title;
        this.distance = distance;
        this.address = address;
        this.icon_img = icon_img;
    }
    
    @Override
    public int compareTo(@NonNull Place place) {
        return Double.compare(this.distance, place.distance);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Bitmap getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(Bitmap icon_img) {
        this.icon_img = icon_img;
    }
}
