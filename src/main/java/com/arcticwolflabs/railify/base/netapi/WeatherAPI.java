package com.arcticwolflabs.railify.base.netapi;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.arcticwolflabs.railify.base.dbhandler.DBReader;
import com.arcticwolflabs.railify.base.dynamics.Place;
import com.arcticwolflabs.railify.base.dynamics.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class WeatherAPI {
    //private final String baseurl = "https://www.mohammadulhaque.in/arcticwolflabs/test_placescacher.php?id=";

    private final String baseurl="https://railify-debb.herokuapp.com/maps/weather/";
    private HTTPRequester hr;
    private DBReader dbreader;

    public WeatherAPI(DBReader dbreader) {
        this.hr = new HTTPRequester();
        this.dbreader = dbreader;
    }

    private Weather parse_result(String str, String id) {
        String secretKey = null;
        try {
            secretKey = new String(Base64.decode("aWdHUzRAQEckTUYwQCFAIw==", Base64.DEFAULT),
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            secretKey = "";
        }
        if (str != null && !str.equals("")) {
            /*try {
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
            }*/
            try {
                JSONObject obj = new JSONObject(str);
                /*JSONArray weathobj = obj.getJSONArray("weather");
                String weath = weathobj.getJSONObject(0).getString( "main");

                JSONObject mainobj = obj.getJSONObject("main");
                double temp = mainobj.getDouble("temp")-273.16;
                double humidity = mainobj.getDouble("humidity");
                double press = mainobj.getDouble("pressure");*/

                String weath = obj.getString( "description");
                double temp = Double.parseDouble(obj.getString("temperature"));
                double wind = Double.parseDouble(obj.getString("windSpeed"));
                double humidity = Double.parseDouble(obj.getString("humidity"));
                double press = Double.parseDouble(obj.getString("barometerPressure"));
                String icon_url= obj.getString("iconLink");
                Bitmap icon_img=this.hr.requestGETBitmap(icon_url);

                int region = dbreader.get_physiography(id);
                return new Weather(id, region, weath, temp, wind, humidity, press,icon_img);
            } catch (JSONException ignored) {
                return null;
            }
        }
        return null;
    }


    public Weather query(String station_id) {
        String url = this.baseurl+station_id;
        Log.d("WAPI","Str: "+url);
        String str = this.hr.requestGETString(url);
        return parse_result(str, station_id);
    }

    public String queryRaw(String station_id) {
        return hr.requestGETString(hr.requestGETString(baseurl+station_id));
    }

    public String queryRawProcessed(String station_id) {
        return null;
    }


}

