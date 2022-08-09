package com.arcticwolflabs.railify.core;

import android.util.Log;

import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Place;
import com.arcticwolflabs.railify.base.dynamics.TRRNonStoppageStation;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.dynamics.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CacheSystem {

    private int cache_store_size_kb;
    private int cache_store_time_sec;

    public static class Pair<T, U> {
        public T first;
        public U second;

        public Pair(T _first, U _second) {
            this.first = _first;
            this.second = _second;
        }
    }


    public static class S2SQuery {
        private String from_id;
        private String to_id;
        private boolean direct;

        public S2SQuery(String from_id, String to_id, boolean direct) {
            this.from_id = from_id;
            this.to_id = to_id;
            this.direct=direct;
        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }

        public String getTo_id() {
            return to_id;
        }

        public void setTo_id(String to_id) {
            this.to_id = to_id;
        }
        // maybe later use date

        public String getDirect(){
            if(direct==true){
                return "true";
            } else {
                return "false";
            }
        }
        public void setDirect(boolean direct){
            this.direct=direct;
        }
    }

    public static interface Cache<T, U> {

        public U get(T query);

        public void set(T query, U res);

        public String compute_hash(T query);

        public void clear();
    }

    public static class TRQuery {
        private int train_no;

        public TRQuery(int train_no) {
            this.train_no = train_no;
        }

        public int getTrain_no() {
            return train_no;
        }

        public void setTrain_no(int train_no) {
            this.train_no = train_no;
        }
    }

    public static class CacheS2S implements Cache<S2SQuery, ArrayList<Journey.JourneyMinimal[]>> {
        private HashMap<String, ArrayList<Journey.JourneyMinimal[]>> store;

        public CacheS2S() {
            this.store = new HashMap<>();
        }

        @Override
        public ArrayList<Journey.JourneyMinimal[]> get(S2SQuery query) {
            String hash = compute_hash(query);
            return store.get(hash);
        }

        @Override
        public void set(S2SQuery query, ArrayList<Journey.JourneyMinimal[]> res) {
            String hash = compute_hash(query);
            ArrayList<Journey.JourneyMinimal[]> a=new ArrayList<Journey.JourneyMinimal[]>();
            a=res;
            store.put(hash, a);
        }

        @Override
        public String compute_hash(S2SQuery query) {
            return query.getFrom_id() + "_" + query.getTo_id()+"_"+query.getDirect();
        }

        @Override
        public void clear() {
            store.clear();
        }
    }

    public static class TRobject{
        Train train;
        ArrayList<ArrayList<TRRNonStoppageStation>> trrNonStoppageStationRoot;
        public TRobject(Train _train, ArrayList<ArrayList<TRRNonStoppageStation>> _trrNonStoppageStationRoot){
            train=_train;
            trrNonStoppageStationRoot=_trrNonStoppageStationRoot;
        }

        public void setTrain(Train train) {
            this.train = train;
        }

        public Train getTrain() {
            return train;
        }

        public void setTrrNonStoppageStationRoot(ArrayList<ArrayList<TRRNonStoppageStation>> trrNonStoppageStationRoot) {
            this.trrNonStoppageStationRoot = trrNonStoppageStationRoot;
        }

        public ArrayList<ArrayList<TRRNonStoppageStation>> getTrrNonStoppageStationRoot() {
            return trrNonStoppageStationRoot;
        }
    }

    public static class CacheTR implements Cache<TRQuery, TRobject> {

        private HashMap<String, TRobject> store;

        public CacheTR() {
            this.store = new HashMap<String, TRobject>();
        }

        @Override
        public TRobject get(TRQuery query) {
            String hash = compute_hash(query);
            return store.get(hash);
        }

        @Override
        public void set(TRQuery query, TRobject res) {
            String hash = compute_hash(query);
            store.put(hash, res);
        }

        @Override
        public String compute_hash(TRQuery query) {
            return String.valueOf(query.getTrain_no());
        }

        @Override
        public void clear() {
            store.clear();
        }
    }


    public static class CacheAVL implements Cache<String, String> {

        private Map<String, Pair<String, Double>> store;
        private Double validity_time;

        public CacheAVL(Double validity_time) {
            this.store = new HashMap<>();
            this.validity_time = validity_time;
        }

        @Override
        public String get(String query) {
            String hash = compute_hash(query);
            Pair<String, Double> val = store.get(hash);
            if (val == null) {
                return null;
            }
            Double now_time = System.currentTimeMillis() / 1000.0;
            if ((now_time - val.second) > this.validity_time) {
                store.remove(hash);
                return null;
            }
            return val.first;
        }

        @Override
        public void set(String query, String res) {
            String hash = compute_hash(query);
            Double now_time = System.currentTimeMillis() / 1000.0;
            store.put(hash, new Pair<>(res, now_time));
        }

        @Override
        public String compute_hash(String query) {
            return String.valueOf(query);
        }

        @Override
        public void clear() {
            store.clear();
        }
    }

    public static class CacheWeather implements Cache<String, Weather> {

        private Map<String, Pair<Weather, Double>> store;

        public Double getValidity_time() {
            return validity_time;
        }

        public void setValidity_time(Double validity_time) {
            this.validity_time = validity_time;
        }

        private Double validity_time;

        public CacheWeather(Double validity_time) {
            this.store = new HashMap<>();
            this.validity_time = validity_time;
        }

        @Override
        public Weather get(String query) {
            String hash = compute_hash(query);
            Pair<Weather, Double> val = store.get(hash);
            if (val == null) {
                return null;
            }
            double now_time = System.currentTimeMillis() / 1000.0;
            Double tmp = (now_time - val.second);
            if ( tmp> this.validity_time) {
                store.remove(hash);
                return null;
            }
            return val.first;
        }

        @Override
        public void set(String query, Weather res) {
            String hash = compute_hash(query);
            Double now_time = System.currentTimeMillis() / 1000.0;
            store.put(hash, new Pair<>(res, now_time));
        }

        @Override
        public String compute_hash(String query) {
            return String.valueOf(query);
        }

        @Override
        public void clear() {
            store.clear();
        }
    }

    public static class CachePlace implements Cache<String, ArrayList<Place>> {

        private Map<String, ArrayList<Place>> store;

        public CachePlace() {
            this.store = new HashMap<>();
        }


        @Override
        public ArrayList<Place> get(String query) {
            String hash = compute_hash(query);
            ArrayList<Place> val = store.get(hash);
            if (val == null) {
                return null;
            }
            return val;
        }

        @Override
        public void set(String query, ArrayList<Place> res) {
            String hash = compute_hash(query);
            store.put(hash, new ArrayList<>(res));
        }

        @Override
        public String compute_hash(String query) {
            return String.valueOf(query);
        }

        @Override
        public void clear() {
            store.clear();
        }
    }


}
