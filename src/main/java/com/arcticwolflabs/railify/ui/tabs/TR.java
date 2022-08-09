package com.arcticwolflabs.railify.ui.tabs;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.TRRNonStoppageStation;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.retrofit.RetrofitRunningStatusApi;
import com.arcticwolflabs.railify.core.CacheSystem;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.customadapters.RVTRAdapter;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.dd.processbutton.iml.ActionProcessButton;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static java.lang.Math.abs;


public class TR extends Fragment implements View.OnClickListener {
    private ArrayList<Train.TrainMinimal> trainsList;
    ArrayAdapter<Train.TrainMinimal> arrayAdapter;
    AutoCompleteTextView train_no;
    Boolean train_no_selected;
    private ActionProcessButton button;
    private RecyclerView recyclerview;
    private RVTRAdapter adapter;
    private Train train;
    private LinearLayout parent_linear_layout;

    private Context context;
    private MainActivity main_activity;
    private OnFIListener mListener;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    TextView train_runday;
    SimpleDateFormat monthformat;
    String monthselected;
    int year;
    int month;
    int day;
    private GetStationsTRASync asyc;
    private boolean isTRAsncRunning;

    private CacheSystem.CacheTR cs_tr;

    private TextView inside_train;
    private boolean is_inside_train;

    ArrayList<String> non_stoppage_stops;

    RetrofitRunningStatusApi retrofitRunningStatusApi;


    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TR() {
        cs_tr = new CacheSystem.CacheTR();

    }

    public static TR newInstance(Context _context) {
        TR fragment = new TR();
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
        return inflater.inflate(R.layout.fragment_tr, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        train_no_selected = false;
        main_activity = (MainActivity) getActivity();
        trainsList = main_activity.getCoreSystem().getTrains();
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, trainsList);
        train_no = view.findViewById(R.id.train_route_train_no);

        train_no.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonshape));
        train_no.setAdapter(arrayAdapter);
        //recyclerview = view.findViewById(R.id.recyclerViewTR);
        //recyclerview.setLayoutManager(new LinearLayoutManager(this.getContext()));
        button = (ActionProcessButton) getView().findViewById(R.id.buttonsearchTR);
        button.setMode(ActionProcessButton.Mode.ENDLESS);
        button.setOnClickListener(this);
        isTRAsncRunning = false;
        train_runday = getView().findViewById(R.id.train_route_train_run_day);
        parent_linear_layout = getView().findViewById(R.id.linearLayoutTR);
        inside_train = getView().findViewById(R.id.inside_train);
        inside_train.setOnClickListener(this);
        is_inside_train = false;
/*        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        monthformat = new SimpleDateFormat("MMM", Locale.US);
        monthselected = monthformat.format(calendar.getTime());
        train_runday.setText(day + "-" + monthselected + "-" + year);
        train_runday.setOnClickListener(this);
        datePickerDialog = new DatePickerDialog(getActivity(), datepickerlistener, year, month, day);
        calendar.add(Calendar.DATE, -4);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);*/
        //result = new Train(0, "");

        //non_stoppage_stops=new ArrayList<>();
        //adapter = new RVTRAdapter(this.context, main_activity.getCoreSystem(), non_stoppage_stops, mListener);
        //recyclerview.setAdapter(adapter);

        retrofitRunningStatusApi = new RetrofitRunningStatusApi(this.getContext());
        train_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (train_no.isPerformingCompletion()) {
                    train_no_selected = true;
                    if (inside_train.getVisibility() == View.GONE) {
                        inside_train.setVisibility(View.VISIBLE);
                    }
                    Tools.hideKeyboardFrom(getContext(), view);
                }
                if ((train_no_selected) && (i1 > i2)) {
                    train_no_selected = false;
                    train_no.setText("");
                    train_runday.setText(getResources().getString(R.string.rv_runs_on));
                    train_runday.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    if (is_inside_train) {
                        is_inside_train = false;
                        inside_train.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                    train = null;
                } else {
                    // Perform your task here... Like calling web service, Reading data from SQLite database, etc...
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
/*
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
            train_runday.setText(day + " " + monthselected + ", " + year);
            Toast.makeText(getActivity(), day + " " + monthselected + "," + year, Toast.LENGTH_LONG).show();
        }
    };*/

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void get_arguments_from_s2s(Journey.JourneyMinimal selected_jrny, Date start_date, View train_no_view, View recycler_view) {
        train_no_selected = false;
        train_no.setText(selected_jrny.getTrain_num() + ", " + selected_jrny.getTrain_name());
        train_no_selected = true;
        if (inside_train.getVisibility() == View.GONE) {
            inside_train.setVisibility(View.VISIBLE);
        }
        is_inside_train = false;
        inside_train.setTextColor(getResources().getColor(android.R.color.darker_gray));
        Tools.hideKeyboardFrom(getContext(), this.train_no);
        Log.d("train name no", "get_arguments_from_s2s: " + train_no.getText().toString());
        if (!(asyc == null)) {
            asyc.cancel(true);
        }
        /*while (isTRAsncRunning) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        isTRAsncRunning = true;
        CacheSystem.TRQuery q = new CacheSystem.TRQuery(selected_jrny.getTrain_num());
        asyc = new GetStationsTRASync(q, selected_jrny.getTrain_num(), start_date, train_no_view, this, button, train_runday, parent_linear_layout, retrofitRunningStatusApi);
        asyc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetStationsTR(View view) {
        if (train_no.getText().toString().equalsIgnoreCase("")) {
            Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
        } else {
            train = main_activity.getCoreSystem().get_train_details(
                    Integer.valueOf(Tools.extractTrainNo(train_no.getText().toString())));
            //adapter = new RVTRAdapter(this.getContext(), main_activity.getCoreSystem(), result, mListener);
            //recyclerview.setAdapter(adapter);
            Tools.hideKeyboardFrom(getContext(), view);
        }
    }

    private static class GetStationsTRASync extends AsyncTask<Void, Train, Train> {
        private CacheSystem.TRQuery q;
        CacheSystem.TRobject cache_res;
        private boolean isNonStoppageStationUpdateComplete;
        private int train_no;
        private Train train;
        private Date start_date;
        ArrayList<String> non_stoppage_stop;
        ArrayList<ArrayList<TRRNonStoppageStation>> trrNonStoppageStationRoot;
        ArrayList<TRRNonStoppageStation> trrNonStoppageStations;
        private WeakReference<TR> tr;
        private WeakReference<View> vw;
        private WeakReference<ActionProcessButton> pr_button;
        private WeakReference<TextView> tr_runday;
        private RetrofitRunningStatusApi retrofitRunningStatusApi;

        private WeakReference<LinearLayout> parent_linear_layout;
        private CardView item_parent_view;
        private RelativeLayout item_parent_relatve_layout;
        private ImageView item_parent_station_tour;
        private LottieAnimationView txtView_parent_lottie_animation;
        private TextView txtView_parent_stn_name;
        private TextView txtView_parent_sch_arr;
        private TextView txtView_parent_sch_dep;
        private TextView txtView_parent_act_arr;
        private TextView txtView_parent_act_dep;
        private TextView txtView_parent_day_train;
        private TextView txtView_parent_pf_train;
        private TextView txtView_parent_dist;
        private TextView txtView_child_stn_name;
        private TextView txtView_child_sch_dep;
        private TextView txtView_child_day_train;
        private TextView txtView_child_dist;
        private View currentStationView;
        private int currentStationPosition;

        private ViewGroup parent_view;
        private TextView txtView_stn_code;


        String stn_name_code;
        private ArrayList<LinearLayout> childLinearLayoutContainer;

        int travel_time;
        int diff_dist;
        int non_stoppage_time;
        int non_stoppage_day;
        String non_stoppage_sch_dep;

        GetStationsTRASync(CacheSystem.TRQuery _q, int _train_no, Date _start_date, View _v, TR _tr, ActionProcessButton _button, TextView _train_runday, LinearLayout _parent_linear_layout, RetrofitRunningStatusApi _retrofitRunningStatusApi) {
            q = _q;
            isNonStoppageStationUpdateComplete = false;
            train_no = _train_no;
            start_date=_start_date;
            trrNonStoppageStationRoot = new ArrayList<ArrayList<TRRNonStoppageStation>>();
            childLinearLayoutContainer = new ArrayList<LinearLayout>();
            vw = new WeakReference<>(_v);
            tr = new WeakReference<>(_tr);
            pr_button = new WeakReference<>(_button);
            tr_runday = new WeakReference<>(_train_runday);
            parent_linear_layout = new WeakReference<>(_parent_linear_layout);
            retrofitRunningStatusApi = _retrofitRunningStatusApi;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pr_button.get().setProgress(1);
            if (this.tr != null) {
                this.tr.get().train = new Train(0, "");
            }
        }

        @Override
        protected Train doInBackground(Void... params) {
            cache_res = this.tr.get().cs_tr.get(q);

            if (cache_res == null) {
                train = this.tr.get().main_activity.getCoreSystem().get_train_details(train_no);
                if (Tools.isNetworkAvailable(tr.get().getContext())) {
                    train=retrofitRunningStatusApi.getRunningStatus(this.tr.get().main_activity.getCoreSystem(), train, start_date);
                }
                publishProgress(train);
                //for direct route
                int i = 0;
                String stn_code_one, stn_code_two;
                int dist;
                for (i = 0; i < (train.getStationStops().size() - 1); i++) {
                    if (isCancelled()) {
                        this.tr.get().isTRAsncRunning = false;
                        break;
                    }
                    travel_time = Tools.get_travel_time(train.getStationStops().get(i).getSch_dep(), train.getStationStops().get(i + 1).getSch_arr(), train.getStationStops().get(i).getDay(), train.getStationStops().get(i + 1).getDay());
                    diff_dist = abs(train.getStationStops().get(i).getDist() - train.getStationStops().get(i + 1).getDist());
                    trrNonStoppageStations = new ArrayList<TRRNonStoppageStation>();
                    stn_code_one = train.getStationStops().get(i).getCode();
                    stn_code_two = train.getStationStops().get(i + 1).getCode();
                    dist = abs(train.getStationStops().get(i + 1).getDist() - train.getStationStops().get(i).getDist());
                    non_stoppage_stop = this.tr.get().main_activity.getCoreSystem().get_direct_stops(stn_code_one, stn_code_two, dist);
                    train.setNon_stoppage_stops_stationwise(i, non_stoppage_stop);
                    int j = 0;
                    trrNonStoppageStations.clear();
                    for (j = 1; j < ((non_stoppage_stop.size() / 3) - 1); j++) {
                        non_stoppage_time = (Integer) (abs((train.getStationStops().get(i).getDist()) - (Integer.parseInt(non_stoppage_stop.get((3 * j) + 2)))) * travel_time) / diff_dist;
                        non_stoppage_day = train.getStationStops().get(i).getDay() + (Integer) (non_stoppage_time / (24 * 60));
                        int arr_min = (Integer) ((non_stoppage_time % (24 * 60)) % 60) + (Integer.parseInt(train.getStationStops().get(i).getSch_dep().substring(3, 5)));
                        int arr_hr = (Integer) ((non_stoppage_time % (24 * 60)) / 60) + (Integer.parseInt(train.getStationStops().get(i).getSch_dep().substring(0, 2)));
                        arr_hr = arr_hr + arr_min / 60;
                        arr_min = (arr_min % 60);
                        non_stoppage_day = non_stoppage_day + arr_hr / 24;
                        arr_hr = arr_hr % 24;
                        non_stoppage_sch_dep = (String.format("%02d", arr_hr)) + ":" + (String.format("%02d", arr_min));
                        trrNonStoppageStations.add(new TRRNonStoppageStation(non_stoppage_stop.get((3 * j) + 1), non_stoppage_stop.get((3 * j) + 2), non_stoppage_sch_dep, non_stoppage_day));
                    }
                    trrNonStoppageStationRoot.add(trrNonStoppageStations);
                    Log.d("direct_route", "direct_route: code complete for this");

                }
                if (!isCancelled()) {
                    cache_res = new CacheSystem.TRobject(train, trrNonStoppageStationRoot);
                    this.tr.get().cs_tr.set(q, cache_res);
                }
            } else {
                train = cache_res.getTrain();
                if (Tools.isNetworkAvailable(tr.get().getContext())) {
                    train=retrofitRunningStatusApi.getRunningStatus(this.tr.get().main_activity.getCoreSystem(), train, start_date);
                }
                publishProgress(train);
                trrNonStoppageStationRoot = cache_res.getTrrNonStoppageStationRoot();
            }
            this.tr.get().isTRAsncRunning = false;
            isNonStoppageStationUpdateComplete = true;
            //for direct route
            return train;
        }

        @Override
        protected void onProgressUpdate(Train... values) {
            super.onProgressUpdate(values);
            if (values[0].getStationStops().size() == 0) {
                if (this.tr != null) {
                    Toasty.error(this.tr.get().main_activity, "Train " + values[0].getTrainNum()
                            + ": " + values[0].getName() + " has no stations.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (this.tr != null) {
                    this.tr.get().train = values[0];
                    tr_runday.get().setTextColor(this.tr.get().getResources().getColor(android.R.color.holo_orange_dark));
                    tr_runday.get().setText(Html.fromHtml(Tools.extractRunDaysFromDecimal(this.tr.get().main_activity, values[0].getRunday(), 0)));
                    //this.tr.get().adapter = new RVTRAdapter(this.tr.get().context, this.tr.get().main_activity.getCoreSystem(),this.tr.get().result, this.tr.get().mListener);
                    //this.tr.get().recyclerview.setAdapter(this.tr.get().adapter);
                    parent_linear_layout.get().removeAllViews();
                    int i = 0;
                    for (i = 0; i < values[0].getStationStops().size(); i++) {

                        LayoutInflater inflater = (LayoutInflater) this.tr.get().main_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.tr_rv_layout_parent, null);
                        parent_linear_layout.get().addView(rowView);
                        item_parent_view = parent_linear_layout.get().getChildAt(i).findViewById(R.id.tr_rv_parent_card_view);
                        item_parent_relatve_layout = parent_linear_layout.get().getChildAt(i).findViewById(R.id.tr_rv_parent_relative_layout);
                        item_parent_station_tour = parent_linear_layout.get().getChildAt(i).findViewById(R.id.tr_rv_parent_station_tour);
                        txtView_parent_lottie_animation = parent_linear_layout.get().getChildAt(i).findViewById(R.id.lottie_current_loation);
                        txtView_parent_stn_name = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_stn_name);
                        txtView_parent_sch_arr = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_sch_arr);
                        txtView_parent_sch_dep = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_sch_dep);
                        txtView_parent_act_arr = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_act_arr);
                        txtView_parent_act_dep = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_act_dep);
                        txtView_parent_day_train = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_day_train);
                        txtView_parent_pf_train = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_pf);
                        txtView_parent_dist = parent_linear_layout.get().getChildAt(i).findViewById(R.id.parent_dist);
                        final LinearLayout childLinearlayout = parent_linear_layout.get().getChildAt(i).findViewById(R.id.childLinearLayoutContainer);
                        childLinearLayoutContainer.add(childLinearlayout);

                        if(values[0].getCurrentStation().equalsIgnoreCase(values[0].getStationStops().get(i).getCode())){
                            txtView_parent_lottie_animation.setVisibility(View.VISIBLE);
                            currentStationView=rowView;
                        }
                        txtView_parent_stn_name.setText(values[0].getStationStops().get(i).getName() + " (" + values[0].getStationStops().get(i).getCode() + ")");
                        txtView_parent_sch_arr.setText(values[0].getStationStops().get(i).getSch_arr());
                        txtView_parent_sch_dep.setText(values[0].getStationStops().get(i).getSch_dep());
                        txtView_parent_act_arr.setText(values[0].getStationStops().get(i).getAct_arr());
                        txtView_parent_act_dep.setText(values[0].getStationStops().get(i).getAct_dep());
                        txtView_parent_day_train.setText("Day:" + (Integer.toString(values[0].getStationStops().get(i).getDay())));
                        String pf = values[0].getStationStops().get(i).getPlatform();
                        if (pf == "") {
                            txtView_parent_pf_train.setText("pf: -");
                        } else {
                            txtView_parent_pf_train.setText("pf: " + (pf));
                        }
                        txtView_parent_dist.setText((Integer.toString(values[0].getStationStops().get(i).getDist())) + " km");
                        item_parent_relatve_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (childLinearlayout.getChildCount() > 0) {
                                    if (childLinearlayout.getVisibility() == View.VISIBLE) {
                                        childLinearlayout.setVisibility(View.GONE);
                                    } else {
                                        childLinearlayout.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (isNonStoppageStationUpdateComplete) {
                                        Toasty.info(tr.get().main_activity, "No non stoppage station", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toasty.info(tr.get().main_activity, "Getting non stoppage station data", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
                        item_parent_station_tour.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                parent_view = (ViewGroup) v.getParent();
                                txtView_stn_code = parent_view.findViewById(R.id.parent_stn_name);
                                stn_name_code = (String) txtView_stn_code.getText();
                                tr.get().mListener.onFragmentInteraction(stn_name_code.substring((stn_name_code.indexOf("(") + 1), stn_name_code.indexOf(")")),
                                        stn_name_code.substring(0, (stn_name_code.indexOf("(") - 1)));
                            }
                        });

                    }
                    NestedScrollView scrollView= (NestedScrollView) parent_linear_layout.get().getParent();
                    if(!(currentStationView==null)){
                        currentStationPosition=(int) currentStationView.getY();
                    }else{
                        currentStationPosition= (int) parent_linear_layout.get().getChildAt(0).getY();
                    }
                    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            ObjectAnimator.ofInt(scrollView, "scrollY", currentStationPosition).setDuration(1000).start();
                        }
                    };
                    parent_linear_layout.get().getViewTreeObserver().addOnGlobalLayoutListener(listener);
                }
            }
            if (this.tr != null) {
                pr_button.get().setProgress(0);
                Tools.hideKeyboardFrom(this.tr.get().context, this.vw.get());
            }
        }

        @Override
        protected void onPostExecute(Train result) {
            super.onPostExecute(result);
            train = result;
            int i, j;

            for (i = 0; i < trrNonStoppageStationRoot.size(); i++) {
                if (trrNonStoppageStationRoot.get(i).size() > 0) {
                    for (j = 0; j < trrNonStoppageStationRoot.get(i).size(); j++) {
                        LayoutInflater inflater = (LayoutInflater) this.tr.get().main_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.tr_rv_layout_child, null);
                        childLinearLayoutContainer.get(i).addView(rowView);
                        txtView_child_stn_name = childLinearLayoutContainer.get(i).getChildAt(j).findViewById(R.id.child_stn_name);
                        txtView_child_sch_dep = childLinearLayoutContainer.get(i).getChildAt(j).findViewById(R.id.child_sch_dep);
                        txtView_child_day_train = childLinearLayoutContainer.get(i).getChildAt(j).findViewById(R.id.child_day_train);
                        txtView_child_dist = childLinearLayoutContainer.get(i).getChildAt(j).findViewById(R.id.child_dist);
                        txtView_child_stn_name.setText(tr.get().main_activity.getCoreSystem().getStationNameFromCode(trrNonStoppageStationRoot.get(i).get(j).getStnName()) + " (" + trrNonStoppageStationRoot.get(i).get(j).getStnName() + ")");
                        txtView_child_dist.setText(trrNonStoppageStationRoot.get(i).get(j).getDist() + " km");
                        txtView_child_sch_dep.setText(trrNonStoppageStationRoot.get(i).get(j).getSch_dep());
                        txtView_child_day_train.setText("Day: " + Integer.toString(trrNonStoppageStationRoot.get(i).get(j).getDay()));
                    }
                }
            }

            Toasty.success(tr.get().main_activity, "Updating non stoppage station complete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.train_route_train_run_day:
                setDateForTrainNo(v);
                break;*/
            case R.id.buttonsearchTR:
                if (Tools.extractTrainNo(train_no.getText().toString()).equalsIgnoreCase("")) {
                    Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
                } else {
                    if (!(asyc == null)) {
                        asyc.cancel(true);
                    }
                    /*while (isTRAsncRunning) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                    isTRAsncRunning = true;
                    CacheSystem.TRQuery q = new CacheSystem.TRQuery(Integer.valueOf(Tools.extractTrainNo(train_no.getText().toString())));
                    asyc = new GetStationsTRASync(q, Integer.valueOf(Tools.extractTrainNo(train_no.getText().toString())), calendar.getTime(), v, this, button, train_runday, parent_linear_layout, retrofitRunningStatusApi);
                    asyc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;

            case R.id.inside_train:

                Tools.hideKeyboardFrom(getContext(), v);
                if (is_inside_train) {
                    is_inside_train = false;
                    inside_train.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    //buttonAltsearch.setTextColor(getResources().getColor(R.color.primary_text));
                    //ViewCompat.setElevation(buttonAltsearch,0);
                } else {
                    if (train_no_selected) {
                        if (Tools.checkLocationPermissions(context)) {

                            main_activity.startLocationWorker();

                            if (!(train == null)) {
                                train.setTrainLocation(main_activity.getCoreSystem().calculateTrainLocation(train));
                                if (!(train.getTrainLocation() == null)) {
                                    if (train.getTrainLocation().isGrossLocationValid() || train.getTrainLocation().isFineLocationValid()) {

                                        is_inside_train = true;
                                        inside_train.setTextColor(getResources().getColor(R.color.primary_text));
                                    } else {
                                        Toasty.error(main_activity, "Your location shows that you are not in the train", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            } else {
                                Toasty.info(main_activity, "Load the train route to enable this option", Toast.LENGTH_SHORT).show();

                            }

                            //buttonAltsearch.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            //ViewCompat.setElevation(buttonAltsearch,0);
                        } else if (!(ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.ACCESS_FINE_LOCATION))) {
                            Toasty.info(main_activity, "Location permission is required to enable this feature. Location permission is permanently disbled", Toast.LENGTH_SHORT).show();
                        } else {

                            ActivityCompat.requestPermissions(main_activity,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                            //Tools.openSettings(context);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (Tools.checkLocationPermissions(getContext())) {
                        //main_activity.startLocationService();;
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    is_inside_train = false;
                    inside_train.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        }
        return;
    }


    public interface OnFIListener {
        void onFragmentInteraction(String station_id, String station_name);
    }

}
