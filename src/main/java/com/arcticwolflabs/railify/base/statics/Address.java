package com.arcticwolflabs.railify.base.statics;

import android.location.Location;

public class Address {
    Location location;
    String address_string;
    String district, state;

    //Constructor
    public Address() {
        location=null;
        address_string="";
        district = "";
        state = "";
    }

    public Address(Location _location, String _address_string) {
        location = _location;
        address_string=_address_string;
        district = "";
        state = "";
    }

    public Address(Location _location, String _address_string, String _district, String _state) {
        location = _location;
        address_string=_address_string;
        district = _district;
        state = _state;
    }


    public void setCoordinate(double _latitude, double _longitude) {
        this.location=new Location("dummyProvider");
        this.location.setLatitude(_latitude);
        this.location.setLongitude(_longitude);
    }

    public Location getLocation() {
        return this.location;
    }

    public void setDistrict(String _district) {
        this.district = _district;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setState(String _state) {
        this.state = _state;
    }

    public String getState() {
        return this.state;
    }
}
