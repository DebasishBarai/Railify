package com.arcticwolflabs.railify.base.dynamics;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Stop {
    private String code, name, halt, sch_arr, act_arr, sch_dep, act_dep, platform;
    private int idx, day, dist, non_stoppage_idx;
    private Travelled travelled;


    public static class StopMinimal {
        int idx;
        String code;
        String name;

        public StopMinimal(int idx, String code, String name) {
            this.idx = idx;
            this.code = code;
            this.name = name;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
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
            return "Station[code=" + this.code + ", name=" + this.name + "]";
        }
    }


    public Stop(int _idx, String _code, String _name, String _sch_arr, String _sch_dep, int _day, String _halt, int _dist, String _platform) {
        idx = _idx;
        code = _code;
        name = _name;
        sch_arr = _sch_arr;
        act_arr = "--";
        sch_dep = _sch_dep;
        act_dep = "--";
        day = _day;
        halt = _halt;
        dist = _dist;
        platform = _platform;
        non_stoppage_idx= -1;
        travelled=Travelled.NOT_KNOWN;
    }


    public Stop(int _idx, String _code, String _name, String _sch_arr, String _act_arr, String _sch_dep, String _act_dep, int _day, String _halt, int _dist, String _platform, int _non_stoppage_idx, Travelled _travelled) {
        idx = _idx;
        code = _code;
        name = _name;
        sch_arr = _sch_arr;
        act_arr = _act_arr;
        sch_dep = _sch_dep;
        act_dep = _act_dep;
        day = _day;
        halt = _halt;
        dist = _dist;
        platform = _platform;
        non_stoppage_idx=_non_stoppage_idx;
        travelled=_travelled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setStation_no(int _station_no) {
        this.idx = _station_no;
    }

    public int getStation_no() {
        return this.idx;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public void setSch_arr(String _sch_arr) {
        this.sch_arr = _sch_arr;
    }

    public String getSch_arr() {
        return this.sch_arr;
    }

    public void setAct_arr(String act_arr) {
        this.act_arr = act_arr;
    }

    public String getAct_arr() {
        return act_arr;
    }

    public void setSch_dep(String _sch_dep) {
        this.sch_dep = _sch_dep;
    }

    public String getSch_dep() {
        return this.sch_dep;
    }

    public void setAct_dep(String act_dep) {
        this.act_dep = act_dep;
    }

    public String getAct_dep() {
        return act_dep;
    }

    public void setDay(int _day) {
        this.day = _day;
    }

    public int getDay() {
        return this.day;
    }

    public void setHalt(String _halt) {
        this.halt = _halt;
    }

    public String getHalt() {
        return this.halt;
    }

    public void setDist(int _dist) {
        this.dist = _dist;
    }

    public int getDist() {
        return this.dist;
    }

    public void setPlatform(String _platform) {
        this.platform = _platform;
    }

    public String getPlatform() {
        return this.platform;
    }

    public enum Travelled {
        TRUE,
        FALSE,
        NOT_KNOWN
    };

}

