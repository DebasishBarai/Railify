package com.arcticwolflabs.railify.base.dynamics;

import androidx.annotation.NonNull;

import com.arcticwolflabs.railify.ui.utils.Tools;

import java.util.Comparator;

public class Journey {
    public static class JourneyMinimal implements Comparable<JourneyMinimal[]>{
        int train_id;
        String train_name;
        String from_id, to_id;
        String from_stn_name,to_stn_name;
        String dep, arr;
        int from_day, to_day;
        int from_idx, to_idx;
        int run_day;
        int travel_time;

        @Override
        public String toString() {
            int num_days = to_day - from_day;
            if (num_days == 0) {
                return "" + train_id + "|" + train_name + "| Departure: " + dep + " Arrival: " + arr;
            }
            return "" + train_id + "|" + train_name + "| Departure: " + dep + " Arrival: " + arr + "(+" + num_days + ")";
        }

        public JourneyMinimal(int train_id, String train_name, String from_id, String from_stn_name, String to_id, String to_stn_name,
                              String dep, String arr, int from_day, int to_day,
                              int from_idx, int to_idx) {
            this.train_id = train_id;
            this.train_name = train_name;
            this.from_id = from_id;
            this.from_stn_name=from_stn_name;
            this.to_id = to_id;
            this.to_stn_name=to_stn_name;
            this.from_day = from_day;
            this.to_day = to_day;
            this.dep = dep;
            this.arr = arr;
            this.from_idx = from_idx;
            this.to_idx = to_idx;
            this.run_day=127;
            travel_time=((!this.getDep().equalsIgnoreCase(""))&&(!this.getArr().equalsIgnoreCase("")))?Tools.get_travel_time(this.getDep(), this.getArr(), this.getFrom_day(),this.getTo_day()):0;

        }

        public JourneyMinimal(int train_id, String train_name, String from_id, String from_stn_name, String to_id, String to_stn_name,
                              String dep, String arr, int from_day, int to_day,
                              int from_idx, int to_idx, int run_day) {
            this.train_id = train_id;
            this.train_name = train_name;
            this.from_id = from_id;
            this.from_stn_name=from_stn_name;
            this.to_id = to_id;
            this.to_stn_name=to_stn_name;
            this.from_day = from_day;
            this.to_day = to_day;
            this.dep = dep;
            this.arr = arr;
            this.from_idx = from_idx;
            this.to_idx = to_idx;
            this.run_day=run_day;
            travel_time=((!this.getDep().equalsIgnoreCase(""))&&(!this.getArr().equalsIgnoreCase("")))?Tools.get_travel_time(this.getDep(), this.getArr(), this.getFrom_day(),this.getTo_day()):0;

        }

        @Override
        public int compareTo(@NonNull JourneyMinimal[] journeyMinimal) {
            return dep.compareTo(journeyMinimal[0].dep);
        }

        public static Comparator<JourneyMinimal[]> compare_dep=
                new Comparator<JourneyMinimal[]>() {
                    @Override
                    public int compare(JourneyMinimal[] journeyMinimal1, JourneyMinimal[] journeyMinimal2) {
                        return journeyMinimal1[0].dep.compareTo(journeyMinimal2[0].dep);
                    }
                };

        public static Comparator<JourneyMinimal[]> compare_arr=
                new Comparator<JourneyMinimal[]>() {
                    @Override
                    public int compare(JourneyMinimal[] journeyMinimal1, JourneyMinimal[] journeyMinimal2) {
                        return journeyMinimal1[journeyMinimal1.length-1].arr.compareTo(journeyMinimal2[journeyMinimal2.length-1].arr);
                    }
                };

        public static Comparator<JourneyMinimal[]> compare_travel_t=
                new Comparator<JourneyMinimal[]>() {
                    @Override
                    public int compare(JourneyMinimal[] journeyMinimal1, JourneyMinimal[] journeyMinimal2) {
                        int tot_jrny1_t;
                        int tot_jrny2_t;
                        if(journeyMinimal1.length==1){
                            tot_jrny1_t=journeyMinimal1[0].getTravel_time();
                        }else {
                            tot_jrny1_t = Tools.get_double_journey_total_time(journeyMinimal1);
                        }

                        if(journeyMinimal2.length==1){
                            tot_jrny2_t=journeyMinimal2[0].getTravel_time();
                        }else {

                            tot_jrny2_t = Tools.get_double_journey_total_time(journeyMinimal2);
                        }

                        return tot_jrny1_t-tot_jrny2_t;
                    }
                };


        public int getTrain_num() {
            return train_id;
        }

        public void setTrain_id(int train_id) {
            this.train_id = train_id;
        }

        public String getTrain_name() {
            return train_name;
        }

        public void setTrain_name(String train_name) {
            this.train_name = train_name;
        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }

        public String getFrom_stn_name(){
            return from_stn_name;
        }

        public void setFrom_stn_name(String from_stn_name) {
            this.from_stn_name = from_stn_name;
        }

        public String getTo_id() {
            return to_id;
        }

        public String getTo_stn_name(){
            return to_stn_name;
        }

        public void setTo_stn_name(String to_stn_name) {
            this.to_stn_name = to_stn_name;
        }

        public void setTo_id(String to_id) {
            this.to_id = to_id;
        }

        public int getFrom_day() {
            return from_day;
        }

        public void setFrom_day(int from_day) {
            this.from_day = from_day;
        }

        public int getTo_day() {
            return to_day;
        }

        public void setTo_day(int to_day) {
            this.to_day = to_day;
        }

        public String getDep() {
            return dep;
        }

        public void setDep(String dep) {
            this.dep = dep;
        }

        public String getArr() {
            return arr;
        }

        public void setArr(String arr) {
            this.arr = arr;
        }

        public int getFrom_idx() {
            return from_idx;
        }

        public void setFrom_idx(int from_idx) {
            this.from_idx = from_idx;
        }

        public int getTo_idx() {
            return to_idx;
        }

        public void setTo_idx(int to_idx) {
            this.to_idx = to_idx;
        }

        public int getRun_day() {
            return run_day;
        }

        public void setRun_day(int run_day) {
            this.run_day = run_day;
        }

        public int getTravel_time() {
            return travel_time;
        }

        public void setTravel_time(int travel_time) {
            this.travel_time = travel_time;
        }
    }

}
