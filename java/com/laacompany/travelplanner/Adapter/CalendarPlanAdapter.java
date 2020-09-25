package com.laacompany.travelplanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.PlanActivity;
import com.laacompany.travelplanner.R;

import java.util.ArrayList;

public class CalendarPlanAdapter extends RecyclerView.Adapter<CalendarPlanAdapter.CalendarPlanViewHolder> {


    private Context mContext;
    private ArrayList<PlanMaster> mPlanMasters;

    public CalendarPlanAdapter(Context context, ArrayList<PlanMaster> planMasters){
        mContext = context;
        mPlanMasters = planMasters;
    }

    @NonNull
    @Override
    public CalendarPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CalendarPlanViewHolder(LayoutInflater.from(mContext),parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarPlanViewHolder holder, int position) {
        holder.bind(mPlanMasters.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlanMasters.size();
    }

    public void setPlanMasters(ArrayList<PlanMaster> planMasters){
        mPlanMasters = planMasters;
    }


    public class CalendarPlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTVTitle, mTVStartTime, mTVTotalDes;
        private PlanMaster mPlanMaster;

        public CalendarPlanViewHolder(LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_calendar_plan_master, parent, false));

            mTVTitle = itemView.findViewById(R.id.id_item_calendar_plan_master_tv_title);
            mTVStartTime = itemView.findViewById(R.id.id_item_calendar_plan_master_tv_start_time);
            mTVTotalDes = itemView.findViewById(R.id.id_item_calendar_plan_master_tv_total_destination);

            itemView.setOnClickListener(this);

        }

        public void bind(PlanMaster planMaster){
            mPlanMaster = planMaster;
            String startTime = Handle.getHourFormat(planMaster.getTimeStart());

            mTVTitle.setText(planMaster.getEventTitle());
            mTVStartTime.setText(startTime);
            mTVTotalDes.setText(String.valueOf(mPlanMaster.getPlans().size()));
        }

        @Override
        public void onClick(View v) {
            mContext.startActivity(PlanActivity.newIntentView(mContext, mPlanMaster.getPlanMasterId()));
        }
    }

}
