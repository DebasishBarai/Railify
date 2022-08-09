package com.arcticwolflabs.railify.base.dbhandler;

import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.UpdateAPI;
import com.arcticwolflabs.railify.base.statics.Station;

import java.util.ArrayList;
import java.util.HashMap;

public interface DBReader {
    public Station get_station_detail(String _station_code);

    public Train get_train_detail(int _train_code);

    Journey.JourneyMinimal get_train_journey_minimal(int _train_code, String to_id, String from_id);

    public ArrayList<Integer> get_trains_at_station(String _station_code);

    public ArrayList<Station.StationMinimal> get_all_stations();

    public ArrayList<Train.TrainMinimal> get_all_trains();

    public ArrayList<Stop.StopMinimal> get_all_station_train_no(int _train_no, int _station_id_start);

    public UpdateAPI.UpdateInfo get_stations_update_info();

    public UpdateAPI.UpdateInfo get_trains_update_info();

    public UpdateAPI.UpdateInfo get_stations2trains_update_info();

    public int get_physiography(String id);

    public HashMap<String, HashMap<String, ArrayList<Integer>>> get_graph();

    public void load_s2t();

    public void add_to_saved_pnr(long pnr_num);

    public ArrayList<Long> get_saved_pnr();

    public void openDatabases();

    public void closeDatabases();

    public String get_station_name_from_code(String code);

    public void unload_graph();
}


