package com.laacompany.travelplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.laacompany.travelplanner.Fragment.CalendarFragment;
import com.laacompany.travelplanner.Fragment.HomeFragment;
import com.laacompany.travelplanner.Fragment.SettingFragment;
import com.laacompany.travelplanner.Handle.Handle;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainActivity extends AppCompatActivity {

    private SpaceNavigationView mSpaceNavigationView;
    private boolean mayClick=true;

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, MainActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private void initView(){
        mSpaceNavigationView = findViewById(R.id.id_main_nav_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Handle.isExit) finish();
        else if(Handle.mAuth.getCurrentUser() == null){
            startActivity(LoginActivity.newIntent(this));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //NAVIGATION BAR
        mSpaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        mSpaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        mSpaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_map_black_24dp));
        mSpaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_perm_contact_calendar_black_24dp));
        mSpaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_settings_black_24dp));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        mSpaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                if(!mayClick) return;
                mayClick=false;
                startActivity(PlanActivity.newIntent(MainActivity.this));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                getSupportActionBar().hide();
                switch (itemIndex){
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                        break;
                    case 1:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExploreFragment()).commit();
                        startActivity(ExploreActivity.newIntent(MainActivity.this));
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingFragment()).commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                        break;
                    case 1:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExploreFragment()).commit();
                        startActivity(ExploreActivity.newIntent(MainActivity.this));
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingFragment()).commit();
                        break;
                }
            }
        });

        //NAVIGATION BAR
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpaceNavigationView.changeCurrentItem(0);
        mayClick=true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Exit Application");
        alertDialog.setMessage("Are you sure you want to quit?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Quit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}
