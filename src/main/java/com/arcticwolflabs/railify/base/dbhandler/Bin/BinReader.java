package com.arcticwolflabs.railify.base.dbhandler.Bin;

import android.content.Context;
import android.util.Log;

import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Coach;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.UpdateAPI;
import com.arcticwolflabs.railify.base.statics.Address;
import com.arcticwolflabs.railify.base.statics.Station;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

public class BinReader implements DBReader {
    Context context;
    byte[] station_data;
    byte[] train_data;
    byte[] graph_data;
    byte[] s2t_data;

    byte[][] station_bytearray;
    Array[] train_bytearry;

    int station_length;
    int train_length;

    ArrayList<Integer> station_index;
    ArrayList<Integer> train_index;
    ArrayList<Integer> s2t_index;
    ArrayList<Station.StationMinimal> stations_minimal;
    ArrayList<Train.TrainMinimal> trains_minimal;
    HashMap<String, HashMap<String, ArrayList<Integer>>> graph;
    long[] savedPnrs = new long[]{0, 0, 0, 0, 0};
    ArrayList<Long> savedPnrsArraylst = new ArrayList<Long>();

    public BinReader(Context _context) {
        context = _context;
        openDatabases();
    }

    @Override
    public Station get_station_detail(String _station_code) {
        int index = 0;
        for (Station.StationMinimal station : stations_minimal) {
            if (station.getCode().equals(_station_code)) {
                index = stations_minimal.indexOf(station);
                break;
            }
        }
        int position = station_index.get(index);

        ByteBuffer byteBuffer = ByteBuffer.wrap(station_data).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.position(position + 4);
        int strlen = (int) byteBuffer.get();
        byteBuffer.position(position + strlen + 5);
        double lat = byteBuffer.getDouble();
        byteBuffer.position(position + strlen + 13);
        double lng = byteBuffer.getDouble();
        Address addr = new Address();
        addr.setCoordinate(lat, lng);
        Station station = new Station();
        station.setCode(stations_minimal.get(index).getCode());
        station.setName(stations_minimal.get(index).getName());
        station.setAddress(addr);
        return station;

    }

    @Override
    public Train get_train_detail(int _train_code) {
        int index = 0;
        for (Train.TrainMinimal train : trains_minimal) {
            if (train.getCode().equals(Integer.toString(_train_code))) {
                index = trains_minimal.indexOf(train);
                break;
            }
        }

        int position = train_index.get(index);

        ByteBuffer byteBuffer = ByteBuffer.wrap(train_data).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.position(position + 3);
        int runday = (int) byteBuffer.get();
        byteBuffer.position(position + 4);
        int strlen = (int) byteBuffer.get();
        byteBuffer.position(position + 5 + strlen);
        int rake_length = (int) byteBuffer.get();
        ArrayList<Coach> coaches = new ArrayList<Coach>();
        for (int j = 0; j < rake_length; j++) {
            coaches.add(new Coach((int) byteBuffer.get(position + 6 + strlen + (j * 2)), Integer.toString((int) byteBuffer.get(position + 7 + strlen + (j * 2))), 0, ""));
        }
        Train ctrain = new Train(_train_code, "");
        ctrain.setName(trains_minimal.get(index).getName());
        ctrain.setCoaches(coaches);
        ctrain.setRunday(runday);

        int station_no;
        station_no = (int) byteBuffer.get(position + 6 + strlen + (rake_length * 2));
        ArrayList<Stop> stops = new ArrayList<Stop>();
        position = position + 7 + strlen + (rake_length * 2);
        for (int j = 0; j < station_no; j++) {

            byteBuffer.position(position);
            int station_indexno = (int) byteBuffer.getShort();
            String code, name, halt, sch_arr, sch_dep;
            int idx, day, dist;
            String platform;
            code = stations_minimal.get(station_indexno).getCode();
            name = stations_minimal.get(station_indexno).getName();
            idx = j;
            position = position + 2;
            byteBuffer.position(position);
            String[] arr_dep = extract_arr_dep(((int) byteBuffer.get(position)), ((int) byteBuffer.get(position + 1)), ((int) byteBuffer.get(position + 2)), ((int) byteBuffer.get(position + 3)));
            sch_arr = arr_dep[0];
            sch_dep = arr_dep[1];
            halt = arr_dep[2];
            day = Integer.parseInt(arr_dep[3]);
            dist = (int) byteBuffer.getShort(position + 4);
            strlen = (int) byteBuffer.get(position + 6);
            byte[] platformarray = new byte[strlen];
            byteBuffer.position(position + 7);
            byteBuffer.get(platformarray, 0, strlen);
            position = position + 7 + strlen;
            platform = new String(platformarray);
            Stop stop = new Stop(idx, code, name, sch_arr, sch_dep, day, halt, dist, platform);
            stops.add(stop);
        }
        ctrain.setStationStops(stops);
        return ctrain;
    }

    @Override
    public Journey.JourneyMinimal get_train_journey_minimal(int _train_code, String from_id, String to_id) {
        Train ctrain = get_train_detail(_train_code);
        int idx_from = 0, idx_to = 0, from_day = 1, to_day = 1;
        String from_stn_name = new String();
        String to_stn_name = new String();
        String arr = "", dep = "";
        for (Stop stop : ctrain.getStationStops()) {
            if (stop.getCode().equals(from_id)) {
                idx_from = stop.getIdx();
                from_stn_name = stop.getName();
                dep = stop.getSch_dep();
                from_day = stop.getDay();
            }
            if (stop.getCode().equals(to_id)) {
                idx_to = stop.getIdx();
                to_stn_name = stop.getName();
                arr = stop.getSch_arr();
                to_day = stop.getDay();
            }
        }

        if (idx_to <= idx_from) {
            return null;
        }

        return new Journey.JourneyMinimal(_train_code, ctrain.getName(), from_id, from_stn_name, to_id, to_stn_name, dep, arr, from_day, to_day, idx_from, idx_to);
    }

    @Override
    public ArrayList<Integer> get_trains_at_station(String _station_code) {
        ArrayList<Integer> trnlst = new ArrayList<Integer>();
        int index = 0;
        for (Station.StationMinimal station : stations_minimal) {
            if (station.getCode().equals(_station_code)) {
                index = stations_minimal.indexOf(station);
                break;
            }
        }
        int position = s2t_index.get(index);

        ByteBuffer byteBuffer = ByteBuffer.wrap(s2t_data).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.position(position);

        int i = 0;
        int trnlen = byteBuffer.getShort();
        int trnname;
        for (i = 0; i < trnlen; i++) {
            byteBuffer.position(position + 2 + (i * 2));
            trnname = byteBuffer.getShort();
            trnlst.add(Integer.parseInt(trains_minimal.get(trnname).getCode()));
        }

        return trnlst;
    }

    @Override
    public ArrayList<Station.StationMinimal> get_all_stations() {
        stations_minimal = new ArrayList<Station.StationMinimal>();
        String code = "";
        String name = "";
        try {
            InputStream is;
            try {
                is = context.openFileInput("station.bin");
            } catch (Exception e) {
                is = context.getAssets().open("station.bin");
            }
            int size = is.available();
            station_data = new byte[size];
            station_index = new ArrayList<Integer>();
            is.read(station_data);
            ByteBuffer byteBuffer = ByteBuffer.wrap(station_data).order(ByteOrder.LITTLE_ENDIAN);
            int i = 2;
            byte[] idarray = new byte[4];
            int strlen = 0;
            for (i = 2; i <= station_data.length; i++) {
                station_index.add(i);
                byteBuffer.position(i);
                byteBuffer.get(idarray, 0, 4);
                code = new String(idarray).replaceAll("[^a-zA-Z0-9_-]", "");
                strlen = (int) byteBuffer.get(i + 4);
                byte[] namearray = new byte[strlen];
                byteBuffer.position(i + 4 + 1);
                byteBuffer.get(namearray, 0, strlen);
                name = new String(namearray);
                stations_minimal.add(new Station.StationMinimal(code, name));
                i = i + 4 + 1 + strlen + 8 + 8 + 1 - 1;

            }

        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return stations_minimal;


    }

    @Override
    public ArrayList<Train.TrainMinimal> get_all_trains() {

        trains_minimal = new ArrayList<Train.TrainMinimal>();
        int code = 0;
        String name = "";
        try {
            InputStream is;
            try {
                is = context.openFileInput("train.bin");
            } catch (Exception e) {
                is = context.getAssets().open("train.bin");
            }
            int size = is.available();
            train_data = new byte[size];
            train_index = new ArrayList<Integer>();
            is.read(train_data);
            ByteBuffer byteBuffer = ByteBuffer.wrap(train_data).order(ByteOrder.LITTLE_ENDIAN);
            int i = 0;
            byte[] idarray = new byte[3];
            int strlen = 0;
            int rake_length = 0;
            int station_no = 0;
            for (i = 0; i <= train_data.length; i++) {
                train_index.add(i);
                byteBuffer.position(i);
                byteBuffer.get(idarray, 0, 3);
                int[] idint = new int[3];
                int j;
                for (j = 0; j < 3; j++) {
                    idint[j] = (int) idarray[j];
                    if (idint[j] < 0) {
                        idint[j] = idint[j] + 256;
                    }
                }
                code = idint[0] + idint[1] * 256 + idint[2] * 65536;
                strlen = (int) byteBuffer.get(i + 3 + 1);
                byte[] namearray = new byte[strlen];
                byteBuffer.position(i + 3 + 1 + 1);
                byteBuffer.get(namearray, 0, strlen);
                name = new String(namearray);
                trains_minimal.add(new Train.TrainMinimal(Integer.toString(code), name));
                rake_length = (int) byteBuffer.get(i + 3 + 1 + 1 + strlen);
                station_no = (int) byteBuffer.get(i + 3 + 1 + 1 + strlen + 1 + (rake_length * 2));
                i = i + 3 + 1 + 1 + strlen + 1 + (rake_length * 2);
                int k = 0;
                for (k = 0; k < station_no; k++) {
                    strlen = (int) byteBuffer.get(i + 9);
                    i = i + 9 + strlen;
                }
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return trains_minimal;
    }

    @Override
    public ArrayList<Stop.StopMinimal> get_all_station_train_no(int _train_no, int _station_id_start) {
        ArrayList<Stop.StopMinimal> stops = new ArrayList<Stop.StopMinimal>();
        Train train = get_train_detail(_train_no);
        for (Stop stop : train.getStationStops()) {
            if (stop.getIdx() >= _station_id_start) {
                stops.add(new Stop.StopMinimal(stop.getIdx(), stop.getCode(), stop.getName()));
            }
        }
        return stops;
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
    public int get_physiography(String _station_code) {
        int index = 0;
        for (Station.StationMinimal station : stations_minimal) {
            if (station.getCode().equals(_station_code)) {
                index = stations_minimal.indexOf(station);
                break;
            }
        }
        int position = station_index.get(index + 1);

        ByteBuffer byteBuffer = ByteBuffer.wrap(station_data).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.position(position - 1);
        int phy_index = (int) byteBuffer.get();
        return phy_index;
    }

    @Override
    public HashMap<String, HashMap<String, ArrayList<Integer>>> get_graph() {
        Log.d("Loading time", "total loading time is ms getgraph started");
        int maplength = 0;
        int i = 0;
        int j = 0;
        int k = 2;
        graph = new HashMap<>();
        int connected_length = 0;
        int connected_code_id = 0;
        int connected_dist_length = 0;
        int connected_dist_id = 0;
        String code = "";
        Log.d("Loading time", "total loading time is ms databaseloading started");
        ByteBuffer byteBuffer;
        try {
            InputStream is;
            try {
                is = context.openFileInput("directconnectgraph.bin");
            } catch (Exception e) {
                is = context.getAssets().open("directconnectgraph.bin");
            }
            int size = is.available();
            graph_data = new byte[size];
            is.read(graph_data);
            byteBuffer = ByteBuffer.wrap(graph_data).order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.position(0);
            maplength = byteBuffer.getShort();
            Log.d("Loading time", "total loading time is ms databasereading started");
            for (i = 0; i < stations_minimal.size(); i++) {
                HashMap<String, ArrayList<Integer>> cmap = new HashMap<String, ArrayList<Integer>>();
                byteBuffer.position(k);
                connected_length = byteBuffer.getShort();
                k = k + 2;
                if (!(connected_length == 0)) {
                    for (j = 0; j < connected_length; j++) {

                        byteBuffer.position(k);
                        connected_code_id = byteBuffer.getShort();
                        k = k + 2;
                        byteBuffer.position(k);
                        if (!(connected_code_id == (-1))) {
                            connected_dist_length = byteBuffer.get();
                            k = k + 1;
                            ArrayList<Integer> dist = new ArrayList<>();
                            int n = 0;
                            for (n = 0; n < connected_dist_length; n++) {
                                byteBuffer.position(k);
                                connected_dist_id = byteBuffer.getShort();
                                k = k + 2;
                                dist.add(Integer.valueOf(connected_dist_id));

                            }
                            code = stations_minimal.get(connected_code_id).getCode();
                            cmap.put(code, dist);
                        }
                    }
//                    k = k + (4 * connected_length);
                    graph.put(stations_minimal.get(i).getCode(), cmap);
                }
//                k = k + 2;
                if (i % 100 == 0) {

                    Log.d("Loading time", "total loading time is ms databaseloading i==" + i);
                }
            }

            Log.d("Loading time", "total loading time is ms databaseloading completed");
        } catch (Exception e) {
            Log.d("Loading time", "total loading time is ms databaseloadingexception ocurred at i=" + i + " error is " + e);
            e.getLocalizedMessage();
        }
        Log.d("Loading time", "total loading time is ms getgraph completed");
        byteBuffer=null;
        return graph;
    }

    @Override
    public void load_s2t() {
        try {

            InputStream is;
            try {
                is = context.openFileInput("stationtotrain.bin");
            } catch (Exception e) {
                is = context.getAssets().open("stationtotrain.bin");
            }
            int size = is.available();
            s2t_data = new byte[size];
            s2t_index = new ArrayList<Integer>();
            is.read(s2t_data);
            ByteBuffer byteBuffer = ByteBuffer.wrap(s2t_data).order(ByteOrder.LITTLE_ENDIAN);
            int i = 0;
            int trnlen = 0;
            for (i = 0; i <= s2t_data.length; i++) {
                byteBuffer.position(i);
                s2t_index.add(i);
                trnlen = byteBuffer.getShort();
                i = i + 2 + (trnlen * 2) - 1;

            }
            i = s2t_data.length;

        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    @Override
    public void unload_graph() {
        graph=null;
    }

    @Override
    public void add_to_saved_pnr(long pnr_num) {
        long[] temp_pnrs = new long[5];
        int i = 0;
        for (i = 0; i < 5; i++) {
            temp_pnrs[i] = savedPnrs[i];
        }
        temp_pnrs[0] = pnr_num;
        if (savedPnrsArraylst.contains(pnr_num)) {
            i = 0;
            int position = 0;
            for (i = 0; i < savedPnrs.length; i++) {
                if (savedPnrs[i] == pnr_num) {
                    position = i;
                }
            }
            for (i = 0; i < position; i++) {
                temp_pnrs[i + 1] = savedPnrs[i];
            }
        } else {
            i = 0;
            for (i = 0; i < 4; i++) {
                temp_pnrs[i + 1] = savedPnrs[i];
            }
        }
        savedPnrs = temp_pnrs;
        savedPnrsArraylst.clear();
        for (i = 0; i < 5; i++) {
            if (temp_pnrs[i] != 0) {
                savedPnrsArraylst.add(temp_pnrs[i]);
            }
        }
        try {
            File dir = context.getDir("internal_data_folder", Context.MODE_PRIVATE);
            File file = new File(dir, "savedPnrs.bin");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN);
            for (i = 0; i < savedPnrsArraylst.size(); i++) {
                byteBuffer.putLong(savedPnrsArraylst.get(i));
            }
            byteBuffer.rewind();
            fileOutputStream.write(byteBuffer.array());
        } catch (Exception e) {

        }
    }

    @Override
    public ArrayList<Long> get_saved_pnr() {
        if (savedPnrsArraylst.size() != 0) {
            return savedPnrsArraylst;
        } else {
            return null;
        }
    }

    //Read write hashmap database graph to internal storage directly
    //It is not used as it is slow
/*    private void writeGraphToInternalStorage(){
        try
        {
            FileOutputStream fos = context.openFileOutput("Graph.dbb", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGraphFromInternalStorage(){
        try
        {
            FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+"/Graph.dbb");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            graph = (HashMap<String, HashMap<String, ArrayList<Integer>>>) objectInputStream.readObject();
        }
        catch(ClassNotFoundException | IOException | ClassCastException e) {
            e.printStackTrace();
            graph=null;
        }
    }*/


    @Override
    public void openDatabases() {

        /*try {
            InputStream is = context.getAssets().open("stationcomplete.bin");
            int size = is.available();
            station_data = new byte[size];
            is.read(station_data);
            ByteBuffer byteBuffer=ByteBuffer.wrap(station_data).order(ByteOrder.LITTLE_ENDIAN);
            station_length=byteBuffer.getShort();
            station_bytearray=new byte[station_length][];
            int i=0;
            int j=2;
            for (i=0;i<station_length;i++){
                byteBuffer.position(j+5);
                int strlen=(int)byteBuffer.getChar();
                byte[] idarray=new byte[strlen+22];
                byteBuffer.get(idarray,0,strlen+22);
                station_bytearray[i]=idarray;
                j=j+strlen+22;
            }


        }catch (Exception ignored){

        }

        try {
            InputStream is = context.getAssets().open("train.bin");
            int size = is.available();
            train_data = new byte[size];
            is.read(train_data);
        }catch (Exception ignored){

        }

        try {
            InputStream is = context.getAssets().open("directconnectgraph.bin");
            int size = is.available();
            graph_data = new byte[size];
            is.read(graph_data);
        }catch (Exception ignored){

        }
        try {

            InputStream is;
            try{
                is = context.openFileInput("stationtotrain.bin");
            }catch (Exception e){
                is = context.getAssets().open("stationtotrain.bin");
            }
            int size = is.available();
            s2t_data = new byte[size];
            s2t_index = new ArrayList<Integer>();
            is.read(s2t_data);
            ByteBuffer byteBuffer = ByteBuffer.wrap(s2t_data).order(ByteOrder.LITTLE_ENDIAN);
            int i = 0;
            int trnlen = 0;
            for (i = 0; i <= s2t_data.length; i++) {
                byteBuffer.position(i);
                s2t_index.add(i);
                trnlen = byteBuffer.getShort();
                i = i + 2 + (trnlen * 2) - 1;

            }
            i = s2t_data.length;

        } catch (Exception e) {
            e.getLocalizedMessage();
        }*/
        load_s2t();
        try {
            File dir = context.getDir("internal_data_folder", Context.MODE_PRIVATE);
            File file = new File(dir, "savedPnrs.bin");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                int size = Math.min(fis.available(), 40);
                byte[] pnr_data = new byte[size];
                fis.read(pnr_data);
                ByteBuffer byteBuffer = ByteBuffer.wrap(pnr_data).order(ByteOrder.LITTLE_ENDIAN);
                int i = 0;
                for (i = 0; i < size / 8; i++) {
                    savedPnrs[i] = byteBuffer.getLong();
                    byteBuffer.position(8 * (i + 1));

                }
                for (i = 0; i < 5; i++) {
                    if ((savedPnrs[i] != 0) && (!(savedPnrsArraylst.contains(savedPnrs[i])))) {
                        savedPnrsArraylst.add(savedPnrs[i]);
                    }
                }
                for (i = 0; i < 5; i++) {
                    if (i < savedPnrsArraylst.size()) {
                        savedPnrs[i] = savedPnrsArraylst.get(i);
                    } else {
                        savedPnrs[i] = 0;
                    }
                }
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void closeDatabases() {
        if (station_data.length > 0) {

        }

        if (train_data.length > 0) {

        }

        if (graph_data.length > 0) {

        }

    }

    @Override
    public String get_station_name_from_code(String code) {
        int i = 0;
        for (i = 0; i < stations_minimal.size(); i++) {
            if (stations_minimal.get(i).getCode().equalsIgnoreCase(code)) {
                return stations_minimal.get(i).getName();
            }
        }
        return null;
    }

    private String[] extract_arr_dep(int arrhr, int arrmin, int dephr, int depmin) {
        String halt, sch_arr, sch_dep;
        int day;
        halt = "None";
        String a, b, c, d;

        if ((arrhr + 256) != 255) {
            day = (arrhr / 24) + 1;
            if ((arrhr % 24) < 10) {
                a = "0" + Integer.toString(arrhr % 24);
            } else {
                a = Integer.toString(arrhr % 24);
            }
            if (arrmin < 10) {
                b = "0" + Integer.toString(arrmin);
            } else {
                b = Integer.toString(arrmin);
            }
            sch_arr = a + ":" + b;

        } else {
            day = 1;
            sch_arr = "None";
        }
        if ((dephr + 256) != 255) {
            if ((dephr % 24) < 10) {
                c = "0" + Integer.toString(dephr % 24);
            } else {
                c = Integer.toString(dephr % 24);
            }
            if (depmin < 10) {
                d = "0" + Integer.toString(depmin);
            } else {
                d = Integer.toString(depmin);
            }

            sch_dep = c + ":" + d;

        } else {
            sch_dep = "None";
        }
        if (((arrhr + 256) != 255) && ((dephr + 256) != 255)) {
            halt = Integer.toString(((dephr * 60) + depmin) - ((arrhr * 60) + arrmin));

        }
        String[] res = {sch_arr, sch_dep, halt, Integer.toString(day)};
        return res;
    }

}
