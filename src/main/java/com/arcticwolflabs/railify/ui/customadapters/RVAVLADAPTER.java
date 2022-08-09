package com.arcticwolflabs.railify.ui.customadapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.netapi.Avl_res_data;

import java.util.ArrayList;

public class RVAVLADAPTER extends RecyclerView.Adapter<RVAVLADAPTER.ViewHolder> {
    ArrayList<Avl_res_data> avl_res_data;

    public RVAVLADAPTER(ArrayList<Avl_res_data> _avl_res_data) {
        avl_res_data = _avl_res_data;
    }

    @NonNull
    @Override
    public RVAVLADAPTER.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avl_rv_layout, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.avl_rv_cl_name.setText(avl_res_data.get(position).getAvlcl());
        viewHolder.avl_rv_cl_availability.setText(avl_res_data.get(position).getValue());
        String gn_fare_amount;
        String tk_fare_amount;
        if(avl_res_data.get(position).getFare_amount()[0]==(-1)){
            gn_fare_amount="NA";
        }else {
            gn_fare_amount=Integer.toString(avl_res_data.get(position).getFare_amount()[0]);
        }
        if(avl_res_data.get(position).getFare_amount()[1]==(-1)){
            tk_fare_amount="NA";

        }else{
        tk_fare_amount = Integer.toString(avl_res_data.get(position).getFare_amount()[1]);
    }
        viewHolder.avl_rv_fare.setText("GENERAL: "+gn_fare_amount+"/TATKAL: "+tk_fare_amount);
    }

    @Override
    public int getItemCount() {
        return avl_res_data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parent_layout;
        private TextView avl_rv_cl_name;
        private TextView avl_rv_cl_availability;
        private TextView avl_rv_fare;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            parent_layout = itemLayoutView.findViewById(R.id.avl_rv_layout);
            avl_rv_cl_name = itemLayoutView.findViewById(R.id.avl_rv_cl_name);
            avl_rv_cl_availability = itemLayoutView.findViewById(R.id.avl_rv_cl_availability);
            avl_rv_fare=itemLayoutView.findViewById(R.id.avl_rv_fare);
        }
    }
}
