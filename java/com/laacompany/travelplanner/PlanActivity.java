package com.laacompany.travelplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laacompany.travelplanner.Adapter.PlanAdapter;
import com.laacompany.travelplanner.Algorithm.TSP;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.Handle.VolleyHandle;
import com.laacompany.travelplanner.InterfaceAndCallback.OnStartDragListener;
import com.laacompany.travelplanner.InterfaceAndCallback.SimpleItemTouchHelperCallback;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.ModelClass.Plan;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.PickerDialog.DialogDuration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlanActivity extends AppCompatActivity  implements OnStartDragListener, DialogDuration.DurationDialogListener, VolleyHandle.VolleyResponseListener {

    private static final String EXTRA_MODE = "mode_extra";
    private static final String EXTRA_PLAN_ID = "plan_id_extra";
    private static final String EXTRA_DATE = "date_extra";
    private static final String EXTRA_DESTINATION_ID = "destination_id_extra";
    public static final int REQUEST_CODE_SEARCH = 1;
    public static final int REQUEST_CODE_SEARCH_ORIGIN = 2;
    public static final String EXTRA_RESULT_DESTINATION_ID = "result_destination_id_extra";

    private View mRootView;
    private EditText mEDTTitle;
    private Button mBTNDate, mBTNStartTime;
    private TextView mTVEndTime, mTVOriginName, mTVOriginAddress;
    private ImageButton mIBTNDelete, mIBTNShare;
    private ImageView mIVOriginPreview;
    private ProgressBar mPBLoading;


    private RecyclerView mRecyclerView;
    private PlanAdapter planAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private PlanMaster mPlanMaster;

    private int mode;
    public static double originLatitude, originLongitude;
    private boolean hasShown;

//    int debug = 0;

    public static Intent newIntentDestination(Context packageContext, String destinationId){
        Intent intent = new Intent(packageContext,PlanActivity.class);
        intent.putExtra(EXTRA_MODE, 0);
        intent.putExtra(EXTRA_DESTINATION_ID, destinationId);
        return intent;
    }

    public static Intent newIntentCalendar(Context packageContext, long date){
        Intent intent = new Intent(packageContext,PlanActivity.class);
        intent.putExtra(EXTRA_MODE,0);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext,PlanActivity.class);
        intent.putExtra(EXTRA_MODE, 0);
        return intent;
    }

    public static Intent newIntentView(Context packageContext, String planId){
        Intent intent = new Intent(packageContext,PlanActivity.class);
        intent.putExtra(EXTRA_MODE, 1);
        intent.putExtra(EXTRA_PLAN_ID, planId);
        return intent;
    }

    private void initView(){
        mRootView = findViewById(R.id.id_activity_plan_rl_root);
        mEDTTitle = findViewById(R.id.id_activity_plan_edt_title);
        mBTNDate = findViewById(R.id.id_activity_plan_btn_date);
        mBTNStartTime = findViewById(R.id.id_activity_plan_btn_start_time);
        mTVEndTime = findViewById(R.id.id_activity_plan_tv_end_time);
        mIBTNDelete = findViewById(R.id.id_activity_plan_ibtn_delete);
        mIBTNShare = findViewById(R.id.id_activity_plan_ibtn_share);
        mRecyclerView = findViewById(R.id.id_activity_plan_rv_plan_list);
        mIVOriginPreview = findViewById(R.id.id_activity_plan_iv_origin_preview);
        mTVOriginName = findViewById(R.id.id_activity_plan_tv_origin_name);
        mTVOriginAddress = findViewById(R.id.id_activity_plan_tv_origin_address);
        mPBLoading = findViewById(R.id.id_activity_plan_pb_loading);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initView();
        VolleyHandle.listener = this;
        originLatitude = originLongitude = -1;
        // MODE
        // 0 = NEW
        // 1 = UPDATE

        mode = getIntent().getIntExtra(EXTRA_MODE,-1);
        if(mode == 0){
            mPlanMaster = new PlanMaster();
            String destinationID;
            if(getIntent().hasExtra(EXTRA_DATE)){
                long time = getIntent().getLongExtra(EXTRA_DATE,0);
                Date date = new Date();
                date.setTime(time);
                String dateString = new SimpleDateFormat("EEEE, dd MMM YYYY").format(date);
                mPlanMaster.setEventDate(date);
                mBTNDate.setText(dateString);
            }

            mPlanMaster.setOrigin(null);
            String startTime = Handle.getHourFormat(0);
            mPlanMaster.setTimeStart(0);
            mBTNStartTime.setText(startTime);

            mPlanMaster.setPlans(new ArrayList<>());

            if(getIntent().hasExtra(EXTRA_DESTINATION_ID)){
                destinationID = getIntent().getStringExtra(EXTRA_DESTINATION_ID);
                Destination dest = Handle.getDestination(destinationID);
                mPlanMaster.getPlans().add(new Plan( dest.getDestinationId(),dest.getName(), dest.getAddress(), dest.getPreviewUrl(),0, 30));
            }

            mIBTNDelete.setVisibility(View.GONE);
            mIBTNShare.setVisibility(View.GONE);
        } else if (mode == 1){
            String plan_id = getIntent().getStringExtra(EXTRA_PLAN_ID);
            for(PlanMaster planMaster : Handle.sPlanMasters){
                if(planMaster.getPlanMasterId().equals(plan_id)){
                    Plan origin = new Plan(planMaster.getOrigin());
                    mPlanMaster = new PlanMaster();
                    mPlanMaster.setPlanMaster(planMaster);
                    ArrayList<Plan> plans = new ArrayList<>(mPlanMaster.getPlans());
                    mPlanMaster.setPlans(plans);
                    mPlanMaster.setOrigin(origin);
                    break;
                }
            }

            String date = new SimpleDateFormat("EEEE, dd MMM YYYY").format(mPlanMaster.getEventDate());
            String startTime = Handle.getHourFormat(mPlanMaster.getTimeStart());

            mEDTTitle.setText(mPlanMaster.getEventTitle());
            mBTNDate.setText(date);
            mBTNStartTime.setText(startTime);
            if (mPlanMaster.getOrigin() != null){
                Glide.with(this)
                        .load(mPlanMaster.getOrigin().getPreviewUrl())
                        .into(mIVOriginPreview);
                mTVOriginName.setText(mPlanMaster.getOrigin().getName());
                mTVOriginAddress.setText(mPlanMaster.getOrigin().getAddress());
                Destination origin = Handle.getDestination(mPlanMaster.getOrigin().getDestinationId());
                originLatitude = origin.getLatitude();
                originLongitude = origin.getLongitude();
            }

        }

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(this, mPlanMaster.getPlans(), this, mBTNStartTime, mTVEndTime);
        mRecyclerView.setAdapter(planAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(planAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        planAdapter.refreshData();
    }


    public void clickAdd(View view) {
        if (mPlanMaster.getOrigin() == null){
            Toast.makeText(this, "Start must be selected", Toast.LENGTH_SHORT).show();
            return;
        } else if (mPlanMaster.getPlans().size() >= 15) {
            Toast.makeText(this,"Each Plan can only hold 15 destinations", Toast.LENGTH_SHORT).show();
            return;
        }

        mPlanMaster.getPlans().add(new Plan("NEW", "New Destination", "", null, 0,30 ));
        planAdapter.setPlans(mPlanMaster.getPlans());
        planAdapter.refreshData();
    }

    public void clickDate(View view) {
        Calendar currentDate = Calendar.getInstance();;

        if(mPlanMaster.getEventDate() != null){
            currentDate.setTime(mPlanMaster.getEventDate());
        }

        int DD = currentDate.get(Calendar.DAY_OF_MONTH);
        int MM = currentDate.get(Calendar.MONTH);
        int YYYY = currentDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(PlanActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int YYYY, int MM, int DD) {
                        GregorianCalendar gc = new GregorianCalendar(YYYY,MM,DD);
                        Date date = new Date(gc.getTimeInMillis());
                        mPlanMaster.setEventDate(date);
                        mBTNDate.setText(Handle.getDateToString(mPlanMaster.getEventDate()));
                    }
                }, YYYY, MM, DD);
        datePickerDialog.show();
    }

    public void clickTime(View view) {

        int startTime = mPlanMaster.getTimeStart();
        int startHour = startTime/60;
        int startMinute = startTime%60;

        TimePickerDialog timePickerDialog = new TimePickerDialog(PlanActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int minutes = hourOfDay*60+minute;
                        mPlanMaster.setTimeStart(minutes);
                        mBTNStartTime.setText(Handle.getHourFormat(minutes));
                        planAdapter.refreshData();
                    }
        },startHour, startMinute, true);
        timePickerDialog.show();

    }


    public void clickComment(View view) {
        Intent intent = new Intent (this, ChatActivity.class);
        startActivity(intent);
    }


    public void clickSave(View view) {
        if (mPlanMaster.getOrigin() == null){
            Toast.makeText(this, "Start must be selected", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isAllDestinationSelected()) {
            Toast.makeText(this, "All destinations must be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean valid = validation();
        hasShown = false;
        if(valid) {
            mPBLoading.setVisibility(View.VISIBLE);
            if (mode == 0){
                Toast.makeText(PlanActivity.this, "Adding Plan, please wait...", Toast.LENGTH_SHORT).show();
                Handle.sPlanMasters.add(mPlanMaster);
                VolleyHandle.addPlanMaster(Handle.sPlanMasters.size()-1);
            } else {
                Toast.makeText(PlanActivity.this, "Saving plan, please wait...", Toast.LENGTH_SHORT).show();
                for(int i = 0; i < Handle.sPlanMasters.size(); i++){
                    if(Handle.sPlanMasters.get(i).getPlanMasterId().equals(mPlanMaster.getPlanMasterId())){
                        Handle.sPlanMasters.get(i).setPlanMaster(mPlanMaster);
                        Plan origin = mPlanMaster.getOrigin();
                        Handle.sPlanMasters.get(i).setOrigin(origin);
                        VolleyHandle.updatePlanMaster(i);
                        break;
                    }
                }
            }
        }
    }

    public void clickSimulate(View view) {
        if (mPlanMaster.getOrigin() == null){
            Toast.makeText(this, "Start must be selected", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isAllDestinationSelected()) {
            Toast.makeText(this, "All destinations must be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPlanMaster.getPlans().size() > 0){
            Handle.sCurrentRoutes.clear();
            Handle.sCurrrenRouteDestinations.clear();
            Destination destination = Handle.getDestination(mPlanMaster.getOrigin().getDestinationId());
            Handle.sCurrrenRouteDestinations.add(mPlanMaster.getOrigin().getName());
            Handle.sCurrentRoutes.add(Pair.create(destination.getLongitude(),destination.getLatitude()));
            for(Plan plan : mPlanMaster.getPlans()){
                destination = Handle.getDestination(plan.getDestinationId());
                Handle.sCurrrenRouteDestinations.add(destination.getName());
                Handle.sCurrentRoutes.add(Pair.create(destination.getLongitude(),destination.getLatitude()));
            }
            startActivity(MapsActivity.newIntent(this));
        } else {
            Toast.makeText(this, "Require at least one destination", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickOptimize(View view) {
        if (mPlanMaster.getOrigin() == null){
            Toast.makeText(this, "Start must be selected", Toast.LENGTH_SHORT).show();
            return;
        }else if (!isAllDestinationSelected()) {
            Toast.makeText(this, "All destinations must be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPlanMaster.getPlans().size() > 0){
            ArrayList<Pair<Double,Double>> latLng = new ArrayList<>();
            Destination destination = Handle.getDestination(mPlanMaster.getOrigin().getDestinationId());
            latLng.add(new Pair<>(destination.getLatitude(),destination.getLongitude()));
            for(Plan plan : mPlanMaster.getPlans()){
                destination = Handle.getDestination(plan.getDestinationId());
                latLng.add(new Pair<>(destination.getLatitude(),destination.getLongitude()));
            }

            double[][] dist = new double[latLng.size()][latLng.size()];
            for(int i = 0; i < latLng.size(); i++){
                for(int j = 0; j < latLng.size(); j++){
                    dist[i][j] = Handle.range(latLng.get(i),latLng.get(j));
                }
            }

            ArrayList<Integer> indexes = TSP.simulate(dist, latLng.size());
            ArrayList<Plan> plans = new ArrayList<>();
            for(int i = 1; i < indexes.size(); i++){
                int index = indexes.get(i);
                plans.add(mPlanMaster.getPlans().get(index-1));
            }
            mPlanMaster.setPlans(plans);
            planAdapter.setPlans(plans);
            planAdapter.refreshData();
            Toast.makeText(this, "Optimize Plan Complete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Require at least one destination", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickDeleteMasterPlan(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete Plan");
        alertDialog.setMessage("Are you sure you want to delete this plan?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < Handle.sPlanMasters.size(); i++){
                            if(Handle.sPlanMasters.get(i).getPlanMasterId().equals(mPlanMaster.getPlanMasterId())){
                                Handle.sPlanMasters.remove(i);
                                break;
                            }
                        }
                        Toast.makeText(PlanActivity.this, "Deleting plan, please wait...", Toast.LENGTH_SHORT).show();
                        mPBLoading.setVisibility(View.VISIBLE);
                        VolleyHandle.updateUser();
                    }
                });
        alertDialog.show();

    }

    public void clickShareMasterPlan(View view) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        StringBuilder hash_code = new StringBuilder();
        for(char c : mPlanMaster.getPlanMasterId().toCharArray()){
            hash_code.append(c);
            hash_code.append((char)(Math.random() * 26 + 'A'));
        }
        String shareBody = "https://www.aturinaja.com/addplan/"+hash_code.toString();
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    public void clickSearchOrigin(View view) {
        startActivityForResult(ExploreActivity.newIntentSelectOrigin(this), PlanActivity.REQUEST_CODE_SEARCH_ORIGIN);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void applyTime(int minutes) {
        mPlanMaster.getPlans().get(PlanAdapter.selectedPos).setDuration(minutes);
        planAdapter.refreshData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SEARCH){
            if(resultCode == Activity.RESULT_OK){
                String destinationId = data.getStringExtra(EXTRA_RESULT_DESTINATION_ID);
                Destination destination = Handle.getDestination(destinationId);

                mPlanMaster.getPlans().get(PlanAdapter.selectedPos).setDestinationId(destinationId);
                mPlanMaster.getPlans().get(PlanAdapter.selectedPos).setName(destination.getName());
                mPlanMaster.getPlans().get(PlanAdapter.selectedPos).setAddress(destination.getAddress());
                mPlanMaster.getPlans().get(PlanAdapter.selectedPos).setPreviewUrl(destination.getPreviewUrl());
                planAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_CODE_SEARCH_ORIGIN){
            if (resultCode == RESULT_OK){
                String destinationId = data.getStringExtra(EXTRA_RESULT_DESTINATION_ID);
                Destination origin = Handle.getDestination(destinationId);
                originLatitude = origin.getLatitude();
                originLongitude = origin.getLongitude();
                mPlanMaster.setOrigin(new Plan(origin.getDestinationId(),origin.getName(),origin.getAddress(),origin.getPreviewUrl(),0,0));
                Glide.with(this)
                        .load(origin.getPreviewUrl())
                        .into(mIVOriginPreview);
                mTVOriginName.setText(origin.getName());
                mTVOriginAddress.setText(origin.getAddress());
            }
        }
    }

    @Override
    public void onResponse(String functionResp) {
        mPBLoading.setVisibility(View.GONE);
        if (mode == 0){
            if (functionResp.equals("updateUser")){
                finish();
            }
        } else {
            if (functionResp.equals("updatePlanMaster") || functionResp.equals("updateUser")){
                finish();
            }
        }
    }

    @Override
    public void onError(String functionResp) {
        if (!hasShown){
            Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            hasShown = true;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle((mode == 0)? "Discard Plan" : "Cancel Plan Update");
        alertDialog.setMessage((mode == 0)? "Are you sure you want to discard this plan?" : "Are you sure you want to cancel plan update?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    private boolean validation(){

        String title = mEDTTitle.getText().toString();
        String date = mBTNDate.getText().toString();

        boolean valid = true;

        if (title.isEmpty()){
            mEDTTitle.setError("Event name should not be empty");
            valid = false;
        } else {
            mPlanMaster.setEventTitle(title);
        }

        if (date.equals("Select Date")){
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private boolean isAllDestinationSelected(){
        for(Plan plan : mPlanMaster.getPlans()){
            if (plan.getDestinationId().equals("NEW")) return false;
        }
        return true;
    }

    public void disableClicks() {
        // Get all touchable views
        ArrayList<View> layoutButtons = mRootView.getTouchables();
        for(View v : layoutButtons){
            v.setVisibility(View.GONE);
        }
    }

    public void enableClicks() {
        // Get all touchable views
        ArrayList<View> layoutButtons = mRootView.getTouchables();
        for(View v : layoutButtons){
            v.setVisibility(View.VISIBLE);
        }
    }


}
