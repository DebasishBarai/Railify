package com.arcticwolflabs.railify.base.statics;

public class Platform {
    int pf_no;
    Boolean up_train, down_train, ticket_counter, toilet, drinking_water;

    //Constructor
    public Platform(int _pf_no, Boolean _up_train, Boolean _down_train, Boolean _ticket_counter, Boolean _toilet, Boolean _drinking_water) {
        pf_no = _pf_no;
        up_train = _up_train;
        down_train = _down_train;
        ticket_counter = _ticket_counter;
        toilet = _toilet;
        drinking_water = _drinking_water;
    }

    public void setPf_no(int _pf_no) {
        this.pf_no = _pf_no;
    }

    public int getPf_no() {
        return this.pf_no;
    }

    public void setUp_train(Boolean _up_train) {
        this.up_train = _up_train;
    }

    public Boolean getUp_train() {
        return this.up_train;
    }

    public void setDown_train(Boolean _down_train) {
        this.down_train = _down_train;
    }

    public Boolean getDown_train() {
        return this.down_train;
    }

    public void setTicket_counter(Boolean _ticket_counter) {
        this.ticket_counter = _ticket_counter;
    }

    public Boolean getTicket_counter() {
        return this.ticket_counter;
    }

    public void setToilet(Boolean _toilet) {
        this.toilet = _toilet;
    }

    public Boolean getToilet() {
        return this.toilet;
    }

    public void setDrinking_water(Boolean _drinking_water) {
        this.drinking_water = _drinking_water;
    }

    public Boolean getDrinking_water() {
        return this.drinking_water;
    }
}
