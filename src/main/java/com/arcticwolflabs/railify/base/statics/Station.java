package com.arcticwolflabs.railify.base.statics;

import java.util.ArrayList;

public class Station {
    String code, name;
    int no_of_platform, length;
    Address address;
    Telephone telephone;
    ArrayList<Integer> train_no;

    public static class StationMinimal {
        String code;
        String name;

        public StationMinimal(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "" + this.code + ", " + this.name;
        }

        public String toStringRaw() {
            return "Station[id=" + this.code + ", name=" + this.name + "]";
        }
    }

    //Constructor
    public Station(String _code, String _name, int _no_of_platform, int _length, Address _address, Telephone _telephone) {
        code = _code;
        name = _name;
        no_of_platform = _no_of_platform;
        length = _length;
        address = _address;
        telephone = _telephone;
    }

    public Station() {
        code = null;
        name = null;
        no_of_platform = 0;
        length = 0;
        address = null;
        telephone = null;
        train_no = null;
    }

    public Station(String _code, String _name, int _no_of_platform, int _length, Address _address, Telephone _telephone, ArrayList<Integer> _train_no) {
        code = _code;
        name = _name;
        no_of_platform = _no_of_platform;
        length = _length;
        address = _address;
        telephone = _telephone;
        train_no = _train_no;
    }

    public void setCode(String _code) {
        this.code = _code;
    }

    public String getCode() {
        return this.code;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public void setNo_of_platform(int _no_of_platform) {
        this.no_of_platform = _no_of_platform;
    }

    public int getNo_of_platform() {
        return this.no_of_platform;
    }

    public void setLength(int _length) {
        this.length = _length;
    }

    public int getLength() {
        return this.length;
    }

    public void setAddress(Address _address) {
        this.address = _address;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setTelephone(Telephone _telephone) {
        this.telephone = _telephone;
    }

    public Telephone getTelephone() {
        return this.telephone;
    }

    public void setTrain_no(ArrayList<Integer> _train_no) {
        this.train_no = _train_no;
    }

    public ArrayList<Integer> getTrain_no() {
        return this.train_no;
    }

    public void addTrain_no(int _train_no) {
        train_no.add(_train_no);
    }

    @Override
    public String toString() {
        return "" + this.code + ", " + this.name;
    }
}
