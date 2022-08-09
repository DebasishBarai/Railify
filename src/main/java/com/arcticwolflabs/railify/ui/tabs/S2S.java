package com.arcticwolflabs.railify.ui.tabs;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.statics.Station;
import com.arcticwolflabs.railify.core.CacheSystem;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.customadapters.ACAdapter;

import com.arcticwolflabs.railify.ui.customadapters.RVS2SAdapter;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.dd.processbutton.iml.ActionProcessButton;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;


public class S2S extends Fragment implements View.OnClickListener {

    ACAdapter<Station.StationMinimal> arrayAdapter;
    AutoCompleteTextView fromStation;
    AutoCompleteTextView toStation;
    Boolean dataLoadedFromTR;
    Boolean from_station_selected;
    Boolean to_station_selected;
    private ArrayList<Station.StationMinimal> stationsList;
    private ImageButton buttonswap;
    private ActionProcessButton buttonsearch;
    private TextView buttonAltsearch;
    private TextView buttonsort;
    private RecyclerView recyclerview;
    private RVS2SAdapter adapter;
    private ArrayList<Journey.JourneyMinimal[]> result;
    private Context context;
    private MainActivity main_activity;
    SharedPreferences sharedPreferences;
    private AsyncTask<Void, Void, ArrayList<Journey.JourneyMinimal[]>> asyc;
    private AsyncTask<Void, Void, String> bsyc;

    //CheckBox checkBoxDate;
    boolean dateChecked;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    TextView journeyDate;
    Date dateselected;
    SimpleDateFormat monthformat;
    SimpleDateFormat dayformat;
    String monthselected;
    String dayselected;
    int year;
    int month;
    int day;

    String nearestStationCode;

    private OnFIListener mListener;
    private CacheSystem.CacheS2S cs_s2s;

    public S2S() {
        cs_s2s = new CacheSystem.CacheS2S();
    }

    public static S2S newInstance(Context _context) {
        S2S fragment = new S2S();
        fragment.setContext(_context);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFIListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s2s, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main_activity = (MainActivity) getActivity();
        sharedPreferences = context.getSharedPreferences("preferencesFile", MODE_PRIVATE);
        stationsList = main_activity.getCoreSystem().getStations();
        arrayAdapter = new ACAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stationsList);
        fromStation = view.findViewById(R.id.fromStationHint);
        fromStation.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        fromStation.setAdapter(arrayAdapter);
        toStation = view.findViewById(R.id.toStationHint);
        toStation.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        toStation.setAdapter(arrayAdapter);
        dataLoadedFromTR = false;
        from_station_selected = false;
        to_station_selected = false;
        fromStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (fromStation.isPerformingCompletion()) {
                    from_station_selected = true;
                    toStation.requestFocus();
                }
                if ((from_station_selected) && (!dataLoadedFromTR) && (i1 > i2)) {
                    from_station_selected = false;
                    fromStation.setText("");
                } else {
                    // Perform your task here... Like calling web service, Reading data from SQLite database, etc...
                }
                dataLoadedFromTR = false;
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });
        toStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (toStation.isPerformingCompletion()) {
                    to_station_selected = true;
                    Tools.hideKeyboardFrom(getContext(), view);
                }
                if ((to_station_selected) && (!dataLoadedFromTR) && (i1 > i2)) {
                    to_station_selected = false;
                    toStation.setText("");
                } else {
                    // Perform your task here... Like calling web service, Reading data from SQLite database, etc...
                }
                dataLoadedFromTR = false;
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        fromStation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (fromStation.getRight() - fromStation.getCompoundDrawables()[2].getBounds().width())) {
                        // your action here
                        nearestStationCode = sharedPreferences.getString("nearestStationCode", "");
                        if (!(nearestStationCode.equalsIgnoreCase(""))) {
                            dataLoadedFromTR =true;
                            fromStation.setText(nearestStationCode);
                            from_station_selected = true;
                        } else {
                            Toasty.error(getActivity(), "Unable to detect your current location", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        /*fromStation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Set nearest station in the from station autocompletetextview if available
                if ((PreferenceManager.getDefaultSharedPreferences(getContext())).getBoolean("show_nearest_station", true)) {
                    nearestStationCode = sharedPreferences.getString("nearestStationCode", "");
                    if (!(nearestStationCode.equalsIgnoreCase(""))) {
                        fromStation.setText(nearestStationCode);
                        from_station_selected = true;
                    } else {
                        Toasty.error(getActivity(), "Unable to detect your current location", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;

            }
        });*/
        recyclerview = view.findViewById(R.id.recyclerViewS2S);
        recyclerview.setLayoutManager(new LinearLayoutManager(this.getContext()));
        buttonswap = view.findViewById(R.id.buttonswapS2S);
        buttonswap.setOnClickListener(this);
        buttonsearch = (ActionProcessButton) view.findViewById(R.id.buttonsearchS2S);
        buttonsearch.setTypeface(ResourcesCompat.getFont(main_activity, R.font.raleway_semibold));
        buttonsearch.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonsearch.setOnClickListener(this);

        buttonAltsearch = (TextView) view.findViewById(R.id.buttonsearchAltS2S);
        /*ArrayAdapter spinneraltadapter = ArrayAdapter.createFromResource(main_activity.getApplicationContext(),
                R.array.searchalternateroutearray, R.layout.spinner_item);

        spinneraltadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        buttonAltsearch.setAdapter(spinneraltadapter);*/
        buttonAltsearch.setOnClickListener(this);

        buttonsort = (TextView) view.findViewById(R.id.buttonSortS2S);
        buttonsort.setOnClickListener(this);
        /*ArrayAdapter spinnersortadapter = ArrayAdapter.createFromResource(main_activity.getApplicationContext(),
                R.array.spinnersortS2S, R.layout.spinner_item);

        spinnersortadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        buttonsort.setAdapter(spinnersortadapter);*/

        result = new ArrayList<Journey.JourneyMinimal[]>();
        adapter = new RVS2SAdapter(this.context, this.main_activity.getCoreSystem(), result, dateChecked ? dateselected : null, mListener);
        recyclerview.setAdapter(adapter);

        journeyDate = getView().findViewById(R.id.s2s_train_date);
        //checkBoxDate = getView().findViewById(R.id.checkbox_date);
        journeyDate.setOnClickListener(this);
        //checkBoxDate.setOnClickListener(this);
        //journeyDate.setClickable(false);
        //checkBoxDate.setChecked(false);
        dateChecked = false;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        monthformat = new SimpleDateFormat("MMM", Locale.US);
        dayformat = new SimpleDateFormat("EEE");
        monthselected = monthformat.format(calendar.getTime());
        dateselected = calendar.getTime();
        dayselected = dayformat.format(dateselected);
        //journeyDate.setText(monthselected + " " + day + ", " + dayselected);
        journeyDate.setText("All Day");
        datePickerDialog = new DatePickerDialog(getContext(), R.style.TimePickerTheme, datepickerlistener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);

        //Set nearest station in the from station autocompletetextview if available
        /*if ((PreferenceManager.getDefaultSharedPreferences(getContext())).getBoolean("show_nearest_station", true)) {
            nearestStationCode = sharedPreferences.getString("nearestStationCode", "");
            if (!(nearestStationCode.equalsIgnoreCase(""))) {
                fromStation.setText(nearestStationCode);
                from_station_selected = true;
            }
        }*/



    }
    @Override
    public void onResume(){
        super.onResume();
        //OnResume Fragment
        if ((PreferenceManager.getDefaultSharedPreferences(getContext())).getBoolean("show_nearest_station", true)) {
            fromStation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vd_location, 0, R.drawable.current_location, 0);
        }else{
            fromStation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vd_location, 0, 0, 0);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        String from_id = Tools.extractStationID(fromStation.getText().toString());
        String to_id = Tools.extractStationID(toStation.getText().toString());
        switch (v.getId()) {
            case R.id.buttonsearchS2S:
                if (from_id.equalsIgnoreCase("")
                        || to_id.equalsIgnoreCase("")) {
                    Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
                } else if (from_id.equals(to_id)) {
                    Toasty.error(getActivity(), "Source and destination stations cannot be same", Toast.LENGTH_SHORT).show();
                } else {

                    CacheSystem.S2SQuery q1 = new CacheSystem.S2SQuery(Tools.extractStationID(fromStation.getText().toString()),
                            Tools.extractStationID(toStation.getText().toString()), true);
                    CacheSystem.S2SQuery q2 = new CacheSystem.S2SQuery(Tools.extractStationID(fromStation.getText().toString()),
                            Tools.extractStationID(toStation.getText().toString()), false);
                    asyc = new GetJourneysS2SASync(q1, q2, v, this, buttonsearch, buttonAltsearch, buttonsort, dateChecked ? dateselected : null);

                    asyc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //bsyc = new GetAltRoutesS2SASync(q, v, this);
                    //bsyc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;

            case R.id.buttonsearchAltS2S:

                Tools.hideKeyboardFrom(getContext(), v);
                if (buttonAltsearch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.directTrain))) {
                    buttonAltsearch.setText(getResources().getString(R.string.alternateTrain));
                    buttonAltsearch.setTextColor(getResources().getColor(R.color.primary_text));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                } else if (buttonAltsearch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.alternateTrain))) {
                    buttonAltsearch.setText(getResources().getString(R.string.both));
                    buttonAltsearch.setTextColor(getResources().getColor(R.color.primary_text));
                    //buttonAltsearch.setBackgroundTintList(getResources().getColorStateList(R.color.primary_dark));
                    //ViewCompat.setElevation(buttonAltsearch,10);
                } else {
                    buttonAltsearch.setText(getResources().getString(R.string.directTrain));
                    buttonAltsearch.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                }
                break;

            case R.id.buttonSortS2S:

                Tools.hideKeyboardFrom(getContext(), v);
                if (buttonsort.getText().toString().equalsIgnoreCase(getResources().getString(R.string.departure))) {
                    buttonsort.setText(getResources().getString(R.string.arrival));
                    //buttonAltsearch.setTextColor(getResources().getColor(R.color.primary_text));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                } else if (buttonsort.getText().toString().equalsIgnoreCase(getResources().getString(R.string.arrival))) {
                    buttonsort.setText(getResources().getString(R.string.traveltime));
                    //buttonAltsearch.setTextColor(getResources().getColor(R.color.primary_text));
                    //buttonAltsearch.setBackgroundTintList(getResources().getColorStateList(R.color.primary_dark));
                    //ViewCompat.setElevation(buttonAltsearch,10);
                } else {
                    buttonsort.setText(getResources().getString(R.string.departure));
                    //buttonAltsearch.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                }
                break;
            case R.id.buttonswapS2S:

                SwapSS(v);
                break;
            case R.id.s2s_train_date:

                Tools.hideKeyboardFrom(getContext(), v);
                if (dateChecked) {
                    //journeyDate.setTypeface(null, Typeface.NORMAL);
                    journeyDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    journeyDate.setText("All Day");
                    dateChecked = false;
                    Toasty.info(getActivity(), "Trains running on all days will be shown", Toast.LENGTH_LONG).show();

                } else {
                    setDateForS2S(v);
                    //journeyDate.setTypeface(null, Typeface.BOLD);
                }
                break;

        }
    }

    public void setDateForS2S(View view) {
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener datepickerlistener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            year = i;
            month = i1;
            day = i2;
            calendar.set(year, month, day, 0, 0, 0);
            monthselected = monthformat.format(calendar.getTime());
            calendar.set(year, month, day, 0, 0, 0);
            dateselected = calendar.getTime();
            dayselected = dayformat.format(dateselected);
            //journeyDate.setTypeface(null, Typeface.BOLD);
            journeyDate.setText(monthselected + " " + day + ", " + dayselected);
            journeyDate.setTextColor(getResources().getColor(R.color.primary_text));
            dateChecked = true;
            String journeyDateString = (String) journeyDate.getText();
            //Toasty.info(getActivity(), day + " " + monthselected + "," + year, Toast.LENGTH_LONG).show();
            Toasty.info(getActivity(), "Train running on " + journeyDateString + " will be shown", Toast.LENGTH_LONG).show();
        }
    };

/*
    public void GetTrainsS2S(View view) {
        if (fromStation.getText().toString().equalsIgnoreCase("") || toStation.getText().toString().equalsIgnoreCase("")) {
            Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Integer> result = main_activity.getCoreSystem().get_trains_between_stations(
                    Tools.extractStationID(fromStation.getText().toString()),
                    Tools.extractStationID(toStation.getText().toString()));
            if (result.size() == 0) {

                Toasty.info(getActivity(), "No trains available from " + fromStation.getText().toString()
                        + " to " + toStation.getText().toString() + ".", Toast.LENGTH_SHORT).show();
            } else {

                ArrayList<Journey.JourneyMinimal[]> list = new ArrayList<Journey.JourneyMinimal[]>();
                for (int i = 0; i < result.size(); ++i) {
                    // list.add(result.get(i).toString());
                    // list.add(result.get(i).toJourneyMinimal());
                }
                RVS2SAdapter adapter = new RVS2SAdapter(this.context, this.main_activity.getCoreSystem(), list, dateChecked ? dateselected : null, mListener);
                recyclerview.setAdapter(adapter);
            }
            Tools.hideKeyboardFrom(getContext(), view);
        }

    }

    public void GetJourneysS2S(View view) {
        if (fromStation.getText().toString().equalsIgnoreCase("") || toStation.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Journey.JourneyMinimal> result = main_activity.getCoreSystem().get_journeys_between_stations(
                    Tools.extractStationID(fromStation.getText().toString()),
                    Tools.extractStationID(toStation.getText().toString()));
            if (result.size() == 0) {
                Toast.makeText(getActivity(), "No trains available from " + fromStation.getText().toString()
                        + " to " + toStation.getText().toString() + ".", Toast.LENGTH_SHORT).show();
            } else {
                RVS2SAdapter adapter = new RVS2SAdapter(this.context, result, mListener);
                recyclerview.setAdapter(adapter);
            }
            Tools.hideKeyboardFrom(this.context, view);
        }

    }
*/

    private void SwapSS(View view) {
        Log.d("S2S", "Swapped " + fromStation.getText().toString() + " and " + toStation.getText().toString());
        Animatable animatable = (Animatable) buttonswap.getDrawable();
        animatable.start();
        dataLoadedFromTR = true;
        String swapStationName = fromStation.getText().toString();
        fromStation.setAdapter(null);
        fromStation.setText(toStation.getText().toString());
        fromStation.setAdapter(arrayAdapter);
        dataLoadedFromTR = true;
        toStation.setAdapter(null);
        toStation.setText(swapStationName);
        toStation.setAdapter(arrayAdapter);
    }

    public interface OnFIListener {
        void onFragmentInteraction(Journey.JourneyMinimal selected_jrny, Date date, int tab_num);
    }

    private static class GetJourneysS2SASync extends AsyncTask<Void, Void, ArrayList<Journey.JourneyMinimal[]>> {
        private CacheSystem.S2SQuery q1;
        private CacheSystem.S2SQuery q2;
        private WeakReference<S2S> s2s;
        private WeakReference<View> vw;
        private WeakReference<ActionProcessButton> button_s2s;
        private WeakReference<TextView> button_alt_search;
        private WeakReference<TextView> button_sort;
        Date run_date;
        String run_day;
        boolean isGraphRequired,isGraphLoaded;

        String button_alt_search_selection;
        String button_sort_selection;


        GetJourneysS2SASync(CacheSystem.S2SQuery _q1, CacheSystem.S2SQuery _q2, View _v, S2S _s2s, ActionProcessButton _button_s2s, TextView _buttonAltSearch, TextView _buttonSort, Date _run_date) {
            q1 = _q1;
            q2 = _q2;
            vw = new WeakReference<>(_v);
            s2s = new WeakReference<>(_s2s);
            button_s2s = new WeakReference<>(_button_s2s);
            button_alt_search = new WeakReference<TextView>(_buttonAltSearch);
            button_sort = new WeakReference<TextView>(_buttonSort);
            button_alt_search_selection = this.button_alt_search.get().getText().toString();
            button_sort_selection = this.button_sort.get().getText().toString();
            run_date = _run_date;
            if (!(run_date == null)) {
                run_day = new SimpleDateFormat("EEEE").format(run_date);
            } else {
                run_day = "Allday";
            }

            isGraphRequired=false;
            isGraphLoaded=false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button_s2s.get().setProgress(1);
            if (this.s2s != null) {
                // this.s2s.get().result.clear(); // adding this may work due to shallow
                // copy of ArrayList, see below, check if required , why this is so?
                this.s2s.get().adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected ArrayList<Journey.JourneyMinimal[]> doInBackground(Void... params) {
            ArrayList<Journey.JourneyMinimal[]> direct_res = new ArrayList<Journey.JourneyMinimal[]>();
            ArrayList<Journey.JourneyMinimal[]> alt_res = new ArrayList<Journey.JourneyMinimal[]>();
            ArrayList<Journey.JourneyMinimal[]> res = new ArrayList<Journey.JourneyMinimal[]>();
            if (this.s2s.get().main_activity.getResources().getString(R.string.directTrain).equals(button_alt_search_selection)) {
                direct_res = this.s2s.get().cs_s2s.get(q1);
                if (direct_res == null) {
                    direct_res = this.s2s.get().main_activity.getCoreSystem().get_direct_journeys_between_stations(
                            q1.getFrom_id(), q1.getTo_id());
                    this.s2s.get().cs_s2s.set(q1, direct_res);
                }
                res.addAll(direct_res);
            } else if (this.s2s.get().main_activity.getResources().getString(R.string.alternateTrain).equals(button_alt_search_selection)) {
                isGraphRequired=true;
                isGraphLoaded=this.s2s.get().main_activity.getCoreSystem().isCgraphLoadingComplete();
                if (isGraphLoaded){
                    direct_res = this.s2s.get().cs_s2s.get(q1);
                    if (direct_res == null) {
                        direct_res = this.s2s.get().main_activity.getCoreSystem().get_direct_journeys_between_stations(
                                q1.getFrom_id(), q1.getTo_id());
                        this.s2s.get().cs_s2s.set(q1, direct_res);
                    }

                    alt_res = this.s2s.get().cs_s2s.get(q2);
                    if (alt_res == null) {
                        alt_res = this.s2s.get().main_activity.getCoreSystem().get_multiple_journeys_between_stations(
                                q2.getFrom_id(), q2.getTo_id(), direct_res);
                        this.s2s.get().cs_s2s.set(q2, alt_res);
                    }
                    res.addAll(alt_res);
                }else{
                    return null;

                }
            } else if (this.s2s.get().main_activity.getResources().getString(R.string.both).equals(button_alt_search_selection)) {
                isGraphRequired=true;
                isGraphLoaded=this.s2s.get().main_activity.getCoreSystem().isCgraphLoadingComplete();
                if (isGraphLoaded){

                    direct_res = this.s2s.get().cs_s2s.get(q1);
                if (direct_res == null) {
                    direct_res = this.s2s.get().main_activity.getCoreSystem().get_direct_journeys_between_stations(
                            q1.getFrom_id(), q1.getTo_id());
                    this.s2s.get().cs_s2s.set(q1, direct_res);
                }


                alt_res = this.s2s.get().cs_s2s.get(q2);
                if (alt_res == null) {
                    alt_res = this.s2s.get().main_activity.getCoreSystem().get_multiple_journeys_between_stations(
                            q2.getFrom_id(), q2.getTo_id(), direct_res);
                    this.s2s.get().cs_s2s.set(q2, alt_res);
                }

                res.addAll(direct_res);
                res.addAll(alt_res);
                }else{
                    return null;
                }
            }


            if (this.s2s.get().main_activity.getResources().getString(R.string.departure).equals(button_sort_selection)) {
                Collections.sort(res, Journey.JourneyMinimal.compare_dep);
            } else if (this.s2s.get().main_activity.getResources().getString(R.string.arrival).equals(button_sort_selection)) {
                Collections.sort(res, Journey.JourneyMinimal.compare_arr);
            } else if (this.s2s.get().main_activity.getResources().getString(R.string.traveltime).equals(button_sort_selection)) {
                Collections.sort(res, Journey.JourneyMinimal.compare_travel_t);
            }

            Train train;
            int i = 0;
            for (i = 0; i < res.size(); i++) {
                int j = 0;
                for (j = 0; j < res.get(i).length; j++) {
                    train = this.s2s.get().main_activity.getCoreSystem().get_train_details(res.get(i)[j].getTrain_num());
                    res.get(i)[j].setRun_day(train.getRunday());
                }
            }

            int run_day_int = 127;
            if (!(run_day.equalsIgnoreCase("Allday"))) {
                switch (run_day) {
                    case "Monday":
                        run_day_int = 64;
                        break;
                    case "Tuesday":
                        run_day_int = 32;
                        break;
                    case "Wednesday":
                        run_day_int = 16;
                        break;
                    case "Thursday":
                        run_day_int = 8;
                        break;
                    case "Friday":
                        run_day_int = 4;
                        break;
                    case "Saturday":
                        run_day_int = 2;
                        break;
                    case "Sunday":
                        run_day_int = 1;
                        break;
                }

                int valid_run_day;
                for (i = 0; i < res.size(); i++) {
                    valid_run_day = Tools.getValidRunDays(res.get(i)[0].getRun_day(), (res.get(i)[0].getFrom_day() - 1), run_day_int, 0);
                    if (valid_run_day == 0) {
                        res.remove(i);
                        i--;
                    } else {
                        if (res.get(i).length > 1) {
                            valid_run_day = Tools.getValidRunDays(valid_run_day, ((Tools.get_travel_time(res.get(i)[0].getArr(), res.get(i)[1].getDep(), 1, 1) > 0) ? (res.get(i)[0].getTo_day() - res.get(i)[0].getFrom_day()) : (res.get(i)[0].getTo_day() - res.get(i)[0].getFrom_day() + 1)), res.get(i)[1].getRun_day(), (res.get(i)[1].getFrom_day() - 1));
                            if (valid_run_day == 0) {
                                res.remove(i);
                                i--;
                            }
                        }
                    }
                }
            }

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<Journey.JourneyMinimal[]> result) {
            super.onPostExecute(result);
            if((!isGraphLoaded)&&isGraphRequired){
                Toasty.error(this.s2s.get().getContext(), "Database required for this feature is still loading. Please wait", Toast.LENGTH_LONG).show();

            }
            if(!(result==null)) {

                if (result.size() == 0) {
                    if (this.s2s != null) {
                        Toasty.info(this.s2s.get().main_activity, "No trains available from " + q1.getFrom_id()
                                + " to " + q1.getTo_id() + ".", Toast.LENGTH_SHORT).show();
                        button_s2s.get().setProgress(0);
                    }
                } else {
                    if (this.s2s != null) {
                        this.s2s.get().result = result;
                        this.s2s.get().adapter = new RVS2SAdapter(this.s2s.get().context, this.s2s.get().main_activity.getCoreSystem(), this.s2s.get().result, run_date, this.s2s.get().mListener);
                        this.s2s.get().recyclerview.setAdapter(this.s2s.get().adapter);
                        String current_time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                        int i = 0;
                        for (i = 0; i < result.size(); ++i) {
                            if (this.s2s.get().main_activity.getResources().getString(R.string.departure).equals(button_sort_selection)) {
                                String dep_time = result.get(i)[0].getDep();
                                if (dep_time.compareTo(current_time) > 0) {
                                    this.s2s.get().recyclerview.getLayoutManager().scrollToPosition(i - 1);
                                    /*final RecyclerView recyclerView=this.s2s.get().recyclerview;
                                    final int startPosition= (int) this.s2s.get().recyclerview.getChildAt(Math.max(0, i - 1)).getY();
                                    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {

                                        @Override
                                        public void onGlobalLayout() {
                                            ObjectAnimator.ofInt(recyclerView, "scrollY", startPosition).setDuration(1000).start();
                                        }
                                    };
                                    recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(listener);*/
                                    break;
                                }
                            }

                        }
                    }
                }

            }
            if (this.s2s != null) {
                    Tools.hideKeyboardFrom(this.s2s.get().context, this.vw.get());
                    button_s2s.get().setProgress(0);
                }
        }

    }

    private static class GetAltRoutesS2SASync extends AsyncTask<Void, Void, String> {
        private CacheSystem.S2SQuery q;
        private WeakReference<S2S> s2s;
        private WeakReference<View> vw;

        GetAltRoutesS2SASync(CacheSystem.S2SQuery _q, View _v, S2S _s2s) {
            q = _q;
            vw = new WeakReference<>(_v);
            s2s = new WeakReference<>(_s2s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String res = this.s2s.get().main_activity.getCoreSystem().get_via_stations_one_stop_between_stations(q.getFrom_id(), q.getTo_id());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.length() == 0) {
                if (this.s2s != null) {
                    Toasty.info(this.s2s.get().main_activity, "No alternate routes available from " + q.getFrom_id()
                            + " to " + q.getTo_id() + ".", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (this.s2s != null) {
                    Toasty.info(this.s2s.get().main_activity,
                            "You may wish to travel from " + q.getFrom_id()
                                    + " to " + q.getTo_id() + " via" + result + ".", Toast.LENGTH_LONG).show();
                }
            }
            if (this.s2s != null) {
                Tools.hideKeyboardFrom(this.s2s.get().context, this.vw.get());
            }
        }

    }
}
