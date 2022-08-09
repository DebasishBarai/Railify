package com.arcticwolflabs.railify.base.dynamics;

import java.util.ArrayList;
import java.util.HashMap;

public class Train {
    int train_num;
    String name;
    String type;
    ArrayList<Stop> station_stops;
    int last_arrived_idx;
    ArrayList<Coach> coaches;
    int runday;
    HashMap<Integer,ArrayList<String>> non_stoppage_stops;
    TrainLocation trainLocation;

    boolean isDeparted, isTerminated;
    String currentStation;

    public static class TrainMinimal {
        String code;
        String name;

        public TrainMinimal(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String _code) {
            this.code = _code;
        }

        public String getName() {
            return name;
        }

        public void setName(String _name) {
            this.name = _name;
        }

        @Override
        public String toString() {
            return "" + this.code + ", " + this.name;
        }

        public String toStringRaw() {
            return "Station[id=" + this.code + ", name=" + this.name + "]";
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Train(int _train_id, String _type) {
        train_num = _train_id;
        type = _type;
        station_stops = new ArrayList<Stop>();
        coaches = new ArrayList<Coach>();
        runday=0;
        non_stoppage_stops=new HashMap<Integer, ArrayList<String>>();
        trainLocation=null;

        isDeparted=false;
        isTerminated=false;
        currentStation="";
    }

    public Train(int _train_id) {
        train_num = _train_id;
        type = null;
        station_stops = new ArrayList<Stop>();
        coaches = new ArrayList<Coach>();
        runday=0;
        non_stoppage_stops=new HashMap<Integer, ArrayList<String>>();
        trainLocation=null;

        isDeparted=false;
        isTerminated=false;
        currentStation="";
    }

    public void setTrainNum(int _train_no) {
        this.train_num = _train_no;
    }

    public int getTrainNum() {
        return this.train_num;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public String getType() {
        return this.type;
    }

    public void setStationStops(ArrayList<Stop> _station_stops) {
        this.station_stops = _station_stops;
    }

    public void setRunday(int _runday){
        this.runday=_runday;
    }

    public int getRunday(){
        return this.runday;
    }

    public ArrayList<Stop> getStationStops() {
        return this.station_stops;
    }

    public void addStationStop(Stop _station_stop) {
        station_stops.add(_station_stop);
    }

    public void setLast_arrived_idx(int last_arrived_idx) {
        this.last_arrived_idx = last_arrived_idx;
    }

    public int getLast_arrived_idx() {
        return last_arrived_idx;
    }

    public void setCoaches(ArrayList<Coach> _coaches) {
        this.coaches = _coaches;
    }

    public ArrayList<Coach> getCoaches() {
        return this.coaches;
    }

    public void addCoach(Coach _coach) {
        coaches.add(_coach);
    }

    public HashMap<Integer, ArrayList<String>> getNon_stoppage_stops() {
        return non_stoppage_stops;
    }

    public void setNon_stoppage_stops(HashMap<Integer, ArrayList<String>> non_stoppage_stops) {
        this.non_stoppage_stops = non_stoppage_stops;
    }

    public ArrayList<String> getNon_stoppage_stops_stationwise(int _station_idx){
        return this.non_stoppage_stops.get(_station_idx);
    }


    public void setNon_stoppage_stops_stationwise(int _station_idx, ArrayList<String> value) {
        int int_dist = station_stops.get(_station_idx).getDist()-Integer.parseInt(value.get(2));
        int t_dist;
        int j=0;
        for (j = 0; j < (value.size() / 3); j++) {
            t_dist = Integer.parseInt(value.get((3 * j) + 2)) + int_dist;
            value.set(((3 * j) + 2), Integer.toString(t_dist));
        }
        this.non_stoppage_stops.put(_station_idx,value);
    }

    public ArrayList<String> getStopsAsString() {
        ArrayList<String> stop_string_arraylist=new ArrayList<String>();
        int i=0;
        for(i=0;i<this.station_stops.size();i++){
            stop_string_arraylist.add(Integer.toString(this.station_stops.get(i).getIdx()));
            stop_string_arraylist.add(this.station_stops.get(i).getCode());
            stop_string_arraylist.add(Integer.toString(this.station_stops.get(i).getDist()));
        }
            return stop_string_arraylist;
    }

    public TrainLocation getTrainLocation() {
        return trainLocation;
    }

    public void setTrainLocation(TrainLocation trainLocation) {
        this.trainLocation = trainLocation;
    }

    public boolean isDeparted() {
        return isDeparted;
    }

    public void setDeparted(boolean departed) {
        isDeparted = departed;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(String currentStation) {
        this.currentStation = currentStation;
    }

    public static class TrainLocation{
        String nextStoppageStation, nextNonStoppageStation;
        float nextStoppageStationDistance, nextNonStoppageStationDistance;
        int nextStoppageStationIdx, nextNonStoppageStationIdx;
        boolean isGrossLocationValid, isFineLocationValid;
        public TrainLocation(int nextStoppageStationIdx, String nextStoppageStation, float nextStoppageStationDistance) {
            this.nextStoppageStationIdx=nextStoppageStationIdx;
            this.nextStoppageStation = nextStoppageStation;
            this.nextStoppageStationDistance = nextStoppageStationDistance;
            this.nextNonStoppageStationIdx=0;
            this.nextNonStoppageStation=null;
            this.nextNonStoppageStationDistance=0;
            this.isGrossLocationValid=false;
            this.isFineLocationValid=false;
        }

        public TrainLocation(int nextStoppageStationIdx, int nextNonStoppageStationIdx, String nextStoppageStation, String nextNonStoppageStation, float nextStoppageStationDistance, float nextNonStoppageStationDistance) {
            this.nextStoppageStationIdx=nextStoppageStationIdx;
            this.nextNonStoppageStationIdx=nextNonStoppageStationIdx;
            this.nextStoppageStation = nextStoppageStation;
            this.nextNonStoppageStation = nextNonStoppageStation;
            this.nextStoppageStationDistance = nextStoppageStationDistance;
            this.nextNonStoppageStationDistance = nextNonStoppageStationDistance;
            this.isGrossLocationValid=false;
            this.isFineLocationValid=false;
            }




        public int getNextStoppageStationIdx() {
            return nextStoppageStationIdx;
        }

        public void setNextStoppageStationIdx(int nextStoppageStationIdx) {
            this.nextStoppageStationIdx = nextStoppageStationIdx;
        }

        public int getNextNonStoppageStationIdx() {
            return nextNonStoppageStationIdx;
        }

        public void setNextNonStoppageStationIdx(int nextNonStoppageStationIdx) {
            this.nextNonStoppageStationIdx = nextNonStoppageStationIdx;
        }

        public String getNextStoppageStation() {
            return nextStoppageStation;
        }

        public void setNextStoppageStation(String nextStoppageStation) {
            this.nextStoppageStation = nextStoppageStation;
        }

        public String getNextNonStoppageStation() {
            return nextNonStoppageStation;
        }

        public void setNextNonStoppageStation(String nextNonStoppageStation) {
            this.nextNonStoppageStation = nextNonStoppageStation;
        }

        public float getNextStoppageStationDistance() {
            return nextStoppageStationDistance;
        }

        public void setNextStoppageStationDistance(float nextStoppageStationDistance) {
            this.nextStoppageStationDistance = nextStoppageStationDistance;
        }

        public float getNextNonStoppageStationDistance() {
            return nextNonStoppageStationDistance;
        }

        public void setNextNonStoppageStationDistance(float nextNonStoppageStationDistance) {
            this.nextNonStoppageStationDistance = nextNonStoppageStationDistance;
        }

        public boolean isGrossLocationValid() {
            return isGrossLocationValid;
        }

        public void setGrossLocationValid(boolean grossLocationValid) {
            isGrossLocationValid = grossLocationValid;
        }

        public boolean isFineLocationValid() {
            return isFineLocationValid;
        }

        public void setFineLocationValid(boolean fineLocationValid) {
            isFineLocationValid = fineLocationValid;
        }
    }


}
