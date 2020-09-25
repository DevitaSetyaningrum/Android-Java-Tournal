package com.laacompany.travelplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.Handle.VolleyHandle;

public class SplashActivity extends AppCompatActivity implements VolleyHandle.VolleyResponseListener {

    private boolean isDestinationLoad, isUserLoad, isPlanMastersLoad, hasShown;
    private int countError = 0, countTime = 0;

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext,SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isDestinationLoad = isUserLoad = isPlanMastersLoad = hasShown = false;

        Handle.init(this);
        VolleyHandle.listener = this;
//        Handle.mAuth.signOut();

        VolleyHandle.getAllDestinations();
        if (Handle.mAuth.getCurrentUser() != null){
            VolleyHandle.getCurrentUser(Handle.mAuth.getCurrentUser().getUid(), true);
        }

        new Handler().postDelayed(runnable,0);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countTime++;
            if (Handle.mAuth.getCurrentUser() != null){
                if (isDestinationLoad && isUserLoad && isPlanMastersLoad){
                    startActivity(MainActivity.newIntent(SplashActivity.this));
                    finish();
                } else {
                    new Handler().postDelayed(runnable,200);
                }
            } else {
                if (isDestinationLoad){
                    startActivity(MainActivity.newIntent(SplashActivity.this));
                    finish();
                } else {
                    new Handler().postDelayed(runnable,200);
                }
            }
            if (countError > 6 || countTime > 150) finish();
        }
    };

    @Override
    public void onResponse(String functionResp) {
        if (functionResp.equals("getAllDestinations")){
            isDestinationLoad = true;
        } else if (functionResp.equals("getCurrentUser")){
            isUserLoad = true;
        } else if (functionResp.equals("getAllPlanMasters")){
            isPlanMastersLoad = true;
        }
    }

    @Override
    public void onError(String functionResp) {
        countError++;
        if (countError > 3) {
            Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            hasShown = true;
        }
        if (!hasShown){
            if (functionResp.equals("getCurrentUserNotFound")){
                if (Handle.mAuth.getCurrentUser() != null){
                    Handle.mAuth.signOut();
                }
            } else {
                if (functionResp.equals("getAllDestinations")){
                    VolleyHandle.getAllDestinations();
                } else if (functionResp.equals("getCurrentUser")){
                    VolleyHandle.getCurrentUser(Handle.mAuth.getCurrentUser().getUid(), false);
                } else if (functionResp.equals("getAllPlanMasters")){
                    VolleyHandle.getAllPlanMasters(Handle.mAuth.getCurrentUser().getUid());
                }
            }
        }
    }
}
