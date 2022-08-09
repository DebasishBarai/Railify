package com.arcticwolflabs.railify.base.dynamics;

public class TRRNonStoppageStation {
    String stnName, dist,sch_dep;
    int day;
    public TRRNonStoppageStation(String _stnName, String _dist, String _sch_dep, int _day) {
        stnName=_stnName;
        dist=_dist;
        sch_dep=_sch_dep;
        day=_day;
    }

    public void setStnName(String stnName) {
        this.stnName = stnName;
    }

    public String getStnName() {
        return stnName;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getDist() {
        return dist;
    }

    public void setSch_dep(String sch_dep) {
        this.sch_dep = sch_dep;
    }

    public String getSch_dep() {
        return sch_dep;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }
}
