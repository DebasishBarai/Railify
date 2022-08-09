package com.arcticwolflabs.railify.base.netapi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.arcticwolflabs.railify.base.dbhandler.Bin.BinReader;
import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dbhandler.SQLite.SQLReader;

import java.util.LinkedList;
import java.util.Queue;

public class UpdateAPI {
    private HTTPRequester hr;
    private Context context;
    private boolean[] flags;
    private Queue<UpdateInfo> udptrinfos;

    public Queue<UpdateInfo> getUpdates() {
        return udptrinfos;
    }


    public static class UpdateInfo {
        double timestamp;
        String info;
        String url;
        String target;

        public UpdateInfo() {
            this.timestamp = 0;
            this.info = "";
            this.target = "";
            this.url = "";
        }

        public double getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(double timestamp) {
            this.timestamp = timestamp;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public UpdateAPI(Context context) {
        this.hr = new HTTPRequester();
        this.context = context;
        this.flags = new boolean[]{false, false, false};
        this.udptrinfos = new LinkedList<>();
    }

    public boolean check() {
        DBReader dbr = new BinReader(this.context);
        this.udptrinfos.clear();
        UpdateInfo updtinfo = null;
        String str_stations =null;
        //str_stations = hr.requestGETString("https://www.mohammadulhaque.in/updater.php?T=0");
        if (str_stations != null) {
            updtinfo = parse_update_info(str_stations);
            if (updtinfo != null) {
                UpdateInfo localinfo = dbr.get_stations_update_info();
                if (localinfo == null || updtinfo.getTimestamp() > localinfo.getTimestamp()) {
                    this.flags[0] = true;
                    this.udptrinfos.add(updtinfo);
                } else {
                    Log.d("UPDTR", "updtinfo error for stations.");
                }
            }
        } else {
            Log.d("UPDTR", "Net error for stations.");
        }

        String str_trains =null;
//        str_trains = hr.requestGETString("https://www.mohammadulhaque.in/updater.php?T=1");
        if (str_trains != null) {
            updtinfo = parse_update_info(str_trains);
            if (updtinfo != null) {
                UpdateInfo localinfo = dbr.get_trains_update_info();
                if (localinfo == null || updtinfo.getTimestamp() > localinfo.getTimestamp()) {
                    this.flags[1] = true;
                    this.udptrinfos.add(updtinfo);
                }
            } else {
                Log.d("UPDTR", "updtinfo error for trains.");
            }
        } else {
            Log.d("UPDTR", "Net error for trains.");
        }

        String str_stations2trains =null;
//        str_stations2trains = hr.requestGETString("https://www.mohammadulhaque.in/updater.php?T=2");
        if (str_stations2trains != null) {
            updtinfo = parse_update_info(str_stations2trains);
            if (updtinfo != null) {
                UpdateInfo localinfo = dbr.get_stations2trains_update_info();
                if (localinfo == null || updtinfo.getTimestamp() > localinfo.getTimestamp()) {
                    this.flags[2] = true;
                    this.udptrinfos.add(updtinfo);
                }
            } else {
                Log.d("UPDTR", "updtinfo error for stations2trains.");
            }
        } else {
            Log.d("UPDTR", "Net error for stations2trains.");
        }
        return flags[0] || flags[1] || flags[2];
    }

    public void execute_updates() {
        for (UpdateInfo cinfo : this.udptrinfos) {
            Toast.makeText(this.context, cinfo.getInfo(), Toast.LENGTH_SHORT).show();
        }

    }

    private UpdateInfo parse_update_info(String str) {
        String secretKey = "igGS4@@G$MF0@!@#";
        if (str != null && !str.equals("")) {
            try {
                Crypto decrypted = Crypto.decrypt(secretKey, str);
                str = decrypted.getData();
                if (str == null) {
                    Log.d("CRYPT", "Null str getErrorMessage");
                } else {
                    Log.d("CRYPT", "decrypted.");
                }
            } catch (Exception e) {
                Log.d("CRYPT", "Exceptioned: " + e.getMessage());
                return null;
            }
            UpdateInfo uinfo = new UpdateInfo();
            String[] splts = str.split(",@,");
            if (splts.length == 4) {
                uinfo.setTimestamp(Double.parseDouble(splts[0]));
                uinfo.setInfo(splts[1]);
                uinfo.setTarget(splts[2]);
                uinfo.setUrl(splts[3]);
                return uinfo;
            }
        }
        return null;
    }


}
