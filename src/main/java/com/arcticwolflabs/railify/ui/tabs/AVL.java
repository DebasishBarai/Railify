package com.arcticwolflabs.railify.ui.tabs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Availability;
import com.arcticwolflabs.railify.base.dynamics.Fare;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.AvailabilityAPI;
import com.arcticwolflabs.railify.base.netapi.FareAPI;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.dd.processbutton.iml.ActionProcessButton;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static com.arcticwolflabs.railify.R.color.primary_dark;

public class AVL extends Fragment implements View.OnClickListener {

    private Context context;
    private MainActivity main_activity;
    private int train_no;
    boolean train_no_selected;
    private ArrayList<Train.TrainMinimal> trainsList;
    private ArrayAdapter<Train.TrainMinimal> train_no_arrayAdapter;
    private ArrayList<Stop.StopMinimal> station_list_from, station_list_to;
    private ArrayAdapter<Stop.StopMinimal> from_station_arrayAdapter;
    private ArrayAdapter<Stop.StopMinimal> to_station_arrayAdapter;
    Availability availability;
    ArrayList<Availability> availabilityArrayList;

    AutoCompleteTextView txtview_train_no;
    Spinner from_station;
    Spinner to_station;
    private AVL.OnFragmentInteractionListener mListener;
    private AvailabilityAPI avlapi;
    private ActionProcessButton buttonquery;
    private TextView spinner_quaotatype;
    String quaotatype;
    AVLRes AVLRes_val;
    private TabLayout avl_tab;
    private ViewPager avl_viewpager;
    private Avl_date_viewpager_adapter avl_date_viewpager_adapter;
    TableLayout tableLayoutRes;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    TextView journeyDate;
    SimpleDateFormat monthformat;
    String monthselected;
    int year;
    int month;
    int day;
    FareAPI fareAPI;
    String fare;
    int rr;
    int ss;
    boolean setFromS2S;

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AVL() {
        // Required empty public constructor
    }

    public static AVL newInstance(Context _context) {
        AVL fragment = new AVL();
        fragment.setContext(_context);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_avl, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        train_no_selected = false;
        main_activity = (MainActivity) getActivity();
        trainsList = main_activity.getCoreSystem().getTrains();
        train_no_arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, trainsList);
        txtview_train_no = view.findViewById(R.id.avl_train_no);
        txtview_train_no.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        txtview_train_no.setAdapter(train_no_arrayAdapter);
        buttonquery = (ActionProcessButton) view.findViewById(R.id.buttonqueryAVL);
        buttonquery.setMode(ActionProcessButton.Mode.ENDLESS);
        from_station = (Spinner)view.findViewById(R.id.fromStationHint);
        from_station.setPopupBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        from_station.setEnabled(true);
        station_list_from = new ArrayList<>();
        //station_list_from.add(new Stop.StopMinimal(-1, "", ""));
        from_station_arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, station_list_from);
        from_station.setAdapter(from_station_arrayAdapter);
        from_station.setEnabled(false);
        to_station = view.findViewById(R.id.toStationHint);
        station_list_to = new ArrayList<>();
        to_station_arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, station_list_to);
        to_station.setPopupBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        to_station.setAdapter(to_station_arrayAdapter);
        to_station.setEnabled(false);

        journeyDate = getView().findViewById(R.id.train_avl_train_date);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        monthformat = new SimpleDateFormat("MMM", Locale.US);
        monthselected = monthformat.format(calendar.getTime());
        journeyDate.setText(day + " " + monthselected + ", " + year);
        journeyDate.setOnClickListener(this);
        datePickerDialog = new DatePickerDialog(getActivity(), R.style.TimePickerTheme,  datepickerlistener, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
        calendar.add(Calendar.DATE, 120);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis() - 1000);
        calendar.add(Calendar.DATE, -120);
        calendar.set(year, month, day, 0, 0, 0);
        spinner_quaotatype = (TextView) view.findViewById(R.id.quotaType);
        spinner_quaotatype.setOnClickListener(this);
        /*ArrayAdapter spinnerquotatypeadapter = ArrayAdapter.createFromResource(main_activity.getApplicationContext(),
                R.array.quotaTypeArray, android.R.layout.simple_list_item_1);
        spinnerquotatypeadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_quaotatype.setAdapter(spinnerquotatypeadapter);*/
        availability = new Availability();
        availabilityArrayList = new ArrayList<Availability>();
        fareAPI = new FareAPI();
        rr = 0;
        ss = 0;
        setFromS2S = false;


        txtview_train_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((!Tools.extractTrainNo(txtview_train_no.getText().toString()).equals("")) && (!(txtview_train_no.getText().toString().equals("")))) {
                    Tools.hideKeyboardFrom(getContext(), view);
                    train_no_selected = true;
                    from_station.setEnabled(true);
                    train_no = Integer.parseInt(Tools.extractTrainNo(txtview_train_no.getText().toString()));
                    station_list_from = main_activity.getCoreSystem().core_get_all_station_train_no(train_no, 0);
                    station_list_from.remove(station_list_from.size() - 1);
                    from_station_arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, station_list_from);
                    from_station.setAdapter(from_station_arrayAdapter);
                    if (setFromS2S) {
                        from_station.setSelection(rr);
                    }
                }
                if (train_no_selected & (i1 > i2)) {
                    train_no_selected = false;
                    txtview_train_no.setText("");
                    from_station.setAdapter(null);
                    from_station.setEnabled(false);
                    to_station.setAdapter(null);
                    to_station.setEnabled(false);
                } else {
                    // Perform your task here... Like calling web service, Reading data from SQLite database, etc...
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });
        from_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if ((!Tools.extractTrainNo(txtview_train_no.getText().toString()).equals("")) && (!(txtview_train_no.getText().toString().equals("")))) {
                    to_station.setEnabled(true);
                    station_list_to = main_activity.getCoreSystem().core_get_all_station_train_no(train_no, position + 1);
                    to_station_arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, station_list_to);
                    to_station.setAdapter(to_station_arrayAdapter);
                    if (setFromS2S) {
                        to_station.setSelection(ss);
                        setFromS2S = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        AVLRes_val = new AVLRes();
        avl_tab = view.findViewById(R.id.tabs_avl);
        avl_tab.setTabTextColors(getResources().getColorStateList(R.color.primary_text));
        avl_viewpager = view.findViewById(R.id.avl_viewpager);
        avl_tab.setupWithViewPager(avl_viewpager);
        avl_date_viewpager_adapter = new Avl_date_viewpager_adapter(getChildFragmentManager());
        avlapi = new AvailabilityAPI();
        buttonquery.setOnClickListener(this);
    }

    private class Avl_date_viewpager_adapter extends FragmentStatePagerAdapter {


        private final ArrayList<AVLRes> fragmentlist = new ArrayList<AVLRes>();

        public Avl_date_viewpager_adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int i;
            for (i = 0; i < fragmentlist.size(); i++) {
                if (i == position) {
                    AVLRes avl_val = fragmentlist.get(i);
                    return avl_val;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragmentlist.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentlist.get(position).getDate();
        }

        public CharSequence removeAllFragment() {
            fragmentlist.clear();
            avl_tab.removeAllTabs();
            notifyDataSetChanged();
            return null;
        }


        public void addFragment(AVLRes fragment, Availability availability, Fare fare) {
            fragment.setAvailbility(availability);
            fragment.setFare(fare);
            fragmentlist.add(fragment);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void GetAvailabilityAVLAsync(View view) {
        if (isNetworkAvailable()) {
            if (!train_no_selected) {
                Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
            } else {
                Train tr = main_activity.getCoreSystem().get_train_details(train_no);
                ArrayList<Stop> stps = tr.getStationStops();
                String from_id = Tools.extractStationID(from_station.getSelectedItem().toString());
                String to_id = Tools.extractStationID(to_station.getSelectedItem().toString());

                int from_idx = -1, to_idx = -1;
                for (Stop s : stps) {
                    if (s.getCode().equals(from_id)) {
                        from_idx = s.getIdx();
                    } else if (s.getCode().equals(to_id)) {
                        to_idx = s.getIdx();
                    }
                }

                Journey.JourneyMinimal jrny = new Journey.JourneyMinimal(
                        train_no, Tools.extractTrainName(txtview_train_no.getText().toString()),
                        from_id, "", to_id, "", "", "", -1, -1,
                        from_idx, to_idx);
                quaotatype = spinner_quaotatype.getText().toString();
                calendar.set(year, month, day, 0, 0, 0);
                HTTPGet hr = new HTTPGet(this, availabilityArrayList, avlapi, jrny, quaotatype, calendar, avl_tab, avl_viewpager, avl_date_viewpager_adapter, AVLRes_val, tableLayoutRes, fareAPI, buttonquery);
                hr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Tools.hideKeyboardFrom(this.context, view);
            }
        } else {
            Toasty.info(getActivity(), "No internet connection. Connect to internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.train_avl_train_date:
                Tools.hideKeyboardFrom(getContext(), v);
                setDateForTrainNo(v);
                break;
            case R.id.quotaType:
                Tools.hideKeyboardFrom(getContext(), v);
                if (spinner_quaotatype.getText().toString().equalsIgnoreCase(getResources().getString(R.string.quotatypeGeneral))) {
                    spinner_quaotatype.setText(getResources().getString(R.string.quotatypeTatkal));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                } else if (spinner_quaotatype.getText().toString().equalsIgnoreCase(getResources().getString(R.string.quotatypeTatkal))) {
                    spinner_quaotatype.setText(getResources().getString(R.string.quotatypeLadies));
                    //buttonAltsearch.setBackgroundTintList(getResources().getColorStateList(R.color.primary_dark));
                    //ViewCompat.setElevation(buttonAltsearch,10);
                } else if (spinner_quaotatype.getText().toString().equalsIgnoreCase(getResources().getString(R.string.quotatypeLadies))) {
                    spinner_quaotatype.setText(getResources().getString(R.string.quotatypeHandicapped));
                    //buttonAltsearch.setBackgroundTintList(getResources().getColorStateList(R.color.primary_dark));
                    //ViewCompat.setElevation(buttonAltsearch,10);
                }else {
                    spinner_quaotatype.setText(getResources().getString(R.string.quotatypeGeneral));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                }
                break;
            case R.id.buttonqueryAVL:
                GetAvailabilityAVLAsync(v);
                break;
        }
    }

    public void setDateForTrainNo(View view) {
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
            journeyDate.setText(day + " " + monthselected + ", " + year);
            Toasty.info(getActivity(), day + " " + monthselected + "," + year, Toast.LENGTH_LONG).show();
        }
    };

    public void get_arguments_from_s2s(Journey.JourneyMinimal selected_jrny, Date run_date) {
        setFromS2S = true;
        rr = selected_jrny.getFrom_idx();
        ss = selected_jrny.getTo_idx() - selected_jrny.getFrom_idx() - 1;
        train_no = selected_jrny.getTrain_num();
        train_no_selected = true;
        txtview_train_no.setAdapter(null);
        txtview_train_no.setText("");
        txtview_train_no.setText("" + train_no + ", " + selected_jrny.getTrain_name());
        txtview_train_no.setAdapter(train_no_arrayAdapter);
        if (!(run_date == null)) {
            try {
                calendar.setTime(run_date);
            } catch (Exception e) {

            }
        } else {
            calendar.setTime(Calendar.getInstance().getTime());
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        monthformat = new SimpleDateFormat("MMM", Locale.US);
        monthselected = monthformat.format(calendar.getTime());
        journeyDate.setText(day + " " + monthselected + ", " + year);

        /*from_station.setEnabled(true);
        station_list_from = main_activity.getCoreSystem().core_get_all_station_train_no(train_no, 0);
        station_list_from.remove(station_list_from.size() - 1);
        from_station_arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, station_list_from);
        from_station.setAdapter(from_station_arrayAdapter);
        from_station_arrayAdapter.notifyDataSetChanged();
        from_station.post(new Runnable() {
            @Override
            public void run() {
                from_station.setSelection(rr);
            }
        });
        to_station.setEnabled(true);
        station_list_to = main_activity.getCoreSystem().core_get_all_station_train_no(train_no, selected_jrny.getFrom_idx() + 1);
        to_station_arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, station_list_to);
        to_station.setAdapter(to_station_arrayAdapter);
        to_station_arrayAdapter.notifyDataSetChanged();
        to_station.post(new Runnable() {
            @Override
            public void run() {
                to_station.setSelection(ss);
            }
        });*/
        Tools.hideKeyboardFrom(this.context, this.from_station);

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private static class HTTPGet extends AsyncTask<String, Void, String> {
        private WeakReference<AVL> _avl;
        Fare _fare;

        private WeakReference<AvailabilityAPI> _avlapi;
        private WeakReference<TabLayout> _tablayout;
        private WeakReference<ViewPager> _viewpager;
        private WeakReference<Avl_date_viewpager_adapter> _avl_date_viewpager_adapter;
        private WeakReference<Journey.JourneyMinimal> _jrny;
        private ArrayList<Availability> _availabilityarraylist;
        private String _quotatype;
        private Calendar _cal;
        private WeakReference<AVLRes> _avl_res_val;
        private WeakReference<TableLayout> _tablelayout;
        private WeakReference<FareAPI> _fareAPI;
        private WeakReference<ActionProcessButton> query_button;

        public HTTPGet(AVL avl_val, ArrayList<Availability> availabilityarraylist, AvailabilityAPI avlapi, Journey.JourneyMinimal jrny, String quotatype, Calendar cal, TabLayout tabLayout, ViewPager viewPager, Avl_date_viewpager_adapter avl_date_viewpager_adapter_val, AVLRes AVLRes_val, TableLayout tablelayout, FareAPI fareAPI, ActionProcessButton _query_button) {
            _avl = new WeakReference<AVL>(avl_val);
            _availabilityarraylist = availabilityarraylist;
            _avlapi = new WeakReference<AvailabilityAPI>(avlapi);
            _jrny = new WeakReference<Journey.JourneyMinimal>(jrny);
            _tablayout = new WeakReference<TabLayout>(tabLayout);
            _viewpager = new WeakReference<ViewPager>(viewPager);
            _avl_date_viewpager_adapter = new WeakReference<Avl_date_viewpager_adapter>(avl_date_viewpager_adapter_val);
            _avl_res_val = new WeakReference<AVLRes>(AVLRes_val);
            _tablelayout = new WeakReference<TableLayout>(tablelayout);
            _quotatype = quotatype;
            _cal = cal;
            _fareAPI = new WeakReference<FareAPI>(fareAPI);
            query_button = new WeakReference<>(_query_button);
            _fare = new Fare();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            query_button.get().setProgress(1);
        }

        @Override
        protected String doInBackground(String[] _params) {
            String data = "";
            try {
                if (_jrny.get() != null) {
                    switch (_quotatype) {
                        case "General":
                            _availabilityarraylist = _avlapi.get().query(_jrny.get(), Availability.QUOTATYPE_GEN, _cal, 6);
                            break;
                        case "Tatkal":
                            _availabilityarraylist = _avlapi.get().query(_jrny.get(), Availability.QUOTATYPE_TKL, _cal, 6);
                            break;
                        case "Ladies":
                            _availabilityarraylist = _avlapi.get().query(_jrny.get(), Availability.QUOTATYPE_LAD, _cal, 6);
                            break;
                        case "Handicaped":
                            _availabilityarraylist = _avlapi.get().query(_jrny.get(), Availability.QUOTATYPE_HNCP, _cal, 6);
                            break;
                    }
                    Integer[] no_avl = {-1, -1, -1, -1, -1};
                    int i;
                    for (i = 0; i < _availabilityarraylist.size(); i++) {
                        Integer[][] availability = _availabilityarraylist.get(i).getAvailability();
                        if (!(availability[i] == no_avl)) {
                            data = data + " " + _availabilityarraylist.get(i).getDate() + " " + Arrays.toString(availability[1]) + "\n";
                        }
                    }
                    _fare = _fareAPI.get().query(_jrny.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        protected void onPostExecute(String result) {
            int i;
            if (_avl_date_viewpager_adapter.get() != null) {
                _avl_date_viewpager_adapter.get().removeAllFragment();
                if (_availabilityarraylist.size() == 0) {
                    Availability _availability = new Availability();
                    _cal.add(Calendar.DATE, -6);
                    int month = _cal.get(Calendar.MONTH) + 1;
                    String _date = _cal.get(Calendar.DAY_OF_MONTH) + "-" + month;
                    _availability.setDate(_date);
                    _availabilityarraylist.add(_availability);
                }
                for (i = 0; i < _availabilityarraylist.size(); i++) {
                    _avl_date_viewpager_adapter.get().addFragment(new AVLRes(), _availabilityarraylist.get(i), _fare);
                }

                try {
                    _avl_date_viewpager_adapter.get().notifyDataSetChanged();
                    _viewpager.get().setSaveFromParentEnabled(false);
                    _viewpager.get().removeAllViews();
                    _viewpager.get().setAdapter(_avl_date_viewpager_adapter.get());
                } catch (Exception e) {
                    Log.d("avl error", "avl error androidx: " + e);
                    Toasty.error(_avl.get().getActivity(), "Internal error. Please try again.", Toast.LENGTH_SHORT).show();
                }
                query_button.get().setProgress(0);
            }
        }

    }

}
