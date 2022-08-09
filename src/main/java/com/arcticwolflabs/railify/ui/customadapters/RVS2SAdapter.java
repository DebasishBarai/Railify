package com.arcticwolflabs.railify.ui.customadapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.ui.tabs.S2S;
import com.arcticwolflabs.railify.ui.utils.Tools;

import java.util.ArrayList;
import java.util.Date;


public class RVS2SAdapter extends RecyclerView.Adapter {
    private ArrayList<Journey.JourneyMinimal[]> result;
    private Context context;
    private int lastPosition = -1;
    private S2S.OnFIListener transfer_arg;
    private static int TYPE_SINGLE_JOURNEY=1;
    private static int TYPE_MULTIPLE_JOURNEY=2;
    private RecyclerView.ViewHolder viewHolder;
    private int position;
    CoreSystem csystem;
    Date run_date;

    public RVS2SAdapter(Context _context, CoreSystem _csystem,ArrayList<Journey.JourneyMinimal[]> result, Date run_date,
                        S2S.OnFIListener transfer_arg) {
        this.context = _context;
        this.csystem=_csystem;
        this.result = result;
        this.run_date = run_date;
        this.transfer_arg = transfer_arg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == TYPE_SINGLE_JOURNEY) {
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.s2s_rv_single_journey_layout, null);
            return new SingleViewHolder(itemLayoutView);
        }else{
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.s2s_rv_double_journey_layout, null);
            return new DoubleViewHolder(itemLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder _viewHolder, int position) {

        this.viewHolder = _viewHolder;
        this.position = position;


        if((getItemViewType(position)==TYPE_SINGLE_JOURNEY)) {
            SingleViewHolder singleViewHolder = (SingleViewHolder) _viewHolder;
            singleViewHolder.txtView_train_code.setText(Integer.toString(result.get(position)[0].getTrain_num()));
            singleViewHolder.txtView_train_name.setText(result.get(position)[0].getTrain_name());
            singleViewHolder.txtView_from_station.setText(result.get(position)[0].getFrom_id()+", "+result.get(position)[0].getFrom_stn_name());
            singleViewHolder.txtView_to_station.setText(result.get(position)[0].getTo_id()+", "+result.get(position)[0].getTo_stn_name());
            singleViewHolder.txtView_sch_dep.setText(result.get(position)[0].getDep());
            singleViewHolder.txtView_sch_arr.setText(result.get(position)[0].getArr());
            singleViewHolder.txtView_runs_on.setText(Html.fromHtml(Tools.extractRunDaysFromDecimal(context, result.get(position)[0].getRun_day(),(result.get(position)[0].getFrom_day()-1))));
            singleViewHolder.txtView_total_travel_time.setText(Tools.get_hrmin_from_min(result.get(position)[0].getTravel_time()));

            _viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(_viewHolder.itemView.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.popup_s2s, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ps2s_one:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[0], run_date, 1);
                                    break;
                                case R.id.ps2s_two:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[0], run_date, 3);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();

                }
            });
        }
        if((getItemViewType(position)==TYPE_MULTIPLE_JOURNEY)) {
            DoubleViewHolder doubleViewHolder = (DoubleViewHolder) _viewHolder;
            doubleViewHolder.txtView_train_code.setText(Integer.toString(result.get(position)[0].getTrain_num()));
            doubleViewHolder.txtView_train_name.setText(result.get(position)[0].getTrain_name());
            doubleViewHolder.txtView_from_station.setText(result.get(position)[0].getFrom_id()+", "+result.get(position)[0].getFrom_stn_name());
            doubleViewHolder.txtView_to_station.setText(result.get(position)[0].getTo_id()+", "+result.get(position)[0].getTo_stn_name());
            doubleViewHolder.txtView_sch_dep.setText(result.get(position)[0].getDep());
            doubleViewHolder.txtView_sch_arr.setText(result.get(position)[0].getArr());
            doubleViewHolder.txtView_runs_on.setText(Html.fromHtml(Tools.extractRunDaysFromDecimal(context, result.get(position)[0].getRun_day(),(result.get(position)[0].getFrom_day()-1))));
            doubleViewHolder.txtView_total_travel_time.setText(Tools.get_hrmin_from_min(Tools.get_double_journey_total_time(result.get(position))));

            doubleViewHolder.txtView_train_code_two.setText(Integer.toString(result.get(position)[1].getTrain_num()));
            doubleViewHolder.txtView_train_name_two.setText(result.get(position)[1].getTrain_name());
            doubleViewHolder.txtView_from_station_two.setText(result.get(position)[1].getFrom_id()+", "+result.get(position)[1].getFrom_stn_name());
            doubleViewHolder.txtView_to_station_two.setText(result.get(position)[1].getTo_id()+", "+result.get(position)[1].getTo_stn_name());
            doubleViewHolder.txtView_sch_dep_two.setText(result.get(position)[1].getDep());
            doubleViewHolder.txtView_sch_arr_two.setText(result.get(position)[1].getArr());
            doubleViewHolder.txtView_runs_on_two.setText(Html.fromHtml(Tools.extractRunDaysFromDecimal(context, result.get(position)[1].getRun_day(),(result.get(position)[1].getFrom_day()-1))));

            ((DoubleViewHolder) _viewHolder).tableLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(_viewHolder.itemView.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.popup_s2s, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ps2s_one:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[0], run_date,1);
                                    break;
                                case R.id.ps2s_two:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[0], run_date, 3);
                                    Toast.makeText(viewHolder.itemView.getContext(), "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();

                }
            });

            ((DoubleViewHolder) _viewHolder).tableLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(_viewHolder.itemView.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.popup_s2s, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ps2s_one:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[1], run_date, 1);
                                    break;
                                case R.id.ps2s_two:
                                    transfer_arg.onFragmentInteraction(result.get(_viewHolder.getAdapterPosition())[1], run_date, 3);
                                    Toast.makeText(viewHolder.itemView.getContext(), "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();

                }
            });
        }
        setAnimation(_viewHolder.itemView, position);



    }

    @Override
    public int getItemViewType(int position){
        if (result.get(position).length==2){
            return TYPE_MULTIPLE_JOURNEY;
        }
        else {
            return TYPE_SINGLE_JOURNEY;
        }
    }
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class SingleViewHolder extends RecyclerView.ViewHolder {
        public CardView parent_layout;
        public TextView txtView_train_code;
        public TextView txtView_train_name;
        public TextView txtView_from_station;
        public TextView txtView_to_station;
        public TextView txtView_sch_dep;
        public TextView txtView_sch_arr;
        public TextView txtView_runs_on;
        public TextView txtView_total_travel_time;

        public SingleViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            parent_layout = itemLayoutView.findViewById(R.id.s2s_rv_single_journey_layout);
            txtView_train_code = itemLayoutView.findViewById(R.id.train_code);
            txtView_train_name = itemLayoutView.findViewById(R.id.train_name);
            txtView_from_station = itemLayoutView.findViewById(R.id.from_station);
            txtView_to_station = itemLayoutView.findViewById(R.id.to_station_two);
            txtView_sch_dep = itemLayoutView.findViewById(R.id.sch_dep_two);
            txtView_sch_arr = itemLayoutView.findViewById(R.id.sch_arr_two);
            txtView_runs_on = itemLayoutView.findViewById(R.id.runs_on_two);
            txtView_total_travel_time = itemLayoutView.findViewById(R.id.total_travel_time_one);
        }
    }

    public static class DoubleViewHolder extends RecyclerView.ViewHolder {
        public CardView parent_layout;
        public TableLayout tableLayout1;
        public TableLayout tableLayout2;
        public TextView txtView_train_code;
        public TextView txtView_train_name;
        public TextView txtView_from_station;
        public TextView txtView_to_station;
        public TextView txtView_sch_dep;
        public TextView txtView_sch_arr;
        public TextView txtView_runs_on;
        public TextView txtView_total_travel_time;

        public TextView txtView_train_code_two;
        public TextView txtView_train_name_two;
        public TextView txtView_from_station_two;
        public TextView txtView_to_station_two;
        public TextView txtView_sch_dep_two;
        public TextView txtView_sch_arr_two;
        public TextView txtView_runs_on_two;


        public DoubleViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            parent_layout = itemLayoutView.findViewById(R.id.s2s_rv_double_journey_layout);
            tableLayout1=itemLayoutView.findViewById(R.id.tablelayout_base);
            tableLayout2=itemLayoutView.findViewById(R.id.tablelayout_two);
            txtView_train_code = itemLayoutView.findViewById(R.id.train_code);
            txtView_train_name = itemLayoutView.findViewById(R.id.train_name);
            txtView_from_station = itemLayoutView.findViewById(R.id.from_station);
            txtView_to_station = itemLayoutView.findViewById(R.id.to_station_two);
            txtView_sch_dep = itemLayoutView.findViewById(R.id.sch_dep_two);
            txtView_sch_arr = itemLayoutView.findViewById(R.id.sch_arr_two);
            txtView_runs_on = itemLayoutView.findViewById(R.id.runs_on_two);
            txtView_total_travel_time = itemLayoutView.findViewById(R.id.total_travel_time_two);

            txtView_train_code_two = itemLayoutView.findViewById(R.id.train_code_two);
            txtView_train_name_two = itemLayoutView.findViewById(R.id.train_name_two);
            txtView_from_station_two = itemLayoutView.findViewById(R.id.from_station_two);
            txtView_to_station_two = itemLayoutView.findViewById(R.id.to_station_three);
            txtView_sch_dep_two = itemLayoutView.findViewById(R.id.sch_dep_three);
            txtView_sch_arr_two = itemLayoutView.findViewById(R.id.sch_arr_three);
            txtView_runs_on_two = itemLayoutView.findViewById(R.id.runs_on_three);
        }
    }

    @Override
    public int getItemCount() {
        return result.size();
    }
}

