package com.laacompany.travelplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laacompany.travelplanner.Adapter.SharingAdapter;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.Handle.VolleyHandle;
import com.laacompany.travelplanner.ModelClass.PlanMaster;

import java.util.List;

public class SharingActivity extends AppCompatActivity implements VolleyHandle.VolleyResponseListener {

    private ProgressBar mPBLoading;
    private LinearLayout mLLMain, mLLMessage;
    private ImageView mIVOriginPreview;
    private TextView mTVOriginName, mTVOriginAddress, mTVTitle, mTVDate, mTVStartTime;
    private Button mBTNAdd, mBTNCancel;
    private RecyclerView mRecyclerView;
    private String id="";
    private int failedAttempts = 0;


    void initView(){
        mBTNAdd = findViewById(R.id.id_activity_sharing_btn_add);
        mBTNCancel = findViewById(R.id.id_activity_sharing_btn_cancel);
        mLLMain = findViewById(R.id.id_activity_sharing_layout_main);
        mLLMessage = findViewById(R.id.id_activity_sharing_ll_message);
        mPBLoading = findViewById(R.id.id_activity_sharing_pb_loading);
        mRecyclerView = findViewById(R.id.id_activity_sharing_rv);
        mIVOriginPreview = findViewById(R.id.id_activity_sharing_iv_origin_preview);
        mTVOriginName = findViewById(R.id.id_activity_sharing_tv_origin_name);
        mTVOriginAddress = findViewById(R.id.id_activity_sharing_tv_origin_address);
        mTVTitle = findViewById(R.id.id_activiy_sharing_tv_title);
        mTVDate = findViewById(R.id.id_activiy_sharing_tv_date);
        mTVStartTime = findViewById(R.id.id_activiy_sharing_tv_start_time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        Handle.init(this);
        initView();


        Uri uri = getIntent().getData();
        if (uri == null) return;

        mLLMain.setVisibility(View.GONE);
        if (Handle.mAuth.getCurrentUser() == null){
            mPBLoading.setVisibility(View.GONE);
            return;
        } else {
            mLLMessage.setVisibility(View.GONE);
        }

        VolleyHandle.listener = this;

        List<String> params = uri.getPathSegments();
        char[] tempid = params.get(params.size()-1).toCharArray();
        for(int i = 0; i < tempid.length/2; i++){
            id+=tempid[i*2];
        }
        VolleyHandle.getCurrentUser(Handle.mAuth.getCurrentUser().getUid(), true);
        failedAttempts = 0;
    }


    @Override
    public void onResponse(String functionResp) {
        if(functionResp.equals("getCurrentUser")){
            for(String pmId : VolleyHandle.sPlanMasterIds){
                if (pmId.equals(id)){
                    mBTNAdd.setText("Launch");
                    break;
                }
            }
            VolleyHandle.getPlanMaster(id);
            failedAttempts = 0;
        } else if (functionResp.equals("getPlanMaster")){

            mLLMain.setVisibility(View.VISIBLE);
            mPBLoading.setVisibility(View.GONE);

            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            PlanMaster planMaster = VolleyHandle.curQueryPlanMaster;

            String time = "00:00 - 00:00";
            if (planMaster.getPlans().size() > 0){
                int endminute = planMaster.getPlans().get(planMaster.getPlans().size()-1).getArrivedTime() + planMaster.getPlans().get(planMaster.getPlans().size()-1).getDuration();
                time = Handle.getHourFormat(planMaster.getTimeStart()) + " - " + Handle.getHourFormat(endminute);
            }

            mTVTitle.setText(planMaster.getEventTitle());
            mTVDate.setText(Handle.getDateToString(planMaster.getEventDate()));
            mTVStartTime.setText(time);
            mTVOriginName.setText(planMaster.getOrigin().getName());
            mTVOriginAddress.setText(planMaster.getOrigin().getAddress());
            Glide.with(this).
                    load(planMaster.getOrigin().getPreviewUrl()).
                    into(mIVOriginPreview);
            SharingAdapter sharingAdapter = new SharingAdapter(this, planMaster.getPlans());
            mRecyclerView.setAdapter(sharingAdapter);

            if (mBTNAdd.getText().equals("Launch")){
                Toast.makeText(SharingActivity.this, "You have this plan already", Toast.LENGTH_SHORT).show();
            }

        } else if (functionResp.equals("updateUserPlanMaster")){
            startActivity(SplashActivity.newIntent(this));
            finish();
        }

    }

    @Override
    public void onError(String functionResp) {
        failedAttempts++;
        if (failedAttempts > 5){
            Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (functionResp.equals("getCurrentUser")){
            VolleyHandle.getCurrentUser(Handle.mAuth.getCurrentUser().getUid(), true);
        } else if (functionResp.equals("getPlanMaster")){
            VolleyHandle.getPlanMaster(id);
        } else if (functionResp.equals("updateUserPlanMaster")){
            VolleyHandle.updateUserPlanMaster();
        }
    }

    public void clickAdd(View view) {
        if (mBTNAdd.getText().equals("Launch")){
            startActivity(SplashActivity.newIntent(this));
            finish();
        } else {
            VolleyHandle.sPlanMasterIds.add(id);
            VolleyHandle.updateUserPlanMaster();
            failedAttempts = 0;
        }
    }

    public void clickCancel(View view) {
        finish();
    }

    public void clickLaunch(View view) {
        startActivity(SplashActivity.newIntent(this));
        finish();
    }
}
