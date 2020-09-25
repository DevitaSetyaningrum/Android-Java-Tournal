package com.laacompany.travelplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaDescrambler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.PickerDialog.DialogDuration;

public class DestinationDetailActivity extends AppCompatActivity implements DialogDuration.DurationDialogListener {

    private static final String EXTRA_DESTINATION_ID = "destination_id_extra";
    private static final String EXTRA_MODE = "mode_extra";

    private TextView mTVName, mTVRating, mTVAddress, mTVCountry, mTVVisitorDaily, mTVVisitorTotal, mTVOpenTime, mTVBestTime;
    private ImageView mIVPreview, mIVFlag;
    private Button mBTNStartPlan;

    private Destination mDestination;

    public static Intent newIntent(Context packageContext, String destinationId){
        Intent intent = new Intent(packageContext, DestinationDetailActivity.class);
        intent.putExtra(EXTRA_MODE, 0);
        intent.putExtra(EXTRA_DESTINATION_ID, destinationId);
        return intent;
    }
    public static Intent newIntentView(Context packageContext, String destinationId){
        Intent intent = new Intent(packageContext, DestinationDetailActivity.class);
        intent.putExtra(EXTRA_MODE, 1);
        intent.putExtra(EXTRA_DESTINATION_ID, destinationId);
        return intent;
    }

    private void initView(){
        mTVName = findViewById(R.id.id_activity_detaildes_tv_name);
        mTVRating = findViewById(R.id.id_activity_detaildes_tv_rating);
        mTVAddress = findViewById(R.id.id_activity_detaildes_tv_address);
        mTVCountry = findViewById(R.id.id_activity_detaildes_tv_country);
        mTVVisitorDaily = findViewById(R.id.id_activity_detaildes_tv_visitor_daily);
        mTVVisitorTotal = findViewById(R.id.id_activity_detaildes_visitor_total);
        mTVOpenTime = findViewById(R.id.id_activity_detaildes_tv_open_time);
        mTVBestTime = findViewById(R.id.id_activity_detaildes_tv_best_time);
        mIVPreview = findViewById(R.id.id_activity_detaildes_iv_preview_url);
        mIVFlag = findViewById(R.id.id_activity_detaildes_iv_flag_url);
        mBTNStartPlan = findViewById(R.id.id_activity_detaildes_btn_start_plan);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_detail);

        initView();

        String destId = getIntent().getStringExtra(EXTRA_DESTINATION_ID);
        int mode = getIntent().getIntExtra(EXTRA_MODE,-1);
        mDestination = Handle.getDestination(destId);

        String rating = String.format("%.1f / 5.0 ", mDestination.getRating());
        String openTime = Handle.getHourFormat(mDestination.getOpenTime()) + " - " + Handle.getHourFormat(mDestination.getCloseTime());
        String bestTime = Handle.getHourFormat(mDestination.getBestTimeStart()) + " - " + Handle.getHourFormat(mDestination.getBestTimeEnd());

        mTVName.setText(mDestination.getName());
        mTVRating.setText(rating);
        mTVAddress.setText(mDestination.getAddress());
        mTVCountry.setText(mDestination.getCountry());
        mTVVisitorDaily.setText(String.valueOf(mDestination.getVisitorEachDay()));
        mTVVisitorTotal.setText(String.valueOf(mDestination.getTotalVisitor()));
        mTVOpenTime.setText(openTime);
        mTVBestTime.setText(bestTime);

        Glide.with(this)
                .load(mDestination.getPreviewUrl())
                .into(mIVPreview);

        Glide.with(this)
                .load(mDestination.getFlagUrl())
                .into(mIVFlag);

        if (mode == 1){
            mBTNStartPlan.setVisibility(View.GONE);
        }
    }


    public void clickPlan(View view) {
        startActivity(PlanActivity.newIntentDestination(this, mDestination.getDestinationId()));
    }

    @Override
    public void applyTime(int minutes) {
        Toast.makeText(this,minutes+"",Toast.LENGTH_SHORT).show();
    }

    public void clickClose(View view) {
        finish();
    }
}
