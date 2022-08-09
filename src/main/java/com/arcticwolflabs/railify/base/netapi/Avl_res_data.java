package com.arcticwolflabs.railify.base.netapi;

public class Avl_res_data {
    String avlcl;
    String value;
    int[]fare_amount;
    public Avl_res_data(String _avlcl,String _value,int[] _fare_amount){
        avlcl=_avlcl;
        value=_value;
        fare_amount=_fare_amount;
    }

    public void setAvlcl(String _avlcl) {
        this.avlcl = _avlcl;
    }

    public String getAvlcl() {
        return avlcl;
    }

    public void setValue(String _value) {
        this.value = _value;
    }

    public String getValue() {
        return value;
    }

    public void setFare_amount(int[] _fare_amount) {
        this.fare_amount = _fare_amount;
    }

    public int[] getFare_amount() {
        return fare_amount;
    }
}
