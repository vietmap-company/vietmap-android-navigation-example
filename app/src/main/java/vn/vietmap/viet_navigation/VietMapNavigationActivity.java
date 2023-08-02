package vn.vietmap.viet_navigation;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
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

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import vn.vietmap.viet_navigation.R;

import vn.vietmap.viet_navigation.interfaces.RetrofitAPI;
import vn.vietmap.viet_navigation.models.VietMapV3PlaceModel;
import vn.vietmap.viet_navigation.models.VietMapV3SearchResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.vietmap.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.camera.CameraUpdate;
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.geometry.LatLngBounds;
import vn.vietmap.vietmapsdk.location.LocationComponent;
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions;
import vn.vietmap.vietmapsdk.location.engine.LocationEngine;
import vn.vietmap.vietmapsdk.location.modes.CameraMode;
import vn.vietmap.vietmapsdk.location.modes.RenderMode;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.VietMapGL;
import vn.vietmap.vietmapsdk.maps.OnMapReadyCallback;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.services.android.navigation.ui.v5.NavigationPresenter;
import vn.vietmap.services.android.navigation.ui.v5.NavigationView;
import vn.vietmap.services.android.navigation.ui.v5.NavigationViewOptions;
import vn.vietmap.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener;
import vn.vietmap.services.android.navigation.ui.v5.listeners.RouteListener;
import vn.vietmap.services.android.navigation.ui.v5.route.NavigationMapRoute;
import vn.vietmap.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import vn.vietmap.services.android.navigation.v5.location.engine.LocationEngineProvider;
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigation;
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigationOptions;
import vn.vietmap.services.android.navigation.v5.navigation.NavigationEventListener;
import vn.vietmap.services.android.navigation.v5.navigation.NavigationRoute;
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener;
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import vn.vietmap.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VietMapNavigationActivity extends AppCompatActivity
        implements OnNavigationReadyCallback, ProgressChangeListener,
        NavigationListener, Callback<DirectionsResponse>, OnMapReadyCallback,
        VietMapGL.OnMapClickListener, VietMapGL.OnMapLongClickListener,
        VietMapGL.OnMoveListener, OnRouteSelectionChangeListener,
        OffRouteListener, RouteListener, NavigationEventListener {
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
    private VietmapNavigation mapboxNavigation;
    private LocationEngine locationEngine;
    private NavigationMapRoute mapRoute;
    private VietMapGL vietmapGL;
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
    private ImageButton muteButton;
    final String BASE_URL = "https://maps.vietmap.vn/";
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
    private FloatingSearchView vietMapSearchBar;
    private Runnable searchRunnable;
    private final Handler handler = new Handler();
    private List<VietMapV3SearchResponse> responseData;
    private String nextNavigationGuide = "";
    private final ProgressChangeListener progressChangeListener = (location, routeProgress) -> {
        List<BannerInstructions> bannerInstructionsList = routeProgress.currentLegProgress().currentStep().bannerInstructions();

        if (routeProgress.currentLegProgress() != null) {
            ArrayList<String> data = calculateTime(Math.round(routeProgress.currentLegProgress().durationRemaining()));
            ((TextView) findViewById(R.id.estimate_time_remaining)).setText(TimeUnit.SECONDS.toMinutes(Math.round(routeProgress.currentLegProgress().durationRemaining())) + "");
            ((TextView) findViewById(R.id.estimate_time_arrive)).setText(data.get(1));
            ((TextView) findViewById(R.id.all_route_distance)).setText(round(routeDistance / 1000, 2) + "");
//            ((TextView) findViewById(R.id.distance_remaining)).setText(String.valueOf(round(routeProgress.currentLegProgress().distanceRemaining() / 1000, 2)));
            Double stepDistanceRemaining = round(routeProgress.stepDistanceRemaining() / 1000, 2);
            String distanceUnit = "Km";
            if (stepDistanceRemaining < 1) {
                distanceUnit = "mét";
                stepDistanceRemaining *= 1000;
                ((TextView) findViewById(R.id.distance_remaining)).setText(String.valueOf(stepDistanceRemaining.intValue()));
            } else {
                ((TextView) findViewById(R.id.distance_remaining)).setText(String.valueOf(stepDistanceRemaining));
            }
            ((TextView) findViewById(R.id.distance_remaining_suffix)).setText(" " + distanceUnit + ", " + nextNavigationGuide.toLowerCase());

            ((SeekBar) findViewById(R.id.route_progress_bar)).setProgress(Math.toIntExact(Math.round((routeDistance - routeProgress.currentLegProgress().distanceRemaining()) / routeDistance * 100)));
            System.out.println(routeProgress.stepDistanceRemaining());
        }
        if (!bannerInstructionsList.isEmpty()) {
            setManeuverInstructionIcon(bannerInstructionsList.get(0).primary().modifier(), bannerInstructionsList.get(0).primary().type());

            ((TextView) findViewById(R.id.bottom_sheet_title)).setText(bannerInstructionsList.get(0).primary().text());
        } else {
            ((TextView) findViewById(R.id.bottom_sheet_title)).setText("");
        }


    };
    private final String apiKey = "YOUR_API_KEY_HERE";

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
        getSupportActionBar().hide();
        Vietmap.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viet_map_navigation);

        CustomNavigationNotification customNotification = new CustomNavigationNotification(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            customNotification.createNotificationChannel(this);
        }
        muteButton = findViewById(R.id.mute);
        VietmapNavigationOptions options = VietmapNavigationOptions.builder()
                .navigationNotification(customNotification).build();
        mapboxNavigation = new VietmapNavigation(this, options);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializeViews(savedInstanceState);
        navigationView.initialize(this);
        navigationMapConstraint = new ConstraintSet();
        navigationMapConstraint.clone(customUINavigation);
        navigationMapExpandedConstraint = new ConstraintSet();
        navigationMapExpandedConstraint.clone(this, R.layout.vietmap_navigation_expand);
        constraintChanged = new boolean[]{false};
        vietMapSearchBar = findViewById(R.id.floating_search_view);
        navigationView.initViewConfig(true);
        NavigationPresenter navigationPresenter = navigationView.getNavigationPresenter();
        muteButton.setOnClickListener(v->{
            if(navigationView.navigationViewModel.speechPlayer.isMuted()){
                navigationView.navigationViewModel.speechPlayer.setMuted(false);
                muteButton.setImageDrawable(getResources().getDrawable( R.drawable.volume_up_24px_rounded));
            }else{
                muteButton.setImageDrawable(getResources().getDrawable( R.drawable.volume_off_24px_rounded));
                navigationView.navigationViewModel.speechPlayer.setMuted(true);
            }
        });
        recenterButton.setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            navigationPresenter.onRecenterClick();
            changeNavigationActionState(true);
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
        vietMapSearchBar.setOnQueryChangeListener((oldQuery, newQuery) -> {
            handler.removeCallbacks(searchRunnable); // Cancel any pending search requests

            searchRunnable = () -> {
                if (!newQuery.isEmpty()) {
                    searchAddress(newQuery);
                    loading.setVisibility(View.VISIBLE);
                } else {
                    vietMapSearchBar.clearSuggestions();
                    responseData = null;
                }
            };

            handler.postDelayed(searchRunnable, 500);
        });

        vietMapSearchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                System.out.println(searchSuggestion.getBody());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    responseData.forEach(featureData -> {
                        if (featureData.getDisplay() == searchSuggestion.getBody()) {
                            getPlaceLocationAndFetchRoute(featureData.getRef_id());
                            vietMapSearchBar.clearSearchFocus();
                            return;
                        }
                    });
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                searchAddress(currentQuery);
            }
        });
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
                if (isNavigationRunning) {
                    changeNavigationActionState(newState != BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initMapRoute() {
        mapRoute = new NavigationMapRoute(mapView, vietmapGL);
        mapRoute.setOnRouteSelectionChangeListener(this);
        mapRoute.addProgressChangeListener(new VietmapNavigation(this));
    }

    private void initializeViews(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.activity_viet_map_navigation);
        customUINavigation = findViewById(R.id.vietmapNavigation);
        mapView = findViewById(R.id.mapView);
        navigationView = findViewById(R.id.navigationView);
        loading = findViewById(R.id.loading);
        launchNavigationFab = findViewById(R.id.launchNavigation);
        navigationView.onCreate(savedInstanceState,null);
        mapView.onCreate(savedInstanceState);
        launchNavigationFab.setOnClickListener(v -> {
            expandCollapse();
            launchNavigation();
        });
        mapView.getMapAsync(this);
        overViewRouteButton = findViewById(R.id.overViewRouteButton);
        stopNavigation = findViewById(R.id.stopNavigation);
        recenterButton = findViewById(R.id.recenterBtnCustom);
        muteButton = findViewById(R.id.mute);
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.stopNavigation);
        transition.addTarget(R.id.recenterBtnCustom);
        transition.addTarget(R.id.overViewRouteButton);
        transition.addTarget(R.id.mute);

    }

    @Override
    public void onMapReady(@NonNull VietMapGL vietmapGL) {
        this.vietmapGL = vietmapGL;
        vietmapGL.setStyle(new Style.Builder().fromUri("https://run.mocky.io/v3/961aaa3a-f380-46be-9159-09cc985d9326"), style -> {
            initLocationEngine();
            getCurrentLocation(true, false);
            enableLocationComponent(style);
            initMapRoute();
        });
        this.vietmapGL.addOnMapClickListener(this);
        this.vietmapGL.addOnMapLongClickListener(this);
        Toast.makeText(this, "Nhấn giữ trên bản đồ hoặc tìm kiếm địa điểm để bắt đầu dẫn đường", Toast.LENGTH_LONG).show();
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

    private void getCurrentLocation(Boolean isGetOrigin, Boolean isGetBearing) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    if (isGetOrigin) {
                        origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    }
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
        locationComponent = vietmapGL.getLocationComponent();

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
//        overViewRouteButton.setVisibility(View.GONE);
//        stopNavigation.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        vietMapSearchBar.setVisibility(View.VISIBLE);
        mBottomSheetLayout.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        Toast.makeText(VietMapNavigationActivity.this, "Có lỗi xảy ra khi tìm đường đi", Toast.LENGTH_LONG).show();
        loading.setVisibility(View.GONE);
        System.out.println(call);
    }

    @Override
    public void onCancelNavigation() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        expandCollapse();
        stopNavigationFunction();
        reRoute = false;
    }

    private void fetchRouteWithBearing(Point origin, Point destination) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    if (location.hasBearing()) {
                        fetchRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination, Double.valueOf(location.getBearing()));
                    } else {
                        fetchRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination, null);
                    }
                }
            });
            return;
        }
    }

    private void fetchRoute(Point origin, Point destination, @Nullable Double bearing) {
        System.out.println("Bearingg-------------------" + bearing);
        NavigationRoute.Builder builder = NavigationRoute
                .builder(this)
                .apikey("95f852d9f8c38e08ceacfd456b59059d0618254a50d3854c")
                .origin(origin, bearing, bearing).destination(destination, bearing, bearing);

        builder.build().getRoute(this);

    }

    private void searchAddress(String keySearch) {
        retrofitAPI.autocomplete(keySearch, apiKey, origin.latitude() + "," + origin.longitude()).enqueue(new Callback<List<VietMapV3SearchResponse>>() {
            public void onResponse(Call<List<VietMapV3SearchResponse>> call, Response<List<VietMapV3SearchResponse>> response) {
                if (response.isSuccessful()) {
                    responseData = response.body();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ArrayList<SearchSuggestion> searchSuggestions = new ArrayList<SearchSuggestion>();
                        responseData.forEach((featureData -> {
                            searchSuggestions.add(new SearchSuggestion() {
                                @Override
                                public String getBody() {
                                    return featureData.getDisplay();
                                }

                                @Override
                                public int describeContents() {
                                    return 0;
                                }

                                @Override
                                public void writeToParcel(@NonNull Parcel dest, int flags) {

                                }
                            });
                        }));
                        vietMapSearchBar.swapSuggestions(searchSuggestions);
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            public void onFailure(Call<List<VietMapV3SearchResponse>> call, Throwable t) {
                Toast.makeText(VietMapNavigationActivity.this, "Có lỗi xảy ra khi lấy thông tin địa điểm", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }


    private void getPlaceLocationAndFetchRoute(String refId) {
        retrofitAPI.getPlaceData(refId, apiKey).enqueue(new Callback<VietMapV3PlaceModel>() {
            public void onResponse(Call<VietMapV3PlaceModel> call, Response<VietMapV3PlaceModel> response) {
                if (response.isSuccessful()) {
                    destination = Point.fromLngLat(response.body().getLng(), response.body().getLat());
                    fetchRouteWithBearing(origin, destination);
                } else {
                    System.out.println(response.errorBody());
                }
            }

            public void onFailure(Call<VietMapV3PlaceModel> call, Throwable t) {
                Toast.makeText(VietMapNavigationActivity.this, "Có lỗi xảy ra khi lấy thông tin địa điểm", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        loading.setVisibility(View.GONE);
        if (validRouteResponse(response)) {
            System.out.println(response.raw().request().url());
            System.out.println("------------------------Request Url");
            if (reRoute) {
                route = response.body().routes().get(0);
                initNavigationOptions();
                navigationView.updateCameraRouteOverview();
                mapboxNavigation.addNavigationEventListener(this);
                navigationView.startNavigation(this.mapviewNavigationOptions.build());
                isArrived = false;
            } else {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                boundsBuilder.include(new LatLng(origin.latitude(), origin.longitude()));
                boundsBuilder.include(new LatLng(destination.latitude(), destination.longitude()));

                LatLngBounds bounds = boundsBuilder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                vietmapGL.easeCamera(cameraUpdate, 800);
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
        VietmapNavigationOptions navigationOptions = VietmapNavigationOptions.builder()
                .maximumDistanceOffRoute(10)
                .build();
        mapviewNavigationOptions = NavigationViewOptions
                .builder().navigationListener(this)
                .routeListener(this)
                .navigationOptions(navigationOptions)
                .locationEngine(locationEngine)
                .shouldSimulateRoute(false)
                .progressChangeListener(progressChangeListener)
                .directionsRoute(route)
                .onMoveListener(this);

        navigationView.setCameraDistance(100L);
        mBottomSheetLayout.setVisibility(View.VISIBLE);
    }

    private void setManeuverInstructionIcon(String modifier, String type) {
        System.out.println(type + modifier);
        switch (type + modifier) {
            case "turnleft":
                nextNavigationGuide = "Rẽ trái";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.turn_left));
                break;
            case "turnright":

                nextNavigationGuide = "Rẽ phải";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.turn_right));
                break;
            case "uturn":

                nextNavigationGuide = "Rẽ trái và quay đầu";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.uturn_left));
                break;
            case "turnsharp right":

                nextNavigationGuide = "Rẽ phải và quay đầu";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.sharp_right));
                break;
            case "turnslight right":

                nextNavigationGuide = "Rẽ chếch về phải";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.slight_right));
                break;
            case "turnstraight":

                nextNavigationGuide = "Tiếp tục đi thẳng";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.continue_straight));
                break;
            case "turnslight left":

                nextNavigationGuide = "Rẽ chếch về trái";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.slight_left));
                break;
            case "turnsharp left":
                nextNavigationGuide = "Rẽ ngoặt về trái";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.sharp_left));
                break;
            case "roundaboutright":

                nextNavigationGuide = "Rẽ chếch phải khỏi vòng xoay";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.roundabout_anticlockwise_slight_right));
                break;
            case "roundaboutleft":

                nextNavigationGuide = "Rẽ chếch trái khỏi vòng xoay";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.roundabout_anticlockwise_slight_left));
                break;
            case "arrivestraight":

                nextNavigationGuide = "Đích đến phía trước";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.arrive_straight));
                break;
            case "arriveleft":

                nextNavigationGuide = "Đích đến bên trái";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.arrive_left));
                break;
            case "arriveright":

                nextNavigationGuide = "Đích đến bên phải";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.arrive_right));
                break;
            case "end of roadright":

                nextNavigationGuide = "Rẽ phải ở cuối đường";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.end_of_road_right));
                break;
            case "end of roadleft":

                nextNavigationGuide = "Rẽ trái ở cuối đường";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.end_of_road_left));
                break;
            case "rotaryright":

                nextNavigationGuide = "Đi vào vòng xoay";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.roundabout_anticlockwise_sharp_right));
                break;
            case "rotaryleft":

                nextNavigationGuide = "Đi vào vòng xoay";
                ((ImageView) findViewById(R.id.maneuverInstruction)).setImageDrawable(getResources()
                        .getDrawable(R.drawable.roundabout_anticlockwise_sharp_left));
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
        vietMapSearchBar.setVisibility(View.GONE);
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
        origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        fetchRouteWithBearing(origin, destination);
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
        vietMapSearchBar.setVisibility(View.VISIBLE);
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

    void changeNavigationActionState(boolean isNavigationRunning) {
        if (!isNavigationRunning) {
            recenterButton.setVisibility(View.VISIBLE);
        } else {
            recenterButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng latLng) {

        return false;
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng latLng) {
        getCurrentLocation(true, true);
        destination = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
        if (origin != null) {
            fetchRouteWithBearing(origin, destination);
        }
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