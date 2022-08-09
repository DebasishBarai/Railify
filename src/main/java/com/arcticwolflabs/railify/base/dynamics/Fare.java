package com.arcticwolflabs.railify.base.dynamics;

public class Fare {
    public static final String CLASSTYPE_AC1 = "1A";
    public static final String CLASSTYPE_AC2 = "2A";
    public static final String CLASSTYPE_AC3 = "3A";
    public static final String CLASSTYPE_CC = "CC";
    public static final String CLASSTYPE_FC = "FC";
    public static final String CLASSTYPE_SL = "SL";
    public static final String CLASSTYPE_2S = "2S";
    public static final String CLASSTYPE_ECON3 = "3E";
    public static final String CLASSTYPE_EC = "EC";
    public static final String CLASSTYPE_UR = "GN";

    //fare_class signifies the availabality of that class in the train//
    private boolean[] fare_class= {false, false, false, false, false, false, false, false,false,false};
    //fare_type signifies the type of fare e.g. fare_type[0]= val for GN type and fare_type[1]= val for Tatkal type
    private int[] fare_type={-1,-1};
    //fare_amount signifies the fares for all avl fare_class//
    private int[][] fare_amount={fare_type, fare_type, fare_type, fare_type, fare_type, fare_type, fare_type, fare_type, fare_type, fare_type};

    public void setClasstypeAc1(boolean _classtypeAc1){
        fare_class[0]=_classtypeAc1;
    }
    public boolean getClasstypeAc1(){
        return this.fare_class[0];
    }

    public void setClasstypeAc2(boolean _classtypeAc2){
        fare_class[1]=_classtypeAc2;
    }
    public boolean getClasstypeAc2(){
        return this.fare_class[1];
    }

    public void setClasstypeAc3(boolean _classtypeAc3){
        fare_class[2]=_classtypeAc3;
    }
    public boolean getClasstypeAc3(){
        return this.fare_class[2];
    }

    public void setClasstypeCc(boolean _classtypeCc){
        fare_class[3]=_classtypeCc;
    }
    public boolean getClasstypeCc(){
        return this.fare_class[3];
    }

    public void setClasstypeFc(boolean _classtypeFc){
        fare_class[4]=_classtypeFc;
    }
    public boolean getClasstypeFc(){
        return this.fare_class[4];
    }

    public void setClasstypeSl(boolean _classtypeSl){
        fare_class[5]=_classtypeSl;
    }
    public boolean getClasstypeSl(){
        return this.fare_class[5];
    }

    public void setClasstype2s(boolean _classtype2s){
        fare_class[6]=_classtype2s;
    }
    public boolean getClasstype2s(){
        return this.fare_class[6];
    }

    public void setClasstypeEcon3(boolean _classtypeEcon3){
        fare_class[7]=_classtypeEcon3;
    }
    public boolean getClasstypeEcon3(){
        return this.fare_class[7];
    }

    public void setClasstypeEc(boolean _classtypeEc){
        fare_class[8]=_classtypeEc;
    }
    public boolean getClasstypeec(){
        return this.fare_class[8];
    }

    public void setClasstypeUr(boolean _classtypeUr){
        fare_class[9]=_classtypeUr;
    }
    public boolean getClasstypeUr(){
        return this.fare_class[9];
    }


    public void setFareAc1(int[] _fareAc1){
        fare_amount[0]=_fareAc1;
    }
    public int[] getFareAc1(){
        return this.fare_amount[0];
    }

    public void setFareAc2(int[] _fareAc2){
        fare_amount[1]=_fareAc2;
    }
    public int[] getFareeAc2(){
        return this.fare_amount[1];
    }

    public void setFareAc3(int[] _fareAc3){
        fare_amount[2]=_fareAc3;
    }
    public int[] getFareAc3(){
        return this.fare_amount[2];
    }

    public void setFareCc(int[] _fareCc){
        fare_amount[3]=_fareCc;
    }
    public int[] getFareCc(){
        return this.fare_amount[3];
    }

    public void setFareFc(int[] _fareFc){
        fare_amount[4]=_fareFc;
    }
    public int[] getFareFc(){
        return this.fare_amount[4];
    }

    public void setFareSl(int[] _fareSl){
        fare_amount[5]=_fareSl;
    }
    public int[] getFareSl(){
        return this.fare_amount[5];
    }

    public void setFare2s(int[] _fare2s){
        fare_amount[6]=_fare2s;
    }
    public int[] getFare2s(){
        return this.fare_amount[6];
    }

    public void setFareEcon3(int[] _fareEcon3){
        fare_amount[7]=_fareEcon3;
    }
    public int[] getFareEcon3(){
        return this.fare_amount[7];
    }

    public void setFareEc(int[] _fareEc){
        fare_amount[8]=_fareEc;
    }
    public int[] getFareEc(){
        return this.fare_amount[8];
    }
    public void setFareUr(int[] _fareUr){
        fare_amount[9]=_fareUr;
    }
    public int[] getFareUr(){
        return this.fare_amount[9];
    }

    public boolean[] getFare_class(){
        return this.fare_class;
    }
    public int[][] getFare_amount(){
        return this.fare_amount;
    }
}
