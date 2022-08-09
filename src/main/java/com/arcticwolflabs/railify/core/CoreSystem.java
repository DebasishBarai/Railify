package com.arcticwolflabs.railify.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;

import com.arcticwolflabs.railify.Railify;
import com.arcticwolflabs.railify.base.dbhandler.Bin.BinReader;
import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.statics.Station;
import com.arcticwolflabs.railify.core.utils.Graph;
import com.arcticwolflabs.railify.core.utils.KDTree;
import com.arcticwolflabs.railify.core.utils.QuadTree;
import com.arcticwolflabs.railify.ui.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;


public class CoreSystem {

    long t1;
    long t2;
    long t3;
    long t4;

    private DBReader dbreader;
    private Context context;
    private QuadTree qtree;
    private KDTree ktree;
    private ArrayList<Station.StationMinimal> stations;
    private ArrayList<Train.TrainMinimal> trains;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> cgraph;
    private boolean cgraphLoadingComplete;

    private static final ExecutorService threadpool = Executors.newFixedThreadPool(3);

    //Following variable are declared exclusively to be used in get_direct_stops method. The variables are declared here to reduce the stack size as declaring inside the method is creating variables for each recursion.
    int max_trn;
    int max_stn;
    int max_idx_one;
    int max_idx_two;
    int stn_no;
    int idx_one;
    int idx_two;
    int dist_one;
    int dist_two;
    int diff_dist;
    Train train;
    ArrayList<Integer> com_trns;
    ArrayList<String> stops;
    ArrayList<String> t_stop;
    int mid_idx;
    ArrayList<String> stops_one;
    ArrayList<String> stops_two;
    //Following variable are declared exclusively to be used in get_direct_stops method. The variables are declared here to reduce the stack size as declaring inside the method is creating variables for each recursion.


    private class StationsLoader implements Callable {

        public StationsLoader() {
        }

        @Override
        public ArrayList<Station.StationMinimal> call() {
            ArrayList<Station.StationMinimal> output = null;
            try {
                output = loader();
            } catch (InterruptedException ex) {
            }

            return output;
        }

        private ArrayList<Station.StationMinimal> loader() throws InterruptedException {
            return dbreader.get_all_stations();
        }
    }

    private class TrainsLoader implements Callable {

        public TrainsLoader() {
        }

        @Override
        public ArrayList<Train.TrainMinimal> call() {
            ArrayList<Train.TrainMinimal> output = null;
            try {
                output = loader();
            } catch (InterruptedException ex) {
            }

            return output;
        }

        private ArrayList<Train.TrainMinimal> loader() throws InterruptedException {
            return dbreader.get_all_trains();
        }
    }

    private class GraphLoader implements Callable {

        public GraphLoader() {
        }

        @Override
        public HashMap<String, HashMap<String, ArrayList<Integer>>> call() {
            HashMap<String, HashMap<String, ArrayList<Integer>>> output = null;
            try {
                output = loader();
            } catch (InterruptedException ex) {
            }

            return output;
        }

        private HashMap<String, HashMap<String, ArrayList<Integer>>> loader() throws InterruptedException {
            return dbreader.get_graph();
        }
    }

    private StationsLoader stloader;
    private TrainsLoader trnsloader;
    private GraphLoader grploader;


    public CoreSystem(Context _context) {
        dbreader = new BinReader(_context);
        context = _context;
        cgraphLoadingComplete=false;

        t1 = Calendar.getInstance().getTimeInMillis();
        stations = dbreader.get_all_stations();
        t2 = Calendar.getInstance().getTimeInMillis();
        trains = dbreader.get_all_trains();
        t3 = Calendar.getInstance().getTimeInMillis();
        //cgraph = dbreader.get_graph();
        //Log.d("Core system", "CoreSystem: cgraph size " + cgraph.size());
        t4 = Calendar.getInstance().getTimeInMillis();
        Log.d("Loading time", "loading time station train cgraph coresystem are " + (t2-t1)+","+(t3-t2) +","+ (t4-t3) + " ms");
/*
        stloader = new StationsLoader();
        trnsloader = new TrainsLoader();
        grploader = new GraphLoader();

        Future stations_f = threadpool.submit(stloader);
        Future trains_f = threadpool.submit(trnsloader);
        Future cgraph_f = threadpool.submit(grploader);*/
/*
        while(!stations_f.isDone() ||!trains_f.isDone() ||!cgraph_f.isDone()) {

            try {
                stations = (ArrayList<Station.StationMinimal>) stations_f.get();
                trains = (ArrayList<Train.TrainMinimal>) trains_f.get();
                t1= Calendar.getInstance().getTimeInMillis();
                cgraph = (HashMap<String, HashMap<String, Double>>) cgraph_f.get();
                t2=Calendar.getInstance().getTimeInMillis();
                t3=t2-t1;
                Log.d("Loading time", "loading time cgraph coresystem is "+t3+" ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("NDT", "Gathered " + stations.size() + " stations.");
        qtree = new QuadTree(stations.size());
        ktree = new KDTree();
        //buildQuadTree();
        // buildKDTree();
        */
    }
    public void loadGraph() {

        cgraph = dbreader.get_graph();
        cgraphLoadingComplete=true;
    }

    public void unloadGraph(){
        cgraph=null;
        dbreader.unload_graph();
    }

    public boolean isCgraphLoadingComplete() {
        return cgraphLoadingComplete;
    }

    public void openDB() {
        dbreader.openDatabases();
    }

    public void closeDB() {
        dbreader.closeDatabases();
    }

    public DBReader getDBReader() {
        return dbreader;
    }

    public ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> searchOneStopRoutes(String start, String stop, Integer dist) {
        return Graph.find_paths(this.cgraph, start, stop, dist);
    }

    private void buildQuadTree() {
        int sz = stations.size();
        for (int i = 0; i < sz; ++i) {
            Station.StationMinimal cstation = stations.get(i);
            Location location = dbreader.get_station_detail(cstation.getCode()).getAddress().getLocation();
            double x[] = new double[2];
            x[0] = location.getLatitude();
            x[1] = location.getLongitude();
            qtree.add(x, i);
        }
    }

    private void buildKDTree() {
        int sz = stations.size();
        for (int i = 0; i < sz; ++i) {
            Station.StationMinimal cstation = stations.get(i);
            Location location = dbreader.get_station_detail(cstation.getCode()).getAddress().getLocation();
            KDTree.Point2D p = new KDTree.Point2D(location.getLatitude(), location.getLongitude(), i);
            ktree.insert(p);
        }
    }

    //Copy Assets when the app starts for the first time

    public void copyAssets() {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(context.getFilesDir(), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    // find neighbours
    public ArrayList<String> find_neighbouring_stations(String id) {
        ArrayList<String> arrlst = new ArrayList<String>();
        Location location = dbreader.get_station_detail(id).getAddress().getLocation();
        double x[] = new double[2];
        x[0] = location.getLatitude();
        x[1] = location.getLongitude();
        QuadTree.QuadNode qn = qtree.find_nearest(x);
        arrlst.add(stations.get(qn.getID()).getCode());
        return arrlst;
    }

    private <T> ArrayList<T> intersection(ArrayList<T> list1, ArrayList<T> list2) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    public ArrayList<Integer> get_trains_between_stations(String id_from, String id_to) {
        ArrayList<Integer> from_list = dbreader.get_trains_at_station(id_from);
        ArrayList<Integer> to_list = dbreader.get_trains_at_station(id_to);
        ArrayList<Integer> intrsct = intersection(from_list, to_list);
        ArrayList<Integer> valid_intrsct = new ArrayList<Integer>();
        for (int i = 0; i < intrsct.size(); ++i) {
            if (dbreader.get_train_journey_minimal(intrsct.get(i), id_from, id_to) != null) {
                valid_intrsct.add(intrsct.get(i));
            }
        }
        return valid_intrsct;
    }

    public ArrayList<Journey.JourneyMinimal[]> get_direct_journeys_between_stations(String id_from, String id_to) {

        ArrayList<Journey.JourneyMinimal[]> jrnys = new ArrayList<Journey.JourneyMinimal[]>();
        ArrayList<Integer> from_list = dbreader.get_trains_at_station(id_from);
        ArrayList<Integer> to_list = dbreader.get_trains_at_station(id_to);
        ArrayList<Integer> intrsct = intersection(from_list, to_list);
        ArrayList<Integer> valid_intrsct = new ArrayList<Integer>();
        for (int i = 0; i < intrsct.size(); ++i) {
            Journey.JourneyMinimal cjrny = dbreader.get_train_journey_minimal(intrsct.get(i), id_from, id_to);
            if (cjrny != null) {
                Journey.JourneyMinimal[] djrny = new Journey.JourneyMinimal[1];
                djrny[0] = cjrny;
                jrnys.add(djrny);
            }
        }
        return jrnys;
    }

    public ArrayList<Journey.JourneyMinimal[]> get_multiple_journeys_between_stations(String id_from, String id_to, ArrayList<Journey.JourneyMinimal[]> direct) {
        ArrayList<Journey.JourneyMinimal[]> jrnys = new ArrayList<Journey.JourneyMinimal[]>();
        int jrny_dist = 0, jrny_t = 0, avg_jrny_dist, avg_jrny_t;
        for (int i = 0; i < direct.size(); ++i) {
            Journey.JourneyMinimal cjrny = direct.get(i)[0];
            if (cjrny != null) {
                jrny_t = jrny_t + Tools.get_travel_time(cjrny.getDep(), cjrny.getArr(), cjrny.getFrom_day(), cjrny.getTo_day());
            }
        }
        if (direct.size() > 0) {
            avg_jrny_t = jrny_t / direct.size();
        } else {
            avg_jrny_t = 0;
        }

        ArrayList<Journey.JourneyMinimal[]> one_stop_jrnys = new ArrayList<Journey.JourneyMinimal[]>();
        one_stop_jrnys = get_jrnys_one_stop_between_stations(id_from, id_to, avg_jrny_t);

        jrnys.addAll(one_stop_jrnys);

        return jrnys;
    }

    public String getStationNameFromCode(String code) {
        return dbreader.get_station_name_from_code(code);
    }

    public ArrayList<Journey.JourneyMinimal[]> get_jrnys_one_stop_between_stations(String id_from, String id_to, int avg_direct_time) {

        // TO BE IMPLEMENTED
        ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> routes = searchOneStopRoutes(id_from, id_to, 0);
        ArrayList<Journey.JourneyMinimal[]> valid_jrnys = new ArrayList<>();
        if (routes.size() > 0) {
            int max_routes = routes.size();
            for (int i = 0; i < max_routes; i++) {
                String id_via = routes.get(i).first;

                ArrayList<Integer> from_list = dbreader.get_trains_at_station(id_from);
                ArrayList<Integer> via_list = dbreader.get_trains_at_station(id_via);
                ArrayList<Integer> to_list = dbreader.get_trains_at_station(id_to);

                ArrayList<Integer> intrsct1 = intersection(from_list, via_list);
                ArrayList<Integer> intrsct2 = intersection(via_list, to_list);
                ArrayList<Integer> intrsct3 = intersection(from_list, to_list);
                intrsct1.removeAll(intrsct3);
                intrsct2.removeAll(intrsct3);

                ArrayList<Integer> valid_intrsct1 = new ArrayList<Integer>();
                ArrayList<Integer> valid_intrsct2 = new ArrayList<Integer>();

                ArrayList<Journey.JourneyMinimal> intrsct_jrny1 = new ArrayList<Journey.JourneyMinimal>();
                ArrayList<Journey.JourneyMinimal> intrsct_jrny2 = new ArrayList<Journey.JourneyMinimal>();

                if ((intrsct1.size() != 0) && (intrsct2.size() != 0)) {


                    for (int j = 0; j < intrsct1.size(); ++j) {
                        Journey.JourneyMinimal jrny1 = dbreader.get_train_journey_minimal(intrsct1.get(j), id_from, id_via);
                        if (jrny1 != null) {
                            intrsct_jrny1.add(jrny1);
                        }
                    }

                    for (int j = 0; j < intrsct2.size(); ++j) {
                        Journey.JourneyMinimal jrny2 = dbreader.get_train_journey_minimal(intrsct2.get(j), id_via, id_to);
                        if (jrny2 != null) {
                            intrsct_jrny2.add(jrny2);
                        }
                    }

                    if ((intrsct_jrny1.size() != 0) && (intrsct_jrny2.size() != 0)) {

                        for (int j = 0; j < intrsct_jrny1.size(); ++j) {
                            int tr_t1 = Tools.get_travel_time(intrsct_jrny1.get(j).getDep(), intrsct_jrny1.get(j).getArr(), intrsct_jrny1.get(j).getFrom_day(), intrsct_jrny1.get(j).getTo_day());
                            Journey.JourneyMinimal[] djrny = new Journey.JourneyMinimal[2];
                            djrny[0] = intrsct_jrny1.get(j);
                            Journey.JourneyMinimal nearest_second = null;
                            int nearest_tr_t = 0;
                            for (int k = 0; k < intrsct_jrny2.size(); ++k) {
                                int halt_t = Tools.get_travel_time(intrsct_jrny1.get(j).getArr(), intrsct_jrny2.get(k).getDep(), 1, 1);
                                if (halt_t < 0) {
                                    halt_t = Tools.get_travel_time(intrsct_jrny1.get(j).getArr(), intrsct_jrny2.get(k).getDep(), 1, 2);
                                }
                                if (halt_t > 20) {
                                    int tr_t2 = Tools.get_travel_time(intrsct_jrny2.get(k).getDep(), intrsct_jrny2.get(k).getArr(), intrsct_jrny2.get(k).getFrom_day(), intrsct_jrny2.get(k).getTo_day());
                                    int tot_tr_t = tr_t1 + tr_t2 + halt_t;
                                    if ((avg_direct_time == 0) || (tot_tr_t < ((avg_direct_time * 1.5) + 50))) {
                                        if (nearest_second == null) {
                                            nearest_second = intrsct_jrny2.get(k);
                                            nearest_tr_t = tot_tr_t;
                                        } else {
                                            if (nearest_tr_t > tot_tr_t) {
                                                nearest_second = intrsct_jrny2.get(k);
                                                nearest_tr_t = tot_tr_t;
                                            }
                                        }
                                    }

                                }
                            }
                            if (nearest_second != null) {
                                djrny[1] = nearest_second;
                                valid_jrnys.add(djrny);
                            }
                        }
                    }
                }
            }
        }
        return valid_jrnys;
    }

    public String get_via_stations_one_stop_between_stations(String id_from, String id_to) {

        ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> routes = searchOneStopRoutes(id_from, id_to, 0);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < routes.size(); ++i) {
            res.append(" ").append(routes.get(i).first).append(" (").append(String.format("%.2f", routes.get(i).second)).append(" km), ");
        }
        if (routes.size() >= 1) {
            res = new StringBuilder(res.substring(0, res.length() - 2));
        }
        return res.toString();
    }

    public ArrayList<Journey.JourneyMinimal> get_journeys_one_stop_between_stations(String id_from, String id_to) {

        // to change everything below
        ArrayList<Journey.JourneyMinimal> jrnys = new ArrayList<Journey.JourneyMinimal>();
        ArrayList<Integer> from_list = dbreader.get_trains_at_station(id_from);
        ArrayList<Integer> to_list = dbreader.get_trains_at_station(id_to);
        ArrayList<Integer> intrsct = intersection(from_list, to_list);
        ArrayList<Integer> valid_intrsct = new ArrayList<Integer>();
        for (int i = 0; i < intrsct.size(); ++i) {
            Journey.JourneyMinimal cjrny = dbreader.get_train_journey_minimal(intrsct.get(i), id_from, id_to);
            if (cjrny != null) {
                jrnys.add(cjrny);
            }
        }
        return jrnys;
    }

    public Train get_train_details(int _train_no) {

        return dbreader.get_train_detail(_train_no);
    }

    public ArrayList<Station.StationMinimal> getStations() {
        return stations;
    }

    public ArrayList<Train.TrainMinimal> getTrains() {
        return trains;
    }

    public ArrayList<Stop.StopMinimal> core_get_all_station_train_no(int _train_no, int _station_id_start) {
        return dbreader.get_all_station_train_no(_train_no, _station_id_start);
    }

    public int get_place_physiography(String station_id) {
        return dbreader.get_physiography(station_id);
    }

    public void add_pnr_to_saved_pnr(long pnr_num) {
        dbreader.add_to_saved_pnr(pnr_num);
    }

    public ArrayList<Long> get_saved_pnrs() {
        return dbreader.get_saved_pnr();
    }

    //following codes are written to enable non stoppage station display functionality

    public ArrayList<String> get_direct_stops(String stn_code_one, String stn_code_two, int dist) {
        Log.d("get_round_route", "get_round_route: direct stop " + stn_code_one + " and " + stn_code_two + " and " + Integer.toString(dist));
        com_trns = get_trains_between_stations(stn_code_one, stn_code_two);
        if (com_trns.size() > 0) {
            int i = 0;
            int j = 0;
            max_trn = 0;
            max_stn = 0;
            max_idx_one = 0;
            max_idx_two = 0;
            stn_no = 0;
            idx_one = 0;
            idx_two = 0;
            dist_one = 0;
            dist_two = 0;
            diff_dist = 0;
            ArrayList<String> max_stop = new ArrayList<String>();
            for (i = 0; i < com_trns.size(); i++) {
                train = get_train_details(com_trns.get(i));
                stops = train.getStopsAsString();
                /*for (j = 0; j < (stops.size()/3); j++) {
                    if (stops.get((3 * j) + 1).equalsIgnoreCase(stn_code_one)) {
                        idx_one = Integer.parseInt(stops.get(3 * j));
                        dist_one = Integer.parseInt(stops.get((3 * j) + 2));
                    } else {
                        if (stops.get((3 * j) + 1).equalsIgnoreCase(stn_code_two)) {
                            idx_two = Integer.parseInt(stops.get(3 * j));
                            dist_two = Integer.parseInt(stops.get((3 * j) + 2));
                        }
                    }
                }*/
                int k = 0;
                int l = 0;
                while ((stops.contains(stn_code_one)) || (stops.contains(stn_code_two))) {
                    if (stops.contains(stn_code_one)) {
                        k = stops.indexOf(stn_code_one);
                        idx_one = Integer.parseInt(stops.get(k - 1));
                        dist_one = Integer.parseInt(stops.get(k + 1));
                    }
                    if (stops.contains(stn_code_two)) {
                        l = stops.indexOf(stn_code_two);
                        idx_two = Integer.parseInt(stops.get(l - 1));
                        dist_two = Integer.parseInt(stops.get(l + 1));
                    }
                    stn_no = abs(idx_one - idx_two) + 1;
                    diff_dist = abs(dist - abs(dist_one - dist_two));
                    if (((dist >= 10) && (diff_dist <= 4)) || ((dist < 10) && (diff_dist <= 1))) {
                        if (stn_no > max_stn) {
                            max_idx_one = idx_one;
                            max_idx_two = idx_two;
                            max_trn = train.getTrainNum();
                            max_stn = stn_no;

                        }
                        break;
                    } else {
                        if (k < l) {
                            stops.remove(k - 1);
                            stops.remove(k - 1);
                            stops.remove(k - 1);
                            if (!(stops.contains(stn_code_one))) {
                                stops.remove(l - 4);
                                stops.remove(l - 4);
                                stops.remove(l - 4);
                            }
                        } else {
                            stops.remove(l - 1);
                            stops.remove(l - 1);
                            stops.remove(l - 1);
                            if (!(stops.contains(stn_code_two))) {
                                stops.remove(k - 4);
                                stops.remove(k - 4);
                                stops.remove(k - 4);
                            }
                        }
                    }
                }
            }

            train = get_train_details(max_trn);
            stops = train.getStopsAsString();
            for (j = min(max_idx_one, max_idx_two); j <= max(max_idx_one, max_idx_two); j++) {
                max_stop.add(stops.get(3 * j));
                max_stop.add(stops.get((3 * j) + 1));
                max_stop.add(stops.get((3 * j) + 2));
            }
            if (max_stn == 2) {
                if (max_stop.get(0).equalsIgnoreCase(stn_code_two)) {

                    t_stop = new ArrayList<String>();
                    t_stop.add(max_stop.get(3));
                    t_stop.add(max_stop.get(4));
                    t_stop.add(max_stop.get(5));
                    t_stop.add(max_stop.get(0));
                    t_stop.add(max_stop.get(1));
                    t_stop.add(max_stop.get(2));
                    max_stop = new ArrayList<>(t_stop);
                }
                if (dist <= 25) {
                    return get_round_route(max_stop, dist);
                } else {
                    ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> graph_route = searchOneStopRoutes(max_stop.get(1), max_stop.get(4), dist);
                    if (graph_route == null) {
                        return get_round_route(max_stop, dist);

                    } else {
                        int t_dist;
                        /*int graph_diff = dist - (graph_route.get(0).second.get(0) + graph_route.get(0).second.get(1));
                        if (!(graph_diff == 0)) {
                            t_dist = graph_route.get(0).second.get(0) + ((int) (graph_diff / 2));
                            graph_route.get(0).second.set(0, t_dist);
                            t_dist = graph_route.get(0).second.get(1) + ((int) (graph_diff / 2));
                            graph_route.get(0).second.set(1, t_dist);
                        }*/
                        ArrayList<String> graph_route_one = get_direct_stops(max_stop.get(1), graph_route.get(0).first, graph_route.get(0).second.get(0));
                        ArrayList<String> graph_route_two = get_direct_stops(graph_route.get(0).first, max_stop.get(4), graph_route.get(0).second.get(1));
                        for (j = 0; j < 3; j++) {
                            graph_route_two.remove(0);
                        }
                        for (j = 0; j < (graph_route_two.size() / 3); j++) {
                            t_dist = Integer.parseInt(graph_route_two.get((3 * j) + 2)) + graph_route.get(0).second.get(0);
                            graph_route_two.set(((3 * j) + 2), Integer.toString(t_dist));
                        }
                        for (j = 0; j < graph_route_two.size(); j++) {
                            graph_route_one.add(graph_route_two.get(j));
                        }
                        return get_round_route(graph_route_one, dist);

                    }
                }
            } else {
                ArrayList<String> res = new ArrayList<String>(max_stop);
                for (i = 0; i < ((max_stop.size() / 3) - 1); i++) {
                    int mid_dist = abs(Integer.parseInt(max_stop.get((3 * i) + 2)) - Integer.parseInt(max_stop.get((3 * i) + 5)));
                    ArrayList<String> mid_stops = get_direct_stops(max_stop.get((3 * i) + 1), max_stop.get((3 * i) + 4), mid_dist);
                    int int_dist = Integer.parseInt(max_stop.get((3 * i) + 2)) - Integer.parseInt(mid_stops.get(2));
                    int t_dist;
                    for (j = 0; j < (mid_stops.size() / 3); j++) {
                        t_dist = Integer.parseInt(mid_stops.get((3 * j) + 2)) + int_dist;
                        mid_stops.set(((3 * j) + 2), Integer.toString(t_dist));
                    }
                    mid_idx = res.indexOf(max_stop.get(3 * i + 1)) - 1;
                    stops_one = new ArrayList<String>();
                    stops_two = new ArrayList<String>();
                    for (j = 0; j < res.size(); j++) {
                        if (j < mid_idx) {
                            stops_one.add(res.get(j));
                        }
                        if (j > (mid_idx + 5)) {
                            stops_two.add(res.get(j));
                        }
                    }
                    res.clear();
                    for (j = 0; j < stops_one.size(); j++) {
                        res.add(stops_one.get(j));
                    }
                    for (j = 0; j < mid_stops.size(); j++) {
                        res.add(mid_stops.get(j));
                    }
                    for (j = 0; j < stops_two.size(); j++) {
                        res.add(stops_two.get(j));
                    }
                }
                return get_round_route(res, dist);
            }

        }
        return null;
    }

    public ArrayList<String> get_round_route(ArrayList<String> stops, int act_dist) {
        int calc_dist = abs(Integer.parseInt(stops.get(2)) - Integer.parseInt(stops.get((stops.size() - 1))));
        Log.d("get_round_route", "get_round_route: " + stops.get(1) + " and " + stops.get((stops.size() - 2)));
        int i = 0;
        if (calc_dist == 0) {
            Log.d("get_round_route", "get_round_route: ");
        }
        if (calc_dist != act_dist) {
            for (i = 0; i < stops.size(); i++) {
                if (((i + 1) % 3) == 0) {
                    int j;
                    j = (Integer.parseInt(stops.get(i)));
                    j = j * act_dist / calc_dist;
                    stops.set(i, Integer.toString(j));
                }
            }
        }
        return stops;
    }

    public Station getStationDetail(String station_id){
        return dbreader.get_station_detail(station_id);
    }

    public Station getNearestStation() {
        Location currentLocation = ((Railify) context.getApplicationContext()).getLocation();
        Station station = null;

        Station nearestStation = null;
        float nearestDistance = 100000;
        if (currentLocation != null) {
            float distance;
            int i = 0;
            for (i = 0; i < stations.size(); i++) {
                station = dbreader.get_station_detail(stations.get(i).getCode());
                Location stationLocation = station.getAddress().getLocation();
                if ((!(stationLocation == null))&&((!(stationLocation.getLatitude()==0))||(!(stationLocation.getLongitude()==0)))) {
                    distance = stationLocation.distanceTo(currentLocation);
                    if (distance <= nearestDistance) {
                        nearestDistance = distance;
                        nearestStation = station;
                    }
                }
            }
        }
        return nearestStation;
    }


    //Determine whether a person is inside a train
    public Train.TrainLocation calculateTrainLocation(Train train) {
        Location currentLocation = ((Railify) context.getApplicationContext()).getLocation();
        Train.TrainLocation trainLocation;
        int i = 0;
        Station station;
        float distance=1000000;

        Location stationLocation;
        trainLocation = new Train.TrainLocation(0, train.getStationStops().get(0).getCode(), 1000000);
        if ((!(currentLocation == null))&&(train.getStationStops().size()>0)) {
            for (i = 0; i < train.getStationStops().size(); i++) {
                station = dbreader.get_station_detail(train.getStationStops().get(i).getCode());
                stationLocation = station.getAddress().getLocation();
                if ((!(stationLocation == null))&&((!(stationLocation.getLatitude()==0))||(!(stationLocation.getLongitude()==0)))) {
                    distance = stationLocation.distanceTo(currentLocation);
                    if (distance <= trainLocation.getNextStoppageStationDistance()) {
                        trainLocation.setNextStoppageStationIdx(train.getStationStops().get(i).getIdx());
                        trainLocation.setNextStoppageStation(station.getCode());
                        trainLocation.setNextStoppageStationDistance(distance);
                    }
                }
            }
            i = trainLocation.getNextStoppageStationIdx();
            distance=trainLocation.getNextStoppageStationDistance();
            if (trainLocation.getNextStoppageStationDistance() <= 100) {
                trainLocation.setGrossLocationValid(true);
                return trainLocation;
            }
            float distance_one = 0;
            float distance_two = 0;
            Station station_one = null;
            Station station_two = null;
            if ((i > 0)) {
                station_one = dbreader.get_station_detail(train.getStationStops().get(i - 1).getCode());
                distance_one = station_one.getAddress().getLocation().distanceTo(currentLocation);
            }
            if (i < (train.getStationStops().size() - 1)) {
                station_two = dbreader.get_station_detail(train.getStationStops().get(i + 1).getCode());
                distance_two = station_two.getAddress().getLocation().distanceTo(currentLocation);
            }
            if (station_one == null) {
                if (train.getStationStops().get(1).getDist() >= (distance + distance_two)) {
                    trainLocation.setGrossLocationValid(true);
                } else {
                    trainLocation.setGrossLocationValid(false);
                }
                trainLocation.setNextStoppageStationIdx(1);
                trainLocation.setNextStoppageStation(train.getStationStops().get(1).getCode());
                trainLocation.setNextStoppageStationDistance(distance_two);
            } else {
                if (!(station_two == null)) {
                    if (distance_one >= distance_two) {
                        if (abs(train.getStationStops().get(i+1).getDist()-train.getStationStops().get(i).getDist())>= (distance + distance_two)) {
                            trainLocation.setGrossLocationValid(true);
                        } else {
                            trainLocation.setGrossLocationValid(false);
                        }
                        trainLocation.setNextStoppageStationIdx(trainLocation.getNextNonStoppageStationIdx() + 1);
                        trainLocation.setNextStoppageStation(train.getStationStops().get(i + 1).getCode());
                        trainLocation.setNextStoppageStationDistance(distance_two);
                    }else{
                        if (abs(train.getStationStops().get(i).getDist()-train.getStationStops().get(i-1).getDist())>= (distance + distance_one)) {
                            trainLocation.setGrossLocationValid(true);
                        } else {
                            trainLocation.setGrossLocationValid(false);
                        }
                    }
                }else{
                    if (abs(train.getStationStops().get(i).getDist()-train.getStationStops().get(i-1).getDist())>= (distance + distance_one)) {
                        trainLocation.setGrossLocationValid(true);
                    } else {
                        trainLocation.setGrossLocationValid(false);
                    }
                }
            }
            if(train.getNon_stoppage_stops().size()>0) {

                ArrayList<String> non_stoppage_stops = train.getNon_stoppage_stops_stationwise(trainLocation.getNextStoppageStationIdx() - 1);
                trainLocation.setNextNonStoppageStationIdx(0);
                trainLocation.setNextNonStoppageStation(non_stoppage_stops.get(1));
                trainLocation.setNextNonStoppageStationDistance(1000000);
                for (i = 0; i < non_stoppage_stops.size() / 3; i++) {
                    station = dbreader.get_station_detail(non_stoppage_stops.get((3 * i) + 1));
                    stationLocation = station.getAddress().getLocation();
                    if ((!(stationLocation == null))&&((!(stationLocation.getLatitude()==0))||(!(stationLocation.getLongitude()==0)))) {
                        distance = stationLocation.distanceTo(currentLocation);
                        if (distance <= trainLocation.getNextStoppageStationDistance()) {
                            trainLocation.setNextNonStoppageStationIdx(i);
                            trainLocation.setNextNonStoppageStation(station.getCode());
                            trainLocation.setNextNonStoppageStationDistance(distance);
                        }
                    }
                }
                i = trainLocation.getNextNonStoppageStationIdx();
                distance = trainLocation.getNextNonStoppageStationDistance();
                if (trainLocation.getNextNonStoppageStationDistance() <= 100) {
                    trainLocation.setFineLocationValid(true);
                    return trainLocation;
                }
                distance_one = 0;
                distance_two = 0;
                station_one = null;
                station_two = null;
                if (i > 0) {
                    station_one = dbreader.get_station_detail(non_stoppage_stops.get((3 * i) - 2));
                    distance_one = station_one.getAddress().getLocation().distanceTo(currentLocation);
                }
                if (i < ((non_stoppage_stops.size() / 3) - 1)) {
                    station_two = dbreader.get_station_detail(non_stoppage_stops.get((3 * (i + 1)) + 1));
                    distance_two = station_two.getAddress().getLocation().distanceTo(currentLocation);
                }
                if (station_one == null) {
                    if (abs(Float.parseFloat(non_stoppage_stops.get(5)) - Float.parseFloat(non_stoppage_stops.get(2))) >= (distance + distance_two)) {
                        trainLocation.setFineLocationValid(true);
                    } else {
                        trainLocation.setFineLocationValid(false);
                    }
                    trainLocation.setNextNonStoppageStationIdx(1);
                    trainLocation.setNextNonStoppageStation(non_stoppage_stops.get((3 * (i + 1)) + 1));
                    trainLocation.setNextNonStoppageStationDistance(distance_two);
                } else {
                    if (!(station_two == null)) {
                        if (distance_one >= distance_two) {
                            if (abs(Float.parseFloat(non_stoppage_stops.get((3 * (i + 1)) + 2)) - Float.parseFloat(non_stoppage_stops.get((3 * i) + 2))) >= (distance + distance_two)) {
                                trainLocation.setFineLocationValid(true);
                            } else {
                                trainLocation.setFineLocationValid(false);
                            }
                            trainLocation.setNextNonStoppageStationIdx(trainLocation.getNextNonStoppageStationIdx() + 1);
                            trainLocation.setNextNonStoppageStation(non_stoppage_stops.get((3 * (i + 1)) + 1));
                            trainLocation.setNextNonStoppageStationDistance(distance_two);
                        } else {
                            if (abs(Float.parseFloat(non_stoppage_stops.get((3 * i) + 2)) - Float.parseFloat(non_stoppage_stops.get((3 * (i - 1)) + 2))) >= (distance + distance_one)) {
                                trainLocation.setFineLocationValid(true);
                            } else {
                                trainLocation.setFineLocationValid(false);
                            }
                        }
                    } else {
                        if (abs(Float.parseFloat(non_stoppage_stops.get((3 * i) + 2)) - Float.parseFloat(non_stoppage_stops.get((3 * (i - 1)) + 2))) >= (distance + distance_one)) {
                            trainLocation.setFineLocationValid(true);
                        } else {
                            trainLocation.setFineLocationValid(false);
                        }
                    }
                }
            }
        }
        return trainLocation;

    }
}