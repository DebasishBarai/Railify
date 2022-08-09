package com.arcticwolflabs.railify.ui.customadapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.PNRPassenger;

import java.util.ArrayList;

public class PNRRVAdapter extends RecyclerView.Adapter<PNRRVAdapter.ViewHolder> {
    ArrayList<PNRPassenger> passenger_list;
    public PNRRVAdapter(ArrayList<PNRPassenger> _passenger_list){
        passenger_list=_passenger_list;
    }
    @NonNull
    @Override
    public PNRRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pnr_single_pas_res_rv, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder._pas_no.setText(passenger_list.get(position).getPassengerIndex());
        holder._curr_stat.setText(passenger_list.get(position).getBookingBerth());
        holder._quota.setText(passenger_list.get(position).getCurrentStatus());
    }

    @Override
    public int getItemCount() {
        return passenger_list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView _pas_no;
        TextView _curr_stat;
        TextView _quota;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            _pas_no = (TextView)itemLayoutView.findViewById(R.id.passenger_no);
            _curr_stat = (TextView) itemLayoutView.findViewById(R.id.current_status);
            _quota = (TextView) itemLayoutView.findViewById(R.id.quota);
        }
    }
}
