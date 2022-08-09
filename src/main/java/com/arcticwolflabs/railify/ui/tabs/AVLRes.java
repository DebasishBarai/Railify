package com.arcticwolflabs.railify.ui.tabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Availability;
import com.arcticwolflabs.railify.base.dynamics.Fare;
import com.arcticwolflabs.railify.base.netapi.Avl_res_data;
import com.arcticwolflabs.railify.ui.customadapters.RVAVLADAPTER;

import java.util.ArrayList;


public class AVLRes extends Fragment {


    private Context context;
    private Availability availability;
    private Fare fare;
    Integer[] empty_array = {-1, -1, -1, -1, -1};
    boolean[] fare_class= {false, false, false, false, false, false, false, false,false,false};
    int[][] fare_amount={{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1}};

    private ArrayList<Avl_res_data> avl_res_data;
    RecyclerView avl_rv;
    RVAVLADAPTER adapter;
    TableLayout tableLayout;
    private OnFIListener mListener;

    public AVLRes() {
        // Required empty public constructor
    }

    public static AVLRes newInstance(Context _context) {
        AVLRes fragment = new AVLRes();
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

    public void setResult() {
        displayresult();
    }

    private void displayresult() {
        Integer[][] avl_array = availability.getAvailability();
        int i;
        for (i = 0; i < 9; i++) {
            if (fare_class[i]) {
                switch (i) {
                    case 0:
                            createdataarray("1A", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 1:
                            createdataarray("2A", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 2:
                            createdataarray("3A", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 3:
                            createdataarray("CC", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 4:
                            createdataarray("FC", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 5:
                            createdataarray("SL", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 6:
                            createdataarray("2S", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 7:
                            createdataarray("3E", avlval(avl_array[i]), fare_amount[i]);
                        break;
                    case 8:
                        createdataarray("EC", avlval(avl_array[i]), fare_amount[i]);
                        break;

                }
            }
        }
        createdataarray("UR", "",fare_amount[9]);
        adapter=new RVAVLADAPTER(avl_res_data);
        avl_rv.setAdapter(adapter);

    }

    public String getDate(){
        return availability.getDate();
    }

    private String avlval(Integer[] eachclass) {
        if (!(eachclass[0] == -1)) {
            return "Available " + Integer.toString(eachclass[0]);
        }
        if (!(eachclass[1] == -1)) {
            return "Rac " + Integer.toString(eachclass[1]);
        }
        if (!(eachclass[2] == -1)) {
            switch (eachclass[4]) {
                case 0:
                    return "GNWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 1:
                    return "RLWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 2:
                    return "PQWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 3:
                    return "RLGN " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 4:
                    return "RSWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 5:
                    return "RQWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);
                case 6:
                    return "CKWL " + Integer.toString(eachclass[3]) + "/" + "WL "+Integer.toString(eachclass[2]);

            }
        }
        return "";
    }

    private void createdataarray(String avlcl, String value, int[] _fare_amount) {
        avl_res_data.add(new Avl_res_data(avlcl,value,_fare_amount));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_avl_res, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        avl_res_data=new ArrayList<Avl_res_data>();
        avl_rv=view.findViewById(R.id.avl_res_rv);
        avl_rv.setLayoutManager(new LinearLayoutManager(this.context));
        availability.getAvailability();
        setResult();
    }

    public void setAvailbility(Availability _availability) {
        availability = _availability;

    }
    public void setFare(Fare _fare){
        fare=_fare;
        fare_class=fare.getFare_class();
        fare_amount=fare.getFare_amount();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFIListener) {
            mListener = (OnFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFIListener {
        void onFragmentInteraction(Uri uri);
    }
}
