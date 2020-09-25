package com.laacompany.travelplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.laacompany.travelplanner.Adapter.ExploreAdapter;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.PickerDialog.DialogDuration;
import com.laacompany.travelplanner.PickerDialog.FilterDialog;
import com.laacompany.travelplanner.PickerDialog.SortDialog;

import java.util.ArrayList;

public class ExploreActivity extends AppCompatActivity implements FilterDialog.FilterDialogListener, SortDialog.SortDialogListener {

    private static final String EXTRA_MODE = "mode_extra";
    private static final String EXTRA_LATITUDE = "mode_latitude";
    private static final String EXTRA_LONGITUDE = "mode_longitude";
    private RecyclerView mRecyclerView;
    ExploreAdapter exploreAdapter;
    ArrayList<Destination> candidateDest = new ArrayList<>();
    // MODE 0 View, 1 Select

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, ExploreActivity.class);
        intent.putExtra(EXTRA_MODE, 0);
        return intent;
    }

    public static Intent newIntentSelect(Context packageContext, double latitude, double longitude){
        Intent intent = new Intent(packageContext, ExploreActivity.class);
        intent.putExtra(EXTRA_MODE, 1);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        return intent;
    }

    public static Intent newIntentSelectOrigin(Context packageContext){
        Intent intent = new Intent(packageContext, ExploreActivity.class);
        intent.putExtra(EXTRA_MODE, 2);
        return intent;
    }

    private void initView(){
        mRecyclerView = findViewById(R.id.id_activity_explore_rv);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        getSupportActionBar().show();
        initView();

        int mode = getIntent().getIntExtra(EXTRA_MODE,-1);
        if (mode == 1){
            double originLatitude, originLongitude;
            originLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE,0);
            originLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE,0);

            for(Destination dest : Handle.sDestinations){
                if (Handle.range(new Pair<>(dest.getLatitude(), dest.getLongitude()), new Pair<>(originLatitude,originLongitude)) < 0.2){
                    candidateDest.add(dest);
                }
            }
        } else {
            candidateDest.addAll(Handle.sDestinations);
        }

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exploreAdapter = new ExploreAdapter(this, candidateDest, mode > 0);
        mRecyclerView.setAdapter(exploreAdapter);

        Button fabSort = findViewById(R.id.id_activity_explore_fbtn_sort);
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortDialog dialogSort = new SortDialog();
                dialogSort.show(getSupportFragmentManager(),"sort");
            }
        });


        Button fabFilter = findViewById(R.id.id_activity_explore_fbtn_filter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog dialogFilter = new FilterDialog();
                dialogFilter.show(getSupportFragmentManager(), "filter dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     //   menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                exploreAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public void applyFilter(String country, double rating, int visitor) {
        exploreAdapter.getCustomFilter(country,rating,visitor);
    }

    @Override
    public void applySort(String category, String sortBy) {
        exploreAdapter.getCustomSort(category, sortBy);
    }
}