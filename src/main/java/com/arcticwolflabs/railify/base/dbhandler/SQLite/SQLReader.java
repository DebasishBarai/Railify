package com.arcticwolflabs.railify.base.dbhandler.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.UpdateAPI;
import com.arcticwolflabs.railify.base.statics.Address;
import com.arcticwolflabs.railify.base.statics.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SQLReader implements DBReader {
    SQLiteDatabase dbS, dbT, dbS2T, dbP, dbG;
    SQLReaderHelper SQLReaderHelperStation, SQLReaderHelperTrain,
            SQLReaderHelperStation2Trains, SQLReaderHelperPhysiography, SQLReaderHelperCGraph;
    Context context;

    public SQLReader(Context _context) {
        context = _context;
        openDatabases();
    }


    public void openDatabases() {
        Calendar calendar=Calendar.getInstance();
        long t1=calendar.getTimeInMillis();
        SQLReaderHelperStation = new SQLReaderHelper(context, SQLReaderHelper.DBTYPE_STATION);
        try {
            SQLReaderHelperStation.createDatabase();
        } catch (IOException ignored) {

        }
        try {
            SQLReaderHelperStation.openDatabase();
        } catch (SQLException ignored) {

        }
        dbS = SQLReaderHelperStation.getReadableDatabase();
        calendar=Calendar.getInstance();
        long t2=calendar.getTimeInMillis();
        long t3=t2-t1;
        Log.d("Loading time", "Loading time station.db is "+t3+" ms");


        calendar=Calendar.getInstance();
        t1=calendar.getTimeInMillis();
        SQLReaderHelperTrain = new SQLReaderHelper(context, SQLReaderHelper.DBTYPE_TRAIN);
        try {
            SQLReaderHelperTrain.createDatabase();
        } catch (IOException ignored) {

        }
        try {
            SQLReaderHelperTrain.openDatabase();
        } catch (SQLException ignored) {

        }
        dbT = SQLReaderHelperTrain.getReadableDatabase();calendar=Calendar.getInstance();
        t2=calendar.getTimeInMillis();
        t3=t2-t1;
        Log.d("Loading time", "Loading time train.db is "+t3+" ms");

        calendar=Calendar.getInstance();
        t1=calendar.getTimeInMillis();
        SQLReaderHelperStation2Trains = new SQLReaderHelper(context, SQLReaderHelper.DBTYPE_STATION2TRAINS);
        try {
            SQLReaderHelperStation2Trains.createDatabase();
        } catch (IOException ignored) {

        }
        try {
            SQLReaderHelperStation2Trains.openDatabase();
        } catch (SQLException ignored) {

        }
        dbS2T = SQLReaderHelperStation2Trains.getReadableDatabase();
        t2=calendar.getTimeInMillis();
        t3=t2-t1;
        Log.d("Loading time", "Loading time stationtotrain.db is "+t3+" ms");

        calendar=Calendar.getInstance();
        t1=calendar.getTimeInMillis();
        SQLReaderHelperPhysiography = new SQLReaderHelper(context, SQLReaderHelper.DBTYPE_PHYSIOGRAPHY);
        try {
            SQLReaderHelperPhysiography.createDatabase();
        } catch (IOException ignored) {

        }
        try {
            SQLReaderHelperPhysiography.openDatabase();
        } catch (SQLException ignored) {

        }
        dbP = SQLReaderHelperPhysiography.getReadableDatabase();
        t2=calendar.getTimeInMillis();
        t3=t2-t1;
        Log.d("Loading time", "Loading time topography.db is "+t3+" ms");

        calendar=Calendar.getInstance();
        t1=calendar.getTimeInMillis();
        SQLReaderHelperCGraph = new SQLReaderHelper(context, SQLReaderHelper.DBTYPE_CGRAPH);
        try {
            SQLReaderHelperCGraph.createDatabase();
        } catch (IOException ignored) {

        }
        try {
            SQLReaderHelperCGraph.openDatabase();
        } catch (SQLException ignored) {

        }
        dbG = SQLReaderHelperCGraph.getReadableDatabase();
        t2=calendar.getTimeInMillis();
        t3=t2-t1;
        Log.d("Loading time", "Loading time graph.db is "+t3+" ms");
    }

    public void closeDatabases() {
        if (dbS != null) dbS.close();
        if (dbT != null) dbT.close();
        if (dbS2T != null) dbS2T.close();
        if (dbP != null) dbP.close();
        if (dbG != null) dbG.close();

        dbS = null;
        dbT = null;
        dbS2T = null;
        dbP = null;
        dbG = null;
    }

    @Override
    public String get_station_name_from_code(String code) {
        return null;
    }

    @Override
    public void unload_graph() {

    }

    @Override
    protected void finalize() throws Throwable {
        this.closeDatabases();
        super.finalize();
    }

    @Override
    public Station get_station_detail(String _station_code) {
        Station station;
        station = new Station();
        String[] column_list = {"id", "name", "lat", "lng"};
        String _station_code_name = "id LIKE \'" + _station_code + "\'";
        Cursor cursor = dbS.query("stations", column_list, _station_code_name, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            cursor.moveToFirst();
            station.setCode(cursor.getString(cursor.getColumnIndex("id")));
            station.setName(cursor.getString(cursor.getColumnIndex("name")));
            Address addr = new Address();
            addr.setCoordinate(cursor.getDouble(cursor.getColumnIndex("lat")), cursor.getDouble(cursor.getColumnIndex("lng")));
            station.setAddress(addr);
        } finally {
            cursor.close();
        }
        return station;
    }

    @Override
    public Train get_train_detail(int _train_code) {
        Train ctrain = new Train(_train_code, "");
        String[] column_list = {"number", "name"};
        String _train_code_name = "number LIKE " + _train_code;
        Log.d("Reader", "Train number: " + _train_code);
        Cursor cursor = dbT.query("trains", column_list, _train_code_name, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            cursor.moveToFirst();
            Log.d("Reader", " Train number query res count: " + cursor.getCount());
            ctrain.setName(cursor.getString(cursor.getColumnIndex("name")));
            String[] column_list1 = {"id", "name", "arr", "dep", "halt", "dist", "day", "pf", "idx"};
            Cursor cursor1 = dbT.query("stations_" + _train_code, column_list1, null, null, null, null, null, null);
            if (cursor1 == null) {
                return null;
            }
            try {
                int n = cursor1.getCount(); // number of stops
                Log.d("Reader", " Number of stops: " + n);
                ArrayList<Stop> sstops = new ArrayList<Stop>(n);
                cursor1.moveToPosition(-1);
                while (cursor1.moveToNext()) {
                    String cid = cursor1.getString(cursor1.getColumnIndex("id"));
                    String cname = cursor1.getString(cursor1.getColumnIndex("name"));
                    String arr = cursor1.getString(cursor1.getColumnIndex("arr"));
                    String dep = cursor1.getString(cursor1.getColumnIndex("dep"));
                    String hlt = cursor1.getString(cursor1.getColumnIndex("halt"));
                    int dist = cursor1.getInt(cursor1.getColumnIndex("dist"));
                    int day = cursor1.getInt(cursor1.getColumnIndex("day"));
                    String pf = cursor1.getString(cursor1.getColumnIndex("pf"));
                    int idx = cursor1.getInt(cursor1.getColumnIndex("idx"));
                    Stop css = new Stop(idx, cid, cname, arr, dep, day, hlt, dist, pf);
                    sstops.add(idx, css);
                }
                ctrain.setStationStops(sstops);
            } finally {
                cursor1.close();
            }

        } finally {
            cursor.close();
        }
        return ctrain;
    }

    @Override
    public Journey.JourneyMinimal get_train_journey_minimal(int _train_code, String from_id, String to_id) {
        String[] column_list = {"name"};
        String _train_code_name = "number LIKE " + _train_code;
        Log.d("Reader", "Train number: " + _train_code);
        String train_name = "";
        Cursor cursor = dbT.query("trains", column_list, _train_code_name, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            cursor.moveToFirst();
            Log.d("Reader", " Train number query res count: " + cursor.getCount());
            train_name = cursor.getString(cursor.getColumnIndex("name"));
        } finally {
            cursor.close();
        }
        String from_stn_name="",to_stn_name="";
        String arr = "", dep = "";
        String _station_code_name1 = "id LIKE \'" + from_id + "\'";
        String[] column_list1 = {"dep", "day", "idx"};
        cursor = dbT.query("stations_" + _train_code, column_list1, _station_code_name1, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        int day_from = -1, idx_from = -1;
        int day_to = -1, idx_to = -1;
        try {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return null;
            }
            from_stn_name=cursor.getString(cursor.getColumnIndex("name"));
            dep = cursor.getString(cursor.getColumnIndex("dep"));
            day_from = cursor.getInt(cursor.getColumnIndex("day"));
            idx_from = cursor.getInt(cursor.getColumnIndex("idx"));

        } finally {
            cursor.close();
        }
        String _station_code_name2 = "id LIKE \'" + to_id + "\'";
        String[] column_list2 = {"arr", "day", "idx"};
        cursor = dbT.query("stations_" + _train_code, column_list2, _station_code_name2, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return null;
            }
            to_stn_name=cursor.getString(cursor.getColumnIndex("name"));
            arr = cursor.getString(cursor.getColumnIndex("arr"));
            day_to = cursor.getInt(cursor.getColumnIndex("day"));
            idx_to = cursor.getInt(cursor.getColumnIndex("idx"));
        } finally {
            cursor.close();
        }
        if (idx_to <= idx_from) {
            return null;
        }

        return new Journey.JourneyMinimal(_train_code, train_name, from_id, from_stn_name, to_id, to_stn_name, dep, arr, day_from, day_to, idx_from, idx_to);
    }

    @Override
    public ArrayList<Integer> get_trains_at_station(String _station_code) {
        ArrayList<Integer> arrlst = new ArrayList<Integer>();
        String[] column_list = {"number"};
        String _table_name = "trains_at_" + _station_code;
        Cursor cursor = null;
        try {
            cursor = dbS2T.query(_table_name, column_list, null, null, null, null, null, null);
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                arrlst.add(cursor.getInt(cursor.getColumnIndex("number")));
            }
        } finally {
            if(cursor !=null) {
                cursor.close();
            }
        }
        return arrlst;
    }

    @Override
    public ArrayList<Station.StationMinimal> get_all_stations() {
        Calendar calendar;
        calendar=Calendar.getInstance();
        long t4=calendar.getTimeInMillis();
                ArrayList<Station.StationMinimal> stations = new ArrayList<Station.StationMinimal>();
        String[] column_list = {"id", "name"};
        Cursor cursor = dbS.query("stations", column_list, null, null, null, null, null, null);
        try {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                stations.add(new Station.StationMinimal(cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name"))));
            }
        } finally {
            cursor.close();
        }
        calendar=Calendar.getInstance();
        long t5=calendar.getTimeInMillis();
        long t6=t5-t4;
        Log.d("Loading time", "Loading time stationdb t6 is "+t6+" ms");
        return stations;
    }

    @Override
    public ArrayList<Train.TrainMinimal> get_all_trains() {
        ArrayList<Train.TrainMinimal> trains = new ArrayList<Train.TrainMinimal>();
        String[] column_list = {"number", "name"};
        Cursor cursor = dbT.query("trains", column_list, null, null, null, null, null, null);
        try {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                trains.add(new Train.TrainMinimal(cursor.getString(cursor.getColumnIndex("number")),
                        cursor.getString(cursor.getColumnIndex("name"))));
            }
        } finally {
            cursor.close();
        }
        return trains;
    }

    @Override
    public ArrayList<Stop.StopMinimal> get_all_station_train_no(int _train_no, int _station_id_start) {
        ArrayList<Stop.StopMinimal> stops = new ArrayList<Stop.StopMinimal>();
        String[] column_list = {"id", "name", "idx"};
        Cursor cursor = dbT.query("stations_" + Integer.toString(_train_no), column_list, null, null, null, null, null, null);
        try {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("idx"))) >= _station_id_start) {
                    stops.add(new Stop.StopMinimal(Integer.parseInt(cursor.getString(cursor.getColumnIndex("idx"))),
                            cursor.getString(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name"))));
                }
            }
        } finally {
            cursor.close();
        }
        return stops;
    }

    @Override
    public UpdateAPI.UpdateInfo get_stations_update_info() {
        String[] column_list = {"time", "info", "target", "url"};
        UpdateAPI.UpdateInfo updinfo = null;
        Cursor cursor = dbS.query("timestamp", column_list, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            updinfo = new UpdateAPI.UpdateInfo();
            updinfo.setTimestamp(cursor.getDouble(cursor.getColumnIndex("time")));
            updinfo.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            updinfo.setTarget(cursor.getString(cursor.getColumnIndex("target")));
            updinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        } finally {
            cursor.close();
        }
        return updinfo;
    }

    @Override
    public UpdateAPI.UpdateInfo get_trains_update_info() {
        String[] column_list = {"time", "info", "target", "url"};
        UpdateAPI.UpdateInfo updinfo = null;
        Cursor cursor = dbT.query("timestamp", column_list, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            updinfo = new UpdateAPI.UpdateInfo();
            updinfo.setTimestamp(cursor.getDouble(cursor.getColumnIndex("time")));
            updinfo.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            updinfo.setTarget(cursor.getString(cursor.getColumnIndex("target")));
            updinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        } finally {
            cursor.close();
        }
        return updinfo;
    }

    @Override
    public UpdateAPI.UpdateInfo get_stations2trains_update_info() {
        String[] column_list = {"time", "info", "target", "url"};
        UpdateAPI.UpdateInfo updinfo = null;
        Cursor cursor = dbS2T.query("timestamp", column_list, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            updinfo = new UpdateAPI.UpdateInfo();
            updinfo.setTimestamp(cursor.getDouble(cursor.getColumnIndex("time")));
            updinfo.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            updinfo.setTarget(cursor.getString(cursor.getColumnIndex("target")));
            updinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));

        } finally {
            cursor.close();
        }
        return updinfo;
    }

    @Override
    public int get_physiography(String id) {
        String label = "";
        int val = -1;
        String[] column_list = {"id", "val"};
        String _station_id = "id LIKE \'" + id + "\'";

        Cursor cursor = dbP.query("physiography", column_list, _station_id, null, null, null, null);
        try {
            cursor.moveToFirst();
            val = cursor.getInt(cursor.getColumnIndex("val"));
        } finally {
            cursor.close();
        }
        return val;
    }

    @Override
    public HashMap<String, HashMap<String, ArrayList<Integer>>> get_graph() {

        HashMap<String, HashMap<String, ArrayList<Integer>>> graph = new HashMap<>();
        String[] column_list = {"id"}, column_list2 = {"id", "dist"};
        Cursor c = dbG.query("xt", column_list, null, null, null, null, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                HashMap<String, ArrayList<Integer>> cmap = new HashMap<String, ArrayList<Integer>>();
                String tname = "y_"+c.getString(0);
                Cursor c1 = dbG.query(tname, column_list2, null, null, null, null, null);
                ArrayList<Integer> dist=new ArrayList<Integer>();
                if (c1.moveToFirst()) {
                    while (!c1.isAfterLast()) {
                        dist.add(c1.getInt(1));
                        cmap.put(c1.getString(0), dist);
                        c1.moveToNext();
                    }
                }
                c1.close();
                graph.put(c.getString(0), cmap);
                c.moveToNext();
            }
        }
        c.close();
        return graph;
    }


    @Override
    public void load_s2t() {

    }

    @Override
    public void add_to_saved_pnr(long pnr_num) {

    }

    @Override
    public ArrayList<Long> get_saved_pnr() {
        return null;
    }


}
