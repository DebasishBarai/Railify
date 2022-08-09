package com.arcticwolflabs.railify.ui.tabs;

import android.content.ContentProviderClient;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Place;
import com.arcticwolflabs.railify.base.dynamics.Weather;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.dd.processbutton.iml.ActionProcessButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PLC extends Fragment implements View.OnClickListener {
    private String name;
    private String id;
    private TableLayout tbl_lyt;
    private ActionProcessButton tv_name;
    private TextView tv_phy;
    private TextView tv_weath;
    private TextView tv_temp;
    private TextView tv_wind;
    private TextView tv_humid;
    private TextView tv_press;
    private ImageView iv_weather_icon;
    private Boolean loaded_places_to_visit;
    private ActionProcessButton tv_plvw_place;
    private OnFIListener mListener;
    private Context context;
    GetWeatherAsync weatherAsync;
    GetPlaceAsync placeAsync;
    ArrayList<Place> placeResult;

    public PLC() {
    }

    public static PLC newInstance(Context _context) {
        PLC fragment = new PLC();
        fragment.context = _context;
        fragment.loaded_places_to_visit = false;
        Log.d("TR2PLC", "Okay from PlaceFragment");
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TR2PLC", "Okay from PlaceFragment onCreateView");
        return inflater.inflate(R.layout.fragment_place, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        tbl_lyt = getView().findViewById(R.id.tbl_lyt_nby_plc);
        tv_name = getView().findViewById(R.id.plvw_title);
        tv_name.setMode(ActionProcessButton.Mode.ENDLESS);
        tv_name.setText(name);
        tv_name.setOnClickListener(this);
        iv_weather_icon = getView().findViewById(R.id.imgWeather);
        tv_phy = getView().findViewById(R.id.region);
        tv_weath = getView().findViewById(R.id.weather_condition);
        tv_temp = getView().findViewById(R.id.temp);
        tv_wind = getView().findViewById(R.id.windSpeed);
        tv_humid = getView().findViewById(R.id.humidity);
        tv_press = getView().findViewById(R.id.pressure);
        tv_plvw_place = getView().findViewById(R.id.plvw_place);
        tv_plvw_place.setOnClickListener(this);
        tv_plvw_place.setMode(ActionProcessButton.Mode.ENDLESS);
        tv_plvw_place.setProgress(0);

        if (Tools.isNetworkAvailable(getContext())) {
            weatherAsync = new PLC.GetWeatherAsync(this, id, name, tv_phy, tv_weath, tv_temp, tv_wind, tv_humid, tv_press, iv_weather_icon, tv_name);
            weatherAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            placeAsync = new PLC.GetPlaceAsync(this, id, name, tbl_lyt, tv_name);
            placeAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {

            Toasty.info(getActivity(), "No internet connection. Connect to internet and try again", Toast.LENGTH_LONG).show();
        }

        loaded_places_to_visit = false;
        Tools.hideKeyboardFrom(this.context, getView());
        Log.d("TR2PLC", "Okay from PlaceFragment onViewCreated");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void get_arguments_from_tr(String station_id, String station_name) {
        name = station_name;
        id = station_id;
        Log.d("TR2PLC", "Okay from PlaceFragment get_arguments_from_tr");

    }


/*    private static void set_image(int phy_val, ImageView view) {
        switch (phy_val) {
            case Weather.REGION_TYPE_EASTERN_HIMALAYAS:
                view.setImageResource(R.drawable.physio_eastern_himalayas);
                break;
            case Weather.REGION_TYPE_WESTERN_HIMALAYAS:
                view.setImageResource(R.drawable.physio_western_himalayas);
                break;
            case Weather.REGION_TYPE_NORTHEASTERN_RANGE:
                view.setImageResource(R.drawable.physio_north_eastern_range);
                break;
            case Weather.REGION_TYPE_EASTERN_PLAINS:
                view.setImageResource(R.drawable.physio_eastern_plains);
                break;
            case Weather.REGION_TYPE_NORTHERN_PLAINS:
                view.setImageResource(R.drawable.physio_northern_plains);
                break;
            case Weather.REGION_TYPE_CENTRAL_HIGHLANDS:
                view.setImageResource(R.drawable.physio_central_highlands);
                break;
            case Weather.REGION_TYPE_EAST_DECCAN:
                view.setImageResource(R.drawable.physio_east_deccan);
                break;
            case Weather.REGION_TYPE_SOUTH_DECCAN:
                view.setImageResource(R.drawable.physio_south_deccan);
                break;
            case Weather.REGION_TYPE_EASTERN_GHATS:
                view.setImageResource(R.drawable.physio_eastern_ghats);
                break;
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (PLC.OnFIListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Log.d("PLVW", "view clicked: " + v.toString());
        switch (v.getId()) {
            case R.id.plvw_title:
                if (Tools.isNetworkAvailable(getContext())) {
                    weatherAsync = new PLC.GetWeatherAsync(this, id, name, tv_phy, tv_weath, tv_temp, tv_wind, tv_humid, tv_press, iv_weather_icon, tv_name);
                    weatherAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    placeAsync = new PLC.GetPlaceAsync(this, id, name, tbl_lyt, tv_name);
                    placeAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {

                    Toast.makeText(getActivity(), "No internet connection. Connect to internet and try again", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.plvw_place:
                if (loaded_places_to_visit) {
                    changeTableRowDisplay(v);
                }
                break;
        }
    }

    private static class GetWeatherAsync extends AsyncTask<String, Void, Weather> {
        private WeakReference<PLC> _plc_frag;
        private WeakReference<TextView> _reg_res;
        private WeakReference<TextView> _wea_res;
        private WeakReference<TextView> _temp_res;
        private WeakReference<TextView> _wind_res;
        private WeakReference<TextView> _humid_res;
        private WeakReference<TextView> _press_res;
        private WeakReference<ImageView> _iv_weather_icon;
        private WeakReference<ActionProcessButton> _tv_name;
        private String _name;
        private String _id;

        private GetWeatherAsync(PLC plc_frag, String id, String name, TextView tv0, TextView tv1,
                                TextView tv2, TextView tv3, TextView tv4, TextView tv5, ImageView iv, ActionProcessButton tv_name) {
            _plc_frag = new WeakReference<>(plc_frag);
            _reg_res = new WeakReference<>(tv0);
            _wea_res = new WeakReference<>(tv1);
            _temp_res = new WeakReference<>(tv2);
            _wind_res = new WeakReference<>(tv3);
            _humid_res = new WeakReference<>(tv4);
            _press_res = new WeakReference<>(tv5);
            _iv_weather_icon = new WeakReference<>(iv);
            _id = id;
            _tv_name = new WeakReference<>(tv_name);
            _name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _tv_name.get().setLoadingText(_name);
            _tv_name.get().setProgress(1);
        }

        @Override
        protected Weather doInBackground(String[] _params) {
            try {
                if (_plc_frag != null) {
                    return ((MainActivity) _plc_frag.get().context).GetWeather(_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Weather result) {


            if (result != null) {
                if (_reg_res.get() != null) {
                    _reg_res.get().setText(Weather.regions_names[result.getRegion()]);
                }
                if (_wea_res.get() != null) {
                    _wea_res.get().setText(result.getWeather());
                }
                if (_temp_res.get() != null) {
                    _temp_res.get().setText(String.format("%.1f", result.getTemp()));
                }
                if (_wind_res.get() != null) {
                    _wind_res.get().setText(String.format("%.2f", (result.getWind() * 3.6)) + " km/hr");
                }
                if (_humid_res.get() != null) {
                    _humid_res.get().setText(String.format("%.1f", result.getHumidity()) + "%");
                }
                if (_press_res.get() != null) {
                    _press_res.get().setText(String.format("%.1f", result.getPressure()) + " mPa");
                }
                if (_iv_weather_icon.get() != null) {
                    _iv_weather_icon.get().setImageBitmap(Bitmap.createScaledBitmap(result.getIcon_img(), 120, 120, false));
                }
            } else {
                Toasty.info(_plc_frag.get().getActivity(), "Temporarily unable to fetch current weather data. Please try again", Toast.LENGTH_LONG).show();

            }
            if (!(_tv_name.get() == null)) {
                _tv_name.get().setProgress(0);
                _tv_name.get().setText(_name);
            }
        }
    }

    private static class GetPlaceAsync extends AsyncTask<String, Void, ArrayList<Place>> {
        private WeakReference<PLC> _plc_frag;
        private WeakReference<TableLayout> _tbl_lyt;
        private WeakReference<ActionProcessButton> _tv_name;
        private String _id;
        private String _name;

        private GetPlaceAsync(PLC plc_frag, String id, String name, TableLayout tbl_lyt, ActionProcessButton tv_name) {
            _plc_frag = new WeakReference<>(plc_frag);
            _tbl_lyt = new WeakReference<>(tbl_lyt);
            _id = id;
            _tv_name = new WeakReference<>(tv_name);
            _name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _tv_name.get().setProgress(1);
            _plc_frag.get().tv_plvw_place.setProgress(1);
        }

        @Override
        protected ArrayList<Place> doInBackground(String[] _params) {
            try {
                if (_plc_frag != null) {
                    return ((MainActivity) _plc_frag.get().context).GetPlace(_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Place> result) {

            if (result != null) {
                _plc_frag.get().placeResult=result;
                try {
                    TableRow.LayoutParams param1 = new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );

                    param1.gravity = Gravity.CENTER;
                    TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            6.0f
                    );

                    param2.gravity = Gravity.CENTER;

                    for (int i = 0; i < result.size(); i++) {
                        TableRow ttlRw = new TableRow(_plc_frag.get().getContext());
                        ImageView nameView = new ImageView(_plc_frag.get().getContext());
                        nameView.setImageBitmap(Bitmap.createScaledBitmap(result.get(i).getIcon_img(), 120, 120, false));
                        //nameView.setTextColor(Color.BLUE);
                        //nameView.setTextSize(20);
                        nameView.setLayoutParams(param1);
                        ttlRw.addView(nameView);

                        TextView nameDetailView = new TextView(_plc_frag.get().getContext());
                        nameDetailView.setText(result.get(i).getTitle() + " (" + String.format("%.1f", result.get(i).getDistance() / 1000) + " km)");
                        nameDetailView.setTextSize(20);
                        nameDetailView.setTextColor(_plc_frag.get().getResources().getColor(R.color.primary_text));
                        nameDetailView.setTypeface(ResourcesCompat.getFont(_plc_frag.get().getContext(), R.font.raleway_semibold));
                        nameDetailView.setLayoutParams(param2);
                        nameDetailView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        nameDetailView.setSingleLine(false);
                        ttlRw.addView(nameDetailView);
                        ttlRw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i=_plc_frag.get().tbl_lyt.indexOfChild(v);
                                Place view_data=_plc_frag.get().placeResult.get(i-1);
                                _plc_frag.get().mListener.onFragmentInteraction(_id, view_data.getTitle(), view_data.getAddress().getLocation());
                            }
                        });
                        _tbl_lyt.get().addView(ttlRw);

                    }
                    _plc_frag.get().tv_plvw_place.setOnClickListener(_plc_frag.get());
                    _plc_frag.get().loaded_places_to_visit = true;
                } catch (Exception ignored) {

                }

            } else {
                Toasty.info(_plc_frag.get().getActivity(), "Temporarily unable to fetch place data. Please try again", Toast.LENGTH_LONG).show();
            }
            if (!(_plc_frag.get().tv_plvw_place == null)) {
                _plc_frag.get().tv_plvw_place.setProgress(0);
            }
            if (!(_tv_name.get() == null)) {
                _tv_name.get().setProgress(0);
                _tv_name.get().setText(_name);
            }
        }
    }

    public void changeTableRowDisplay(View v) {
        if (tbl_lyt.getChildCount() > 1) {
            if (tbl_lyt.getChildAt(1).getVisibility() == View.VISIBLE) {
                for (int i = 1; i < tbl_lyt.getChildCount(); i++) {
                    tbl_lyt.getChildAt(i).setVisibility(View.GONE);
                }
            } else {
                for (int i = 1; i < tbl_lyt.getChildCount(); i++) {
                    tbl_lyt.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PLC", "onDestroy called and destroyed.");
    }

    public interface OnFIListener {
        void onFragmentInteraction(String station_id, String destination_title, Location location);
    }
}
