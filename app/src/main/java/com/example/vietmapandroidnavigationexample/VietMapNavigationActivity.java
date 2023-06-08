package com.example.vietmapandroidnavigationexample;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.engine.LocationEngine;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationPresenter;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.location.engine.LocationEngineProvider;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VietMapNavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback, ProgressChangeListener, NavigationListener, Callback<DirectionsResponse>, OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMapLongClickListener, MapboxMap.OnMoveListener, OnRouteSelectionChangeListener, OffRouteListener, RouteListener, NavigationEventListener {
    private static final int DEFAULT_CAMERA_ZOOM = 18;
    private ConstraintLayout customUINavigation;
    private NavigationView navigationView;
    private MapView mapView;
    private ProgressBar loading;
    private FloatingActionButton launchNavigationFab;
    private Point origin = Point.fromLngLat(106.675789, 10.759050);
    private Point destination = Point.fromLngLat(106.686777, 10.775056);
    private DirectionsRoute route;
    private boolean isNavigationRunning;
    private MapboxNavigation mapboxNavigation;
    private LocationEngine locationEngine;
    private NavigationMapRoute mapRoute;
    private MapboxMap mapboxMap;
    private ConstraintSet navigationMapConstraint;
    private ConstraintSet navigationMapExpandedConstraint;
    private boolean[] constraintChanged;
    private LocationComponent locationComponent;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean reRoute = false;
    private boolean isArrived = false;
    private NavigationViewOptions.Builder mapviewNavigationOptions;
    private ImageButton recenterButton;
    private ImageButton overViewRouteButton;
    private ImageButton stopNavigation;
    private TextView bottomSheetTitle;
    private ImageView maneuverInstruction;
    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private TextView distanceRemaining;
    private SeekBar routeProgressBar;
    private double routeDistance;//Meter
    private final ProgressChangeListener progressChangeListener = (location, routeProgress) -> {
        System.out.println("Progress Changing");
        System.out.println(routeProgress.legIndex());

        List<BannerInstructions> bannerInstructionsList = routeProgress.currentLegProgress().currentStep().bannerInstructions();

//        ((TextView) findViewById(R.id.bottom_sheet_title)).setText(bannerInstructionsList.get(0).primary().text());
//        if (routeProgress.currentLegProgress().followOnStep() != null) {
//            ((TextView) findViewById(R.id.bannerInstructionInfo)).setText(routeProgress.currentLegProgress().upComingStep().maneuver().instruction());
//        }else{
//            ((TextView) findViewById(R.id.bannerInstructionInfo)).setText("");
//        }
        if (routeProgress.currentLegProgress() != null) {
            ArrayList<String> data = calculateTime(Math.round(routeProgress.currentLegProgress().durationRemaining()));
            ((TextView) findViewById(R.id.estimate_time_remaining)).setText(TimeUnit.SECONDS.toMinutes(Math.round(routeProgress.currentLegProgress().durationRemaining())) +"");
            ((TextView) findViewById(R.id.estimate_time_arrive)).setText(data.get(1) );
            ((TextView) findViewById(R.id.all_route_distance)).setText(round(routeDistance / 1000, 2)+"");
            ((TextView) findViewById(R.id.distance_remaining)).setText(String.valueOf(round(routeProgress.currentLegProgress().distanceRemaining() / 1000, 2)));

            ((SeekBar) findViewById(R.id.route_progress_bar)).setProgress(Math.toIntExact(Math.round((routeDistance - routeProgress.currentLegProgress().distanceRemaining()) / routeDistance * 100)));
            System.out.println(routeProgress.currentLegProgress().distanceRemaining());
            System.out.println(routeDistance);
            System.out.println(Math.round((routeDistance - routeProgress.currentLegProgress().distanceRemaining()) / routeDistance * 100));
//            routeDistance
        }
        if (!bannerInstructionsList.isEmpty()) {
            setManeuverInstructionIcon(bannerInstructionsList.get(0).primary().modifier(), bannerInstructionsList.get(0).primary().type());

            ((TextView) findViewById(R.id.bottom_sheet_title)).setText(bannerInstructionsList.get(0).primary().text());
        } else {

            ((TextView) findViewById(R.id.bottom_sheet_title)).setText("");
        }

    };

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static ArrayList<String> calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);

        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);

        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, Math.toIntExact(hours));
        date.add(Calendar.MINUTE, Math.toIntExact(minute));
        date.add(Calendar.SECOND, Math.toIntExact(second));
        ArrayList<String> listResult = new ArrayList<String>();
        Date dateTime = date.getTime();
        String result = "";
        if (day != 0) {
            result += day + " ngày, ";
        }
        if (hours != 0) {
            result += hours + " giờ, ";
        }
        if (minute != 0) {
            result += minute + " phút, ";
        }
        if (second != 0) {
            result += second + " giây";
        }
        listResult.add(result);
        int dataMinutes = dateTime.getMinutes();
        String minuteAtString = "";
        if (dataMinutes < 10) {
            minuteAtString = "0" + dataMinutes;
        } else {
            minuteAtString = dataMinutes + "";
        }

        listResult.add(dateTime.getHours() + ":" + minuteAtString);
        return listResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viet_map_navigation);
        CustomNavigationNotification customNotification = new CustomNavigationNotification(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            customNotification.createNotificationChannel(this);
        }
        MapboxNavigationOptions options = MapboxNavigationOptions.builder()
                .navigationNotification(customNotification).build();
        mapboxNavigation = new MapboxNavigation(this, options);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializeViews(savedInstanceState);
        navigationView.initialize(this);
        navigationMapConstraint = new ConstraintSet();
        navigationMapConstraint.clone(customUINavigation);
        navigationMapExpandedConstraint = new ConstraintSet();
        navigationMapExpandedConstraint.clone(this, R.layout.vietmap_navigation_expand);
        constraintChanged = new boolean[]{false};

        navigationView.initViewConfig(true);
        NavigationPresenter navigationPresenter = navigationView.getNavigationPresenter();
        recenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                navigationPresenter.onRecenterClick();
                changeNavigationActionState(true);
            }
        });
        overViewRouteButton.setOnClickListener(view -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            changeNavigationActionState(false);
            navigationPresenter.onRouteOverviewClick();
        });
        stopNavigation.setOnClickListener(view -> {
            expandCollapse();
            stopNavigationFunction();

        });
        initBottomSheetInfo();
    }

    private void initBottomSheetInfo() {
        routeProgressBar = findViewById(R.id.route_progress_bar);
        routeProgressBar.setOnTouchListener((v, event) -> true);
        maneuverInstruction = findViewById(R.id.maneuverInstruction);
        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        sheetBehavior.setHideable(true);
        bottomSheetTitle = findViewById(R.id.bottom_sheet_title);
        distanceRemaining = findViewById(R.id.distance_remaining);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                changeNavigationActionState(newState != BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initMapRoute() {
        mapRoute = new NavigationMapRoute(mapView, mapboxMap);
        mapRoute.setOnRouteSelectionChangeListener(this);
        mapRoute.addProgressChangeListener(new MapboxNavigation(this));
    }

    private void initializeViews(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.activity_viet_map_navigation);
        customUINavigation = findViewById(R.id.vietmapNavigation);
        mapView = findViewById(R.id.mapView);
        navigationView = findViewById(R.id.navigationView);
        loading = findViewById(R.id.loading);
        launchNavigationFab = findViewById(R.id.launchNavigation);
        navigationView.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        launchNavigationFab.setOnClickListener(v -> {
            expandCollapse();
            launchNavigation();
        });
        mapView.getMapAsync(this);
        overViewRouteButton = findViewById(R.id.overViewRouteButton);
        stopNavigation = findViewById(R.id.stopNavigation);
        recenterButton = findViewById(R.id.recenterBtnCustom);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.stopNavigation);
        transition.addTarget(R.id.recenterBtnCustom);
        transition.addTarget(R.id.overViewRouteButton);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("https://run.mocky.io/v3/961aaa3a-f380-46be-9159-09cc985d9326"), style -> {
            initLocationEngine();
            getCurrentLocation();
            enableLocationComponent(style);
            initMapRoute();
        });
        this.mapboxMap.addOnMapClickListener(this);
    }

    private void expandCollapse() {
        TransitionManager.beginDelayedTransition(customUINavigation);
        ConstraintSet constraint;
        if (constraintChanged[0]) {
            constraint = navigationMapConstraint;
        } else {
            constraint = navigationMapExpandedConstraint;
        }
        constraint.applyTo(customUINavigation);
        constraintChanged[0] = !constraintChanged[0];
    }

    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        return;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                }
            });
            return;
        }
    }

    @Override
    public void onNavigationFinished() {

    }

    @Override
    public void onNavigationRunning() {

    }

    private void enableLocationComponent(Style style) {
        locationComponent = mapboxMap.getLocationComponent();

        if (locationComponent != null) {
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH, 750L, 18.0, 10000.0, 10000.0, null);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.zoomWhileTracking(DEFAULT_CAMERA_ZOOM);
            locationComponent.setRenderMode(RenderMode.GPS);
            locationComponent.setLocationEngine(locationEngine);
        }
    }

    void stopNavigationFunction() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        isNavigationRunning = false;
        navigationView.stopNavigation();
        mapboxNavigation.stopNavigation();
        launchNavigationFab.show();
        recenterButton.setVisibility(View.GONE);
        overViewRouteButton.setVisibility(View.GONE);
        stopNavigation.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        mBottomSheetLayout.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        System.out.println(call);
    }

    @Override
    public void onCancelNavigation() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        expandCollapse();
        stopNavigationFunction();
        reRoute = false;
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute builder = NavigationRoute.builder(this)
                .baseUrl("https://maps.vnpost.vn/navigation_dev/route/")
                .accessToken("pk.eyJ1Ijoic2VubmQiLCJhIjoiY2tkcmdzbDB4MDhzcDJ6bzdoOXNwazduMSJ9.in-0A1ZX6yzIMtnJcLdcRw")
                .origin(origin).destination(destination).alternatives(true).profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC).build();
        builder.getRoute(this);
    }

    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (validRouteResponse(response)) {
            if (reRoute) {
                route = response.body().routes().get(0);
                initNavigationOptions();
                navigationView.updateCameraRouteOverview();
                mapboxNavigation.addNavigationEventListener(this);
                mapboxNavigation.startNavigation(route);
                navigationView.startNavigation(this.mapviewNavigationOptions.build());
//                reRoute = false;
                isArrived = false;
            } else {
                launchNavigationFab.show();
                route = response.body().routes().get(0);
                mapRoute.addRoutes(response.body().routes());
                if (isNavigationRunning) {
                    launchNavigation();
                }
                routeDistance = route.distance();
            }
        }
    }

    void initNavigationOptions() {
        MapboxNavigationOptions navigationOptions = MapboxNavigationOptions.builder().build();
        mapviewNavigationOptions = NavigationViewOptions.builder().navigationListener(this).routeListener(this).navigationOptions(navigationOptions).locationEngine(locationEngine).shouldSimulateRoute(false).progressChangeListener(progressChangeListener).directionsRoute(route).onMoveListener(this);

        navigationView.setCameraDistance(200L);
        mBottomSheetLayout.setVisibility(View.VISIBLE);
    }

    private void setManeuverInstructionIcon(String modifier, String type) {
        System.out.println(type + modifier);
        switch (type + modifier) {
            case "turnleft":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.turn_left));
                break;
            case "turnright":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.turn_right));
                break;
            case "uturn":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.uturn_left));
                break;
            case "turnsharp right":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.sharp_right));
                break;
            case "turnslight right":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.slight_right));
                break;
            case "turnstraight":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.continue_straight));
                break;
            case "turnslight left":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.slight_left));
                break;
            case "turnsharp left":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.sharp_left));
                break;
            case "roundaboutright":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.roundabout_anticlockwise_slight_right));
                break;
            case "roundaboutleft":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.roundabout_anticlockwise_slight_left));
                break;
            case "arrivestraight":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.arrive_straight));
                break;
            case "arriveleft":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.arrive_left));
                break;
            case "arriveright":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.arrive_right));
                break;
            case "end of roadright":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.end_of_road_right));
                break;
            case "end of roadleft":
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources().getDrawable(R.drawable.end_of_road_left));
                break;
            default:
                return;

        }
    }

    private boolean validRouteResponse(Response<DirectionsResponse> response) {
        return response.body() != null && !response.body().routes().isEmpty();
    }

    private void launchNavigation() {
        isNavigationRunning = true;
        changeNavigationActionState(true);
        launchNavigationFab.hide();
        navigationView.setVisibility(View.VISIBLE);

        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mapboxNavigation.addOffRouteListener(this);
        initNavigationOptions();

        mapboxNavigation.startNavigation(route);
        navigationView.startNavigation(this.mapviewNavigationOptions.build());
        isArrived = false;
    }

    @Override
    public void userOffRoute(Location location) {
        if (isArrived) return;
        reRoute = true;
        fetchRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination);
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
//        bottomSheetTitle.setText(routeProgress.currentLegProgress().currentStepProgress().().bannerInstructions[0].primary);
    }

    @Override
    public boolean allowRerouteFrom(Point point) {
        return false;
    }

    @Override
    public void onOffRoute(Point point) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String s) {

    }

    @Override
    public void onArrival() {

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        if (isArrived) return;
        changeNavigationActionState(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert).setTitle("You're arrival").setMessage("Thanks").setPositiveButton("Yes", (dialogInterface, i) -> {
            expandCollapse();
            stopNavigationFunction();
            Toast.makeText(getApplicationContext(), "Yes click", Toast.LENGTH_LONG).show();
        }).setNegativeButton("No", (dialogInterface, i) -> {
            expandCollapse();
            stopNavigationFunction();
            Toast.makeText(getApplicationContext(), "Nothing Happened", Toast.LENGTH_LONG).show();
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        });
        dialog.show();
        System.out.println("Navigation Arrival---------------------------");
        isArrived = true;
        reRoute = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        navigationView.onResume();

        if (locationEngine != null) {
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        stopNavigationFunction();

        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
        mapView.onPause();
        if (locationEngine != null) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.setVisibility(View.VISIBLE);
        navigationView.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        navigationView.onDestroy();
        if (locationEngine != null) {
        }
    }

    void changeNavigationActionState(boolean isNavigationRunning) {


        if (!isNavigationRunning) {
            overViewRouteButton.setVisibility(View.GONE);
            recenterButton.setVisibility(View.VISIBLE);
            stopNavigation.setVisibility(View.GONE);
        } else {
            overViewRouteButton.setVisibility(View.VISIBLE);
            recenterButton.setVisibility(View.GONE);
            stopNavigation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng latLng) {
        getCurrentLocation();
        destination = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
        if (origin != null) {
            fetchRoute(origin, destination);
        }
        return false;
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng latLng) {
        return false;
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        changeNavigationActionState(false);
    }

    @Override
    public void onMove(@NonNull MoveGestureDetector moveGestureDetector) {

    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

    }

    @Override
    public void onNavigationReady(boolean b) {

        isNavigationRunning = b;
    }

    @Override
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        route = directionsRoute;
    }

    @Override
    public void onRunning(boolean b) {
        isNavigationRunning = b;
    }
}