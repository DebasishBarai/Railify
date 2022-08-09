package com.arcticwolflabs.railify.base.dbhandler.JSON;

import android.content.Context;

import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.UpdateAPI;
import com.arcticwolflabs.railify.base.statics.Station;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONReader implements DBReader {
    Context context;

    public JSONReader(Context _context) {
        context = _context;
    }

    @Override
    public Station get_station_detail(String _station_code) {
        Station station;
        station = new Station();
        return station;
    }

    @Override
    public Train get_train_detail(int _train_code) {
        return null;
    }

    @Override
    public Journey.JourneyMinimal get_train_journey_minimal(int _train_code, String to_id, String from_id) {
        return null;
    }

    @Override
    public ArrayList<Integer> get_trains_at_station(String _station_code) {
        ArrayList<Integer> arrlst = new ArrayList<Integer>();
        return arrlst;
    }

    @Override
    public ArrayList<Station.StationMinimal> get_all_stations() {
        ArrayList<Station.StationMinimal> stations = new ArrayList<Station.StationMinimal>();
        String[] column_list = {"id", "name"};
        return stations;
    }

    @Override
    public ArrayList<Train.TrainMinimal> get_all_trains() {
        ArrayList<Train.TrainMinimal> trains= new ArrayList<Train.TrainMinimal>();
        String[] column_list = {"id", "name"};
        return trains;
    }

    @Override
    public ArrayList<Stop.StopMinimal> get_all_station_train_no(int _train_no, int _station_id_start) {
        return null;
    }

    @Override
    public UpdateAPI.UpdateInfo get_stations_update_info() {
        return null;
    }

    @Override
    public UpdateAPI.UpdateInfo get_trains_update_info() {
        return null;
    }

    @Override
    public UpdateAPI.UpdateInfo get_stations2trains_update_info() {
        return null;
    }

    @Override
    public int get_physiography(String id) {
        return -1;
    }

    @Override
    public HashMap<String, HashMap<String, ArrayList<Integer>>> get_graph() {
        return null;
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

    @Override
    public void openDatabases() {

    }

    @Override
    public void closeDatabases() {

    }

    @Override
    public String get_station_name_from_code(String code) {
        return null;
    }

    @Override
    public void unload_graph() {

    }


}
