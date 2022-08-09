package com.arcticwolflabs.railify.ui.customadapters;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.dynamics.TRRNonStoppageStation;
import com.arcticwolflabs.railify.base.dynamics.Train;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.ui.tabs.TR;

import java.util.ArrayList;


public class RVTRAdapter extends RecyclerView.Adapter<RVTRAdapter.ViewHolder> {
    private Train result;
    public Context context;
    public CoreSystem coreSystem;
    private int lastPosition = -1;
    private TR.OnFIListener transfer_arg;
    ArrayList<TRRNonStoppageStation> trrNonStoppageStations;

    public RVTRAdapter(Context _context, CoreSystem _coreSystem, ArrayList<TRRNonStoppageStation> result,
                       TR.OnFIListener transfer_arg) {
        this.trrNonStoppageStations = result;
        this.coreSystem=_coreSystem;
        this.context = _context;
        this.transfer_arg = transfer_arg;
    }

    @Override
    public RVTRAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tr_rv_layout_parent, null);*/
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tr_rv_layout_child, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        /*viewHolder.txtView_parent_stn_name.setText(result.getStationStops().get(position).getName() + " (" + result.getStationStops().get(position).getCode() + ")");
        viewHolder.txtView_parent_sch_arr.setText(result.getStationStops().get(position).getSch_arr());
        viewHolder.txtView_parent_sch_dep.setText(result.getStationStops().get(position).getSch_dep());
        viewHolder.txtView_parent_day_train.setText("Day:" + (Integer.toString(result.getStationStops().get(position).getDay())));
        String pf = result.getStationStops().get(position).getPlatform();
        if (pf == "") {
            viewHolder.txtView_parent_pf_train.setText("pf: -");
        } else {
            viewHolder.txtView_parent_pf_train.setText("pf: " + (pf));
        }
        viewHolder.txtView_parent_dist.setText((Integer.toString(result.getStationStops().get(position).getDist())) + " km");
        setAnimation(viewHolder.itemView, position);*/

        /*viewHolder.item_parent_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer_arg.onFragmentInteraction(result.getStationStops().get(position).getCode(),
                        result.getStationStops().get(position).getName());
                Log.d("TR2PLC", "Okay from TRAdapter");
            }
        });*/
        /*if (!(result.getNon_stoppage_stops(position) == null)) {
            int i;
            int no_of_non_stoppage_stops;
            trrNonStoppageStations = result.getNon_stoppage_stops(position);
            no_of_non_stoppage_stops = trrNonStoppageStations.size() / 3;
            int j=0;
            if(!(trrNonStoppageStations.get(1).equalsIgnoreCase(result.getStationStops().get(position).getCode()))){
                Log.d("direct_route", "direct_routetradapter: code ");
            }
            for(j=0; j<((trrNonStoppageStations.size()/3)-1); j++){
                Log.d("direct_route", "direct_route_tradapter: code "+ trrNonStoppageStations.get((3*j)+1)+" dist "+ trrNonStoppageStations.get((3*j)+2));
            }
            Log.d("direct_route", "direct_route_tradapter: code complete for this");
            if ((viewHolder.child_layout.getChildCount() == 0) && (no_of_non_stoppage_stops > 2)) {

                viewHolder.txtView_child_stn_name.setText(coreSystem.getStationNameFromCode(trrNonStoppageStations.get(position).getStnName()+" ("+ trrNonStoppageStations.get(position).getDist()+")");
                    viewHolder.txtView_child_dist.setText(trrNonStoppageStations.get(position).getDist());


            *//*for(i=no_of_non_stoppage_stops;i<viewHolder.child_layout.getChildCount();i++){
                viewHolder.child_layout.getChildAt(i).setVisibility(View.GONE);
            }*//*
            viewHolder.child_layout.setVisibility(View.GONE);
            }
        }*/
        viewHolder.txtView_child_stn_name.setText(coreSystem.getStationNameFromCode(trrNonStoppageStations.get(position).getStnName())+" ("+ trrNonStoppageStations.get(position).getStnName()+")");
        viewHolder.txtView_child_dist.setText(trrNonStoppageStations.get(position).getDist());

    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private CardView item_parent_view;
        private CardView item_child_view;
        private TextView txtView_parent_stn_name;
        private TextView txtView_parent_sch_arr;
        private TextView txtView_parent_sch_dep;
        private TextView txtView_parent_day_train;
        private TextView txtView_parent_pf_train;
        private TextView txtView_parent_dist;
        private LinearLayout child_layout;
        private TextView txtView_child_stn_name;
        private TextView txtView_child_sch_dep;
        private TextView txtView_child_day_train;
        private TextView txtView_child_dist;
        private int max_non_stoppage_stops;
        private int t_max_non_stoppage_stops;
        private int i;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            context = itemLayoutView.getContext();
            /*item_parent_view = itemLayoutView.findViewById(R.id.tr_rv_parent_card_view);
            txtView_parent_stn_name = itemLayoutView.findViewById(R.id.parent_stn_name);
            txtView_parent_sch_arr = itemLayoutView.findViewById(R.id.parent_sch_arr);
            txtView_parent_sch_dep = itemLayoutView.findViewById(R.id.parent_sch_dep);
            txtView_parent_day_train = itemLayoutView.findViewById(R.id.parent_day_train);
            txtView_parent_pf_train = itemLayoutView.findViewById(R.id.parent_pf);
            txtView_parent_dist = itemLayoutView.findViewById(R.id.parent_dist);
            item_parent_view.setOnClickListener(this);
            child_layout = itemLayoutView.findViewById(R.id.tr_rv_child_layout);
            item_child_view = itemLayoutView.findViewById(R.id.tr_rv_child_card_view);*/
            txtView_child_stn_name = itemLayoutView.findViewById(R.id.child_stn_name);
            txtView_child_sch_dep = itemLayoutView.findViewById(R.id.child_sch_dep);
            txtView_child_day_train = itemLayoutView.findViewById(R.id.child_day_train);
            txtView_child_dist = itemLayoutView.findViewById(R.id.child_dist);
            /*max_non_stoppage_stops = 0;
            for (i = 0; i < result.getStationStops().size(); i++) {
                if (!(result.getNon_stoppage_stops(i) == null)) {
                    t_max_non_stoppage_stops = (result.getNon_stoppage_stops(i).size() / 3)-2;
                    if (max_non_stoppage_stops < t_max_non_stoppage_stops) {
                        max_non_stoppage_stops = t_max_non_stoppage_stops;
                    }
                }
            }*/


            /*for (i = 0; i < max_non_stoppage_stops; i++) {
                item_child_view = new CardView(context);
                item_child_view.setCardBackgroundColor(Color.parseColor("#64ffda"));
                item_child_view.setCardElevation((float)2);
                item_child_view.setRadius(10);
                item_child_view.setContentPadding(10,10,10,10);
                item_child_view.setUseCompatPadding(true);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.tr_rv_layout_child, null);
                //item_child_view=itemLayoutView.findViewById(R.id.tr_rv_child_card_view);
                txtView_child_stn_name = rowView.findViewById(R.id.child_stn_name);
                txtView_child_sch_dep = rowView.findViewById(R.id.child_sch_dep);
                txtView_child_day_train = rowView.findViewById(R.id.child_day_train);
                txtView_child_dist = rowView.findViewById(R.id.child_dist);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if(!(rowView.getParent()==null)){
                    ((ViewGroup)rowView.getParent()).removeView(rowView);
                }
                child_layout.addView(rowView,layoutParams);
            }*/

        }

        /*@Override
        public void onClick(View v) {
            if (v.getId() == R.id.tr_rv_parent_card_view) {
                if (child_layout.getVisibility() == View.VISIBLE) {
                    child_layout.setVisibility(View.GONE);
                } else {
                    child_layout.setVisibility(View.VISIBLE);
                }
            }

        }*/
    }


    @Override
    public int getItemCount() {
        return trrNonStoppageStations.size();
    }

    public void updateData(Train _result) {
        result = _result;
        notifyDataSetChanged();
    }

}

