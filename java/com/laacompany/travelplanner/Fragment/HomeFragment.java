package com.laacompany.travelplanner.Fragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.MainActivity;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.PlanActivity;
import com.laacompany.travelplanner.R;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView mTVNextTitle, mTVNextDate, mTVNextTime, mTVNextDestinations, mTVFuturePlan, mTVTotalPlan;
    private LinearLayout mLLAgenda;
    private PlanMaster nextAgenda = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void initView(View v){
        mTVNextTitle = v.findViewById(R.id.id_fragment_home_tv_title);
        mTVNextDate = v.findViewById(R.id.id_fragment_home_tv_date);
        mTVNextTime = v.findViewById(R.id.id_fragment_home_tv_time_start);
        mTVNextDestinations = v.findViewById(R.id.id_fragment_home_tv_total_destinations);
        mTVFuturePlan = v.findViewById(R.id.id_fragment_home_tv_future_plans);
        mTVTotalPlan = v.findViewById(R.id.id_fragment_home_tv_total_plans);
        mLLAgenda = v.findViewById(R.id.id_fragment_home_ll_agenda);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container,false);

        initView(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        int futurecount = 0;
        Calendar curTime = Calendar.getInstance();

        for(PlanMaster planMaster : Handle.sPlanMasters){
            Calendar planTime = Calendar.getInstance();
            planTime.setTime(planMaster.getEventDate());
            planTime.set(Calendar.HOUR, planMaster.getTimeStart()/60);
            planTime.set(Calendar.MINUTE, planMaster.getTimeStart()%60);

            Log.d("12345", planTime.compareTo(curTime) + "");

            if (planTime.compareTo(curTime) > 0){
                if (nextAgenda == null || nextAgenda.getEventDate().getTime() > planMaster.getEventDate().getTime()) {
                    nextAgenda = planMaster;
                }
                futurecount++;
            }
        }
        mTVFuturePlan.setText(String.valueOf(futurecount));
        mTVTotalPlan.setText(String.valueOf(Handle.sPlanMasters.size()));

        if (nextAgenda == null) mLLAgenda.setVisibility(View.GONE);
        else {
            mLLAgenda.setVisibility(View.VISIBLE);
            mTVNextTitle.setText(nextAgenda.getEventTitle());
            mTVNextDate.setText(Handle.getDateToString(nextAgenda.getEventDate()));
            mTVNextTime.setText(Handle.getHourFormat(nextAgenda.getTimeStart()));
            mTVNextDestinations.setText(String.valueOf(nextAgenda.getPlans().size()));
        }
        mLLAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PlanActivity.newIntentView(getActivity(), nextAgenda.getPlanMasterId()));
            }
        });

    }


}
