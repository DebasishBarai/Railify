package com.arcticwolflabs.railify.base.statics;

import java.util.ArrayList;

public class Telephone {
    ArrayList<String> railway_enquiry_no, grp_no;

    //Constructor
    public Telephone(ArrayList<String> _railway_inquiry_no, ArrayList<String> _grp_no) {
        railway_enquiry_no = _railway_inquiry_no;
        grp_no = _grp_no;
    }

    public void setRailway_enquiry_no(ArrayList<String> _railway_inquiry_no) {
        this.railway_enquiry_no = _railway_inquiry_no;
    }

    public void addRailway_enquiry_no(String _railway_inquiry_no) {
        this.railway_enquiry_no.add(_railway_inquiry_no);
    }

    public ArrayList<String> getRailway_inquiry_no() {
        return this.railway_enquiry_no;
    }

    public void setGrp_no(ArrayList<String> _grp_no) {
        this.grp_no = grp_no;
    }

    public void addGrp_no(String grp_no) {
        this.grp_no.add(grp_no);
    }

    public ArrayList<String> getGrp_no() {
        return this.grp_no;
    }
}
