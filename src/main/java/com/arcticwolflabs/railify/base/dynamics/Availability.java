package com.arcticwolflabs.railify.base.dynamics;

import java.util.HashMap;

public class Availability {
    public static final int NUM_QUOTATYPE = 4;
    public static final String QUOTATYPE_GEN = "GN";
    public static final String QUOTATYPE_TKL = "CK";
    public static final String QUOTATYPE_LAD = "LD";
    public static final String QUOTATYPE_DEF = "DF";
    public static final String QUOTATYPE_HNCP = "HP";

    public static final int NUM_AVLTYPE = 4;
    public static final String AVLTYPE_AVL = "CURR_AVBL";
    public static final String AVLYPE_NA = "NOT AVAILABLE";
    public static final String AVLTYPE_GNWL = "GNWL";
    public static final String AVLTYPE_WL = "WL";
    public static final String[] AVLTYPES = {AVLTYPE_AVL, AVLYPE_NA, AVLTYPE_GNWL, AVLTYPE_WL};

    public static final int NUM_CLASSTYPE = 9;
    public static final String CLASSTYPE_AC1 = "1A";
    public static final String CLASSTYPE_AC2 = "2A";
    public static final String CLASSTYPE_AC3 = "3A";
    public static final String CLASSTYPE_CC = "CC";
    public static final String CLASSTYPE_FC = "FC";
    public static final String CLASSTYPE_SL = "SL";
    public static final String CLASSTYPE_2S = "2S";
    public static final String CLASSTYPE_ECON3 = "3E";
    public static final String CLASSTYPE_EC = "EC";
    public static final String[] CLASSTYPES = {
            CLASSTYPE_AC1, CLASSTYPE_AC2, CLASSTYPE_AC3, CLASSTYPE_CC, CLASSTYPE_FC,
            CLASSTYPE_SL, CLASSTYPE_2S, CLASSTYPE_ECON3, CLASSTYPE_EC};


    /* 10 quotas: Gen/Tatkal/Ladies/Defence/Foreign/DutyPass/Handicapped/Parliament/LowerBerth/Yuva */
    /* 7 WL types: GNWL/RLWL/PQWL/RLGN/RSWL/RQWL/CKWL */
    private Integer[]availability_val={-1,-1,-1,-1,-1};
    /* Integer[]availability: {available, RAC, WL, WL Type={0-6=GNWL/RLWL/PQWL/RLGN/RSWL/RQWL/CKWL}} */
    private Integer[][]availability={availability_val,availability_val,availability_val,availability_val,availability_val,availability_val,availability_val,availability_val,availability_val};
    private String date="";
    private String quota="";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public Integer[] getAvailability_1A() {
        return availability[0];
    }
    public void setAvailability_1A(Integer[] availability) {
        this.availability[0] = availability;
    }

    public Integer[] getAvailability_2A() {
        return availability[1];
    }
    public void setAvailability_2A(Integer[] availability) {
        this.availability[1] = availability;
    }

    public Integer[] getAvailability_3A() {
        return availability[2];
    }
    public void setAvailability_3A(Integer[] availability) {
        this.availability[2] = availability;
    }

    public Integer[] getAvailability_CC() {
        return availability[3];
    }
    public void setAvailability_CC(Integer[] availability) {
        this.availability[3] = availability;
    }

    public Integer[] getAvailability_FC() {
        return availability[4];
    }
    public void setAvailability_FC(Integer[] availability) {
        this.availability[4] = availability;
    }

    public Integer[] getAvailability_SL() {
        return availability[5];
    }
    public void setAvailability_SL(Integer[] availability) {
        this.availability[5] = availability;
    }

    public Integer[] getAvailability_2S() {
        return availability[6];
    }
    public void setAvailability_2S(Integer[] availability) {
        this.availability[6] = availability;
    }

    public Integer[] getAvailability_3E() {
        return availability[7];
    }
    public void setAvailability_3E(Integer[] availability) {
        this.availability[7] = availability;
    }

    public Integer[] getAvailability_EC() {
        return availability[8];
    }
    public void setAvailability_EC(Integer[] availability) {
        this.availability[8] = availability;
    }
    public Integer[][] getAvailability(){
        return availability;
    }
}

