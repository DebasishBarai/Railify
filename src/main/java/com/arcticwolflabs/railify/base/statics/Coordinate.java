package com.arcticwolflabs.railify.base.statics;

public class Coordinate {
    long latitude, longitude;
    int id;

    public Coordinate(long _latitude, long _longitude, int _id) {
        latitude = _latitude;
        longitude = _longitude;
        id = _id;
    }

    public void setLatitude(long _latitude) {
        this.latitude = _latitude;
    }

    public long getLatitude() {
        return this.latitude;
    }

    public void setLongitude(long _longitude) {
        this.longitude = _longitude;
    }

    public long getLongitude() {
        return this.longitude;
    }

    public void setID(int _id) {
        this.id = _id;
    }

    public int getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return "" + this.latitude + ", " + this.longitude;
    }

    public String toStringRaw() {
        return "Coordinate[id=" + this.id + ", latitude=" + this.latitude + ", longitude=" + this.longitude + "]";
    }
}
