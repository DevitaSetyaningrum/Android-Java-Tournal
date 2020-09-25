package com.laacompany.travelplanner;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.laacompany.travelplanner.Handle.Handle;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfMeasurement;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * Make a directions request with the Mapbox Directions API and then draw a line behind a moving
 * SymbolLayer icon which moves along the Directions response route.
 */
public class MapsActivity extends AppCompatActivity implements PermissionsListener {

    private static final String DOT_SOURCE_ID = "dot-source-id";
    private static final String LINE_SOURCE_ID = "line-source-id";

    private MapView mMapView;
    private Button mBTNSimulate, mBTNNavigation;
    private MapboxMap mMapboxMap;

    private GeoJsonSource pointSource;
    private GeoJsonSource lineSource;
    private List<Point> routeCoordinateList;
    private List<Point> markerLinePointList = new ArrayList<>();
    private int routeIndex;
    private ArrayList<Point> destinationPoints = new ArrayList<>();
    private ArrayList<DirectionsRoute> navigationRoutes = new ArrayList<>();
    private int currentIndex = 0, navigationIndex = 0;
    private boolean isExit = false;
    private Animator currentAnimator;

    private LocationComponent mLocationComponent;
    private PermissionsManager mPermissionsManager;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MapsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_maps);



        mMapView = findViewById(R.id.id_activity_maps_map_view);
        mBTNSimulate = findViewById(R.id.id_activity_maps_btn_start_sim);
        mBTNNavigation = findViewById(R.id.id_activity_maps_btn_start_nav);
        mBTNNavigation.setEnabled(false);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MapsActivity.this.mMapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });

            }
        });
    }


    /**
     * Add data to the map once the GeoJSON has been loaded
     *
     * @param featureCollection returned GeoJSON FeatureCollection from the Directions API route request
     */
    private void initData(Style fullyLoadedStyle, @NonNull FeatureCollection featureCollection) {
        if (featureCollection.features() != null) {
            LineString lineString = ((LineString) featureCollection.features().get(0).geometry());
            if (lineString != null) {
                routeCoordinateList = lineString.coordinates();
                initSources(fullyLoadedStyle, featureCollection);
                initSymbolLayer(fullyLoadedStyle);
                initDotLinePath(fullyLoadedStyle);
                animate();
            }
        }
    }

    /**
     * Set up the repeat logic for moving the icon along the route.
     */
    private void animate() {
        // Check if we are at the end of the points list
        if ((routeCoordinateList.size() - 1 > routeIndex)) {
            Point indexPoint = routeCoordinateList.get(routeIndex);
            Point newPoint = Point.fromLngLat(indexPoint.longitude(), indexPoint.latitude());
            currentAnimator = createLatLngAnimator(indexPoint, newPoint);
            currentAnimator.start();
            routeIndex++;
        } else {

            currentIndex++;
            int idx = currentIndex %  Handle.sCurrrenRouteDestinations.size();
            if (currentIndex == destinationPoints.size()){
                mMapboxMap.getStyle().removeLayer("symbol-layer-id");
                mBTNSimulate.setEnabled(true);
                mBTNNavigation.setEnabled(true);
                Toast.makeText(this, "Arrived at : " + Handle.sCurrrenRouteDestinations.get(idx) + "\nSimulation Complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Arrived at : " + Handle.sCurrrenRouteDestinations.get(idx), Toast.LENGTH_SHORT).show();
                addDestinationIconLayoutStart(mMapboxMap.getStyle(), currentIndex);
                int nextCurrentIndex = (currentIndex+1) % destinationPoints.size();
                getRoute(destinationPoints.get(currentIndex),destinationPoints.get(nextCurrentIndex));
                routeIndex = 0;
            }
        }
    }

    private void addDestinationIconLayoutStart(Style style, int id) {
        if(isExit) return;
        if(style.getImage("destination-icon-id-"+id) != null) return;
        style.addImage("destination-icon-id-"+id, BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));

        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id-"+id);
        style.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id-"+id, "destination-source-id-"+id);
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id-"+id), iconAllowOverlap(true), iconIgnorePlacement(true));

        style.addLayer(destinationSymbolLayer);

        GeoJsonSource source = mMapboxMap.getStyle().getSourceAs("destination-source-id-"+id);

        if (source != null){
            source.setGeoJson(Feature.fromGeometry(destinationPoints.get(id)));
        }

    }

    public void clickStartSimulate(View view) {

        destinationPoints.clear();
//        destinationPoints.add(Point.fromLngLat(mLocationComponent.getLastKnownLocation().getLongitude(), mLocationComponent.getLastKnownLocation().getLatitude()));
        for(int i = 0; i < Handle.sCurrentRoutes.size(); i++){
            destinationPoints.add(Point.fromLngLat(Handle.sCurrentRoutes.get(i).first, Handle.sCurrentRoutes.get(i).second));
        }

        markerLinePointList.clear();
        routeIndex = 0;
        navigationIndex = 0;

        mBTNSimulate.setEnabled(false);
        mBTNNavigation.setEnabled(false);
        if(currentIndex != 0){
            currentIndex = 0;
            mMapboxMap.getStyle().removeLayer("line-layer-id");
            for(int i = 0; i < destinationPoints.size(); i++){
                mMapboxMap.getStyle().removeImage("destination-icon-id-"+i);
                mMapboxMap.getStyle().removeLayer("destination-symbol-layer-id-"+i);
                mMapboxMap.getStyle().removeSource("destination-source-id-"+i);
            }
        }

        getRoute(destinationPoints.get(0),destinationPoints.get(1));
        addDestinationIconLayoutStart(mMapboxMap.getStyle(),0);
    }

    public void clickStartNavigation(View view) {
        boolean simulateRoute = true;
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(navigationRoutes.get(navigationIndex))
                .shouldSimulateRoute(simulateRoute)
                .build();

        NavigationLauncher.startNavigation(this, options);
        navigationIndex = (navigationIndex+1) % navigationRoutes.size();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocationComponent(mMapboxMap.getStyle());
        } else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void enableLocationComponent(@NotNull Style loadedMapStyle) {
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            mLocationComponent = mMapboxMap.getLocationComponent();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                return;
            }
            mLocationComponent.activateLocationComponent(this, loadedMapStyle);
            mLocationComponent.setLocationComponentEnabled(true);

            mLocationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            mPermissionsManager = new PermissionsManager(this);
            mPermissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private static class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            return Point.fromLngLat(
                    startValue.longitude() + ((endValue.longitude() - startValue.longitude()) * fraction),
                    startValue.latitude() + ((endValue.latitude() - startValue.latitude()) * fraction)
            );
        }
    }

    private Animator createLatLngAnimator(Point currentPosition, Point targetPosition) {
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new PointEvaluator(), currentPosition, targetPosition);
        latLngAnimator.setDuration((long) TurfMeasurement.distance(currentPosition, targetPosition, "meters"));
        latLngAnimator.setInterpolator(new LinearInterpolator());
        latLngAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animate();
            }
        });
        latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                pointSource.setGeoJson(point);
                markerLinePointList.add(point);
                lineSource.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(markerLinePointList)));
//                Log.d("123", "marker");
            }
        });

        return latLngAnimator;
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     *
     * @param origin      the starting point of the route
     * @param destination the desired finish point of the route
     */
    private void getRoute(Point origin, Point destination) {
        getNavigationRoute(origin,destination);

        MapboxDirections client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

                // You can get the generic HTTP info about the response
                Timber.d("Response code: %s", response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                DirectionsRoute currentRoute = response.body().routes().get(0);
                mMapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        LatLngBounds.Builder bounds =  new LatLngBounds.Builder();

                        for(int i = 0; i <= Math.min(currentIndex+1,destinationPoints.size()-1); i++){
                            bounds.include(new LatLng(destinationPoints.get(i).latitude(),destinationPoints.get(i).longitude()));
                        }

                        mMapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150), 2000);
                        initData(style,FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6))));

                    }
                });
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: %s", throwable.getMessage());
                Toast.makeText(MapsActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNavigationRoute(Point origin, Point destination){
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(isExit) return;
                        if(response.body() != null && response.body().routes().size() > 0){
                            if(navigationRoutes.size() < destinationPoints.size()){
                                navigationRoutes.add(response.body().routes().get(0));
                                Log.d("123", response.body().routes().get(0).toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    /**
     * Add various sources to the map.
     */
    private void initSources(@NonNull Style loadedMapStyle, @NonNull FeatureCollection featureCollection) {
        if (loadedMapStyle.getSource(DOT_SOURCE_ID) != null) return;
        loadedMapStyle.addSource(pointSource = new GeoJsonSource(DOT_SOURCE_ID, featureCollection));
        loadedMapStyle.addSource(lineSource = new GeoJsonSource(LINE_SOURCE_ID));
    }

    /**
     * Add the marker icon SymbolLayer.
     */
    //GAMBAR ICON YANG JALAN
    private void initSymbolLayer(@NonNull Style loadedMapStyle) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_car, null);
        loadedMapStyle.addImage("moving-red-marker", BitmapUtils.getBitmapFromDrawable(drawable));

        if(loadedMapStyle.getLayer("symbol-layer-id") != null) return;
        loadedMapStyle.addLayer(new SymbolLayer("symbol-layer-id", DOT_SOURCE_ID).withProperties(
                iconImage("moving-red-marker"),
                iconSize(1f),
                iconOffset(new Float[] {5f, 0f}),
                iconIgnorePlacement(true),
                iconAllowOverlap(true)
        ));

    }

    /**
     * Add the LineLayer for the marker icon's travel route. Adding it under the "road-label" layer, so that the
     * this LineLayer doesn't block the street name.
     */

    //GARIS JALAN
    private void initDotLinePath(@NonNull Style loadedMapStyle) {
        if(loadedMapStyle.getLayer("line-layer-id") != null) return;
        loadedMapStyle.addLayerBelow(new LineLayer("line-layer-id", LINE_SOURCE_ID).withProperties(
                lineColor(Color.parseColor("#03A9F4")),
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(4f)), "road-label");
    }

    @Override
    public void onBackPressed() {
        isExit = true;
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}