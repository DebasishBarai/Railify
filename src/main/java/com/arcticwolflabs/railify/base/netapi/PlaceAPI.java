package com.arcticwolflabs.railify.base.netapi;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.util.Base64;
import android.util.Log;

import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Place;
import com.arcticwolflabs.railify.base.statics.Address;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class PlaceAPI {
    //private final String baseurl = "https://www.mohammadulhaque.in/arcticwolflabs/test_tovisit.php?id=";
    private final String baseurl="https://railify-debb.herokuapp.com/maps/places/";
    private HTTPRequester hr;
    private DBReader dbreader;

    public PlaceAPI(DBReader dbreader) {
        this.hr = new HTTPRequester();
        this.dbreader = dbreader;
    }

    private ArrayList<Place> parse_result(String str, String id) {
        String secretKey = null;
        try {
            secretKey = new String(Base64.decode("aWdHUzRAQEckTUYwQCFAIw==", Base64.DEFAULT),
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            secretKey = "";
        }
        if (str != null && !str.equals("")) {
         /* try {
                Crypto decrypted = Crypto.decrypt(secretKey, str);
                str = decrypted.getData();
            } catch (Exception e) {
                Log.d("CRYPT", "Exceptioned: " + e.getMessage());
                return null;
            }*/
            ArrayList<Place> places = new ArrayList<Place>();
            try {
                JSONArray obj = new JSONArray(str);
                Log.d("jsonarray", "parse_result: 1");
                for (int i = 0; i < obj.length(); ++i) {
                    String title = obj.getJSONObject(i).getString("title");
                    Double distance = Double.valueOf(obj.getJSONObject(i).getString("distance"));
                    JSONArray position=obj.getJSONObject(i).getJSONArray("position");
                    Location location=new Location("dummyProvider");
                    location.setLatitude((Double) position.get(0));
                    location.setLongitude((Double) position.get(1));
                    String addr_str = obj.getJSONObject(i).getString("vicinity");
                    addr_str = addr_str.replace("/<br\\/>/g", "");
                    String icon_url= obj.getJSONObject(i).getString("icon");
                    Bitmap icon_img=this.hr.requestGETBitmap(icon_url);
                    places.add(new Place(id, title, distance, new Address(location, addr_str),icon_img));
                    // Log.d("jsonarray", "parse_result: i=" + Integer.toString(i));
                }
                Collections.sort(places);
                return places;
            } catch (JSONException ignored) {
                return null;
            }
        }
        return null;
    }


    public ArrayList<Place> query(String station_id) {
        String url = this.baseurl + station_id;
        Log.d("PAPI", "Str: " + url);
        String str = this.hr.requestGETString(url);
        return parse_result(str, station_id);
    }

    public String queryRaw(String station_id) {
        return hr.requestGETString(hr.requestGETString(baseurl + station_id));
    }

    public String queryRawProcessed(String station_id) {
        return null;
    }

}
