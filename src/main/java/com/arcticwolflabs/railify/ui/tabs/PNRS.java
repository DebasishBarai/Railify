package com.arcticwolflabs.railify.ui.tabs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.PNRStatus;
import com.arcticwolflabs.railify.base.dynamics.Stop;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.base.netapi.PNRStatusAPI;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.ui.MainActivity;
import com.arcticwolflabs.railify.ui.customadapters.PNRRVAdapter;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class PNRS extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private MainActivity main_activity;
    private long pnr_num;
    private AutoCompleteTextView et_pnr_num;
    ArrayAdapter<Long> pnrArrayAdapter;
    ArrayList<Long> pnr_array;
    private ActionProcessButton buttonquery;
    private CardView pnr_res;
    private PNRStatusAPI pnrsapi;

    RecyclerView pnr_res_rv;
    PNRRVAdapter adapter;

    AdView pnrAdview;

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PNRS() {
        // Required empty public constructor
    }

    public static PNRS newInstance(Context _context) {
        PNRS fragment = new PNRS();
        fragment.setContext(_context);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pnrs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main_activity = (MainActivity) getActivity();
        et_pnr_num = view.findViewById(R.id.pnrs_et1);
        pnr_array=main_activity.getCoreSystem().get_saved_pnrs();
        pnrArrayAdapter=new ArrayAdapter<Long >(main_activity.getApplicationContext(),android.R.layout.simple_list_item_1,pnr_array);
        et_pnr_num.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!(pnr_array==null)){
                    et_pnr_num.showDropDown();
                }
                et_pnr_num.requestFocus();
                return false;
            }
        });
        et_pnr_num.setAdapter(pnrArrayAdapter);
        buttonquery = view.findViewById(R.id.pnrs_querybutton);
        buttonquery.setMode(ActionProcessButton.Mode.ENDLESS);
        pnr_res = view.findViewById(R.id.pnr_res_cardview);


        pnr_res_rv = (RecyclerView) view.findViewById(R.id.pnr_res_rv);
        pnr_res_rv.setLayoutManager(new LinearLayoutManager(main_activity.getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        buttonquery.setOnClickListener(this);
        pnrsapi = new PNRStatusAPI();

        pnrAdview=view.findViewById(R.id.pnrAdview);
        AdRequest pnrAdrequest=new AdRequest.Builder().build();
        pnrAdview.loadAd(pnrAdrequest);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pnrs_querybutton:
                if (isNetworkAvailable()){
                    if (et_pnr_num.getText().toString().equalsIgnoreCase("")) {
                        Toasty.error(getActivity(), "Fill in all required entry fields and try again", Toast.LENGTH_SHORT).show();
                    } if (!(et_pnr_num.getText().toString().length()==10)) {
                        Toasty.error(getActivity(), "Invalid PNR", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        GetStatusPNRAsync(v);
                    }
                }else {

                    Toasty.info(getActivity(), "No internet connection. Connect to internet and try again", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void GetStatusPNRAsync(View view) {
        /* below for testing only for the moment */
        pnr_num = 0;
        try {
            pnr_num = Long.parseLong(et_pnr_num.getText().toString());

        } catch (NumberFormatException ignored) {
        }
        if (Long.toString(pnr_num).length() != 10) {
            Toasty.info(this.context, "Enter a valid PNR number", Toast.LENGTH_SHORT).show();
            return;
        }
        main_activity.getCoreSystem().add_pnr_to_saved_pnr(pnr_num);
        pnr_array=main_activity.getCoreSystem().get_saved_pnrs();
        pnrArrayAdapter=new ArrayAdapter<Long >(main_activity.getApplicationContext(),android.R.layout.simple_list_item_1,pnr_array);
        et_pnr_num.setAdapter(pnrArrayAdapter);

        HTTPGet hr = new HTTPGet(main_activity.getApplicationContext(), main_activity.getCoreSystem(), pnrsapi, pnr_num, pnr_res, pnr_res_rv, adapter,buttonquery);
        hr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Tools.hideKeyboardFrom(this.context, view);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private static class HTTPGet extends AsyncTask<String, Void, PNRStatus> {
        private WeakReference<Context> _context;
        private WeakReference<ActionProcessButton> _buttonquery;
        private WeakReference<CardView> _cardview;
        private WeakReference<RecyclerView> _pnr_res_rv;
        private PNRRVAdapter _adapter;
        private WeakReference<PNRStatusAPI> _pnrsapi;
        private long _pnr_num;
        private WeakReference<CoreSystem> _csys;
        private Train train;
        private String _sch_dep = "";
        private String _sch_arr = "";
        private String _from_station = "";
        private String _to_station = "";


        public HTTPGet(Context context, CoreSystem csys, PNRStatusAPI pnrsapi, long pnr_num, CardView tv, RecyclerView pnr_res_rv, PNRRVAdapter adapter,ActionProcessButton buttonquery) {
            _csys = new WeakReference<CoreSystem>(csys);
            _pnrsapi = new WeakReference<PNRStatusAPI>(pnrsapi);
            _cardview = new WeakReference<CardView>(tv);
            _pnr_res_rv = new WeakReference<RecyclerView>(pnr_res_rv);
            _adapter = adapter;
            _pnr_num = pnr_num;
            _context = new WeakReference<Context>(context);
            _buttonquery=new WeakReference<ActionProcessButton>(buttonquery);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _buttonquery.get().setProgress(1);
        }
        @Override
        protected PNRStatus doInBackground(String[] _params) {
            PNRStatus pnrStatus = null;
            try{
                pnrStatus = _pnrsapi.get().query(_pnr_num);
            if (!(pnrStatus.getTrainNo()==null)) {
                train = _csys.get().get_train_details(Integer.parseInt(pnrStatus.getTrainNo()));
                for (Stop stop : train.getStationStops()) {
                    if (stop.getCode().equals(pnrStatus.getBoardingPoint())) {
                        _from_station = stop.getCode() + "," + stop.getName();
                        _sch_dep = stop.getSch_dep();
                    }
                    if (stop.getCode().equals(pnrStatus.getDestination())) {
                        _to_station = stop.getCode() + "," + stop.getName();
                        _sch_arr = stop.getSch_arr();
                    }
                }
            }

        }catch (Exception e){
                e.printStackTrace();
            }
            return pnrStatus;
        }

        protected void onPostExecute(PNRStatus result) {
            try {
                if (!(result.getTrainNo() == null)) {
                    if (_cardview.get() != null) {
                        TextView _pnr_no = (TextView) _cardview.get().findViewById(R.id.pnr_no);
                        TextView _train_no = (TextView) _cardview.get().findViewById(R.id.train_code);
                        TextView _train_name = (TextView) _cardview.get().findViewById(R.id.train_name);
                        TextView _boarding_date = (TextView) _cardview.get().findViewById(R.id.boarding_date);
                        TextView _journey_class = (TextView) _cardview.get().findViewById(R.id.journey_class);
                        TextView _from_station_tv = (TextView) _cardview.get().findViewById(R.id.from_station);
                        TextView _sch_dep_tv = (TextView) _cardview.get().findViewById(R.id.sch_dep);
                        TextView _to_station_tv = (TextView) _cardview.get().findViewById(R.id.to_station);
                        TextView _sch_arr_tv = (TextView) _cardview.get().findViewById(R.id.sch_arr);
                        TextView _chart_status = (TextView) _cardview.get().findViewById(R.id.chart_status);
                        _pnr_no.setText("PNR: " + (Long.toString(result.getPnr_num())));
                        _train_no.setText(result.getTrainNo());
                        _train_name.setText(result.getTrainName());
                        _boarding_date.setText("Journey date: " + (result.getTrainJourneyDate()));
                        _journey_class.setText("Class: " + (result.getTicketClass()));
                        _from_station_tv.setText("From " + _from_station);
                        _sch_dep_tv.setText("dep " + _sch_dep);
                        _to_station_tv.setText("To " + _to_station);
                        _sch_arr_tv.setText("arr " + _sch_arr);
                        _chart_status.setText(result.getChartStatus());
                        _adapter = new PNRRVAdapter(result.getPassengers());
                        _pnr_res_rv.get().setAdapter(_adapter);
                        _cardview.get().setVisibility(View.VISIBLE);
                    }
                } else {
                    Toasty.info(_context.get(), "Flushed PNR or unable to fetch data from server", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            _buttonquery.get().setProgress(0);
        }
    }

}
