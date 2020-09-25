package com.laacompany.travelplanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.Plan;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.R;

import java.util.ArrayList;

public class SharingAdapter extends RecyclerView.Adapter<SharingAdapter.SharingViewHolder>{

    private Context mContext;
    private ArrayList<Plan> mPlans;
    public SharingAdapter(Context context, ArrayList<Plan> plans){
        mContext = context;
        mPlans = plans;
    }

    @NonNull
    @Override
    public SharingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SharingAdapter.SharingViewHolder(LayoutInflater.from(mContext),parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SharingViewHolder holder, int position) {
        holder.bind(mPlans.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlans.size();
    }

    public class SharingViewHolder extends RecyclerView.ViewHolder {

        private TextView mTVTitle, mTVName, mTVAddress, mTVTravelTime, mTVArrivedTime, mTVDuration, mTVSummary;
        private ImageView mIVPreview;

        public SharingViewHolder(LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_sharing, parent, false));

            mTVTitle = itemView.findViewById(R.id.id_item_sharing_tv_title);
            mTVName = itemView.findViewById(R.id.id_item_sharing_tv_name);
            mTVAddress = itemView.findViewById(R.id.id_item_sharing_tv_address);
            mTVTravelTime = itemView.findViewById(R.id.id_item_sharing_tv_travel_time);
            mTVArrivedTime = itemView.findViewById(R.id.id_item_sharing_tv_arrived_time);
            mTVDuration = itemView.findViewById(R.id.id_item_sharing_tv_duration);
            mTVSummary = itemView.findViewById(R.id.id_item_sharing_tv_summary);
            mIVPreview = itemView.findViewById(R.id.id_item_sharing_iv_preview);

        }

        public void bind(Plan plan) {
            String title = "Plan " + (getAdapterPosition()+1) + " summary : ";
            String duration = plan.getDuration()+" minutes";
            String summary = Handle.getHourFormat(plan.getArrivedTime()) + " - " + Handle.getHourFormat(plan.getArrivedTime()+plan.getDuration());


            mTVTitle.setText(title);
            mTVName.setText(plan.getName());
            mTVAddress.setText(plan.getAddress());
            mTVSummary.setText(summary);
            mTVTravelTime.setText(String.valueOf(0));
            mTVArrivedTime.setText(Handle.getHourFormat(plan.getArrivedTime()));
            mTVDuration.setText(duration);
            Glide.with(mContext)
                    .load(plan.getPreviewUrl())
                    .into(mIVPreview);
        }
    }

}
