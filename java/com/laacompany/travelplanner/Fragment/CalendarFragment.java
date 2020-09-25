package com.laacompany.travelplanner.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.laacompany.travelplanner.Adapter.CalendarPlanAdapter;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.PlanActivity;
import com.laacompany.travelplanner.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private CalendarView mCalendarView;
    private RecyclerView mRecyclerView;
    private ImageButton mIBTNAdd;
    private long mDatePick;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void initView(View v){
        mCalendarView = v.findViewById(R.id.calendarView);
        mRecyclerView = v.findViewById(R.id.id_fragment_calendar_rv_plan_list);
        mIBTNAdd = v.findViewById(R.id.id_fragment_calendar_btn_add);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container,false);

        initView(v);

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                mDatePick = eventDay.getCalendar().getTimeInMillis();
                CalendarPlanAdapter calendarPlanAdapter = new CalendarPlanAdapter(getActivity(), Handle.getCurrentDatePlanMasters(eventDay.getCalendar().getTime()));
                mRecyclerView.setAdapter(calendarPlanAdapter);
                mIBTNAdd.setVisibility(View.VISIBLE);
            }
        });

        mIBTNAdd.setVisibility(View.GONE);
        mIBTNAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PlanActivity.newIntentCalendar(getActivity(), mDatePick));
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("12345", "size : "  + Handle.sPlanMasters.size());

        List<EventDay> events = new ArrayList<>();
        for(PlanMaster planMaster : Handle.sPlanMasters) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(planMaster.getEventDate());
            events.add(new EventDay(calendar, R.drawable.ic_calender_mark));
        }
        mCalendarView.setEvents(events);


        try {
            Calendar now = Calendar.getInstance();
            mCalendarView.setDate(now);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        CalendarPlanAdapter calendarPlanAdapter = new CalendarPlanAdapter(getActivity(),new ArrayList<PlanMaster>());
        mRecyclerView.setAdapter(calendarPlanAdapter);

        mIBTNAdd.setVisibility(View.GONE);

    }
}
