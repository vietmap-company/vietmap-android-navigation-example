# **VietMap Navigation Android SDK documentation**
## Table of contents
[1.  Gradle and AndroidManifest configure](/README.md#i-add-dependencies-below-to-buildgradle-module-app)

[2. Add some configure values for project](/README.md#ii-add-some-configure-values-for-project)

[3. Create a navigation activity](/README.md#iii-create-a-navigation-activity)

[4. Fetch route (Find a route between two coordinates)](/README.md#iv-fetch-route-find-a-route-between-two-coordinates)

[5. Start Navigation](/README.md#v-start-navigation)

[6. Request location permission in MainActivity](/README.md#at-mainactivity-add-the-function-to-check-location-permission-and-push-to-the-navigation-activity)

[7. Custom Navigation UI ](/README.md#custom-navigaion-ui)

[8. Add apikey and styleUrl for project](/README.md#add-apikey-and-styleurl)

###  **I**. Add dependencies below to build.gradle module app

```gradle
    implementation "androidx.recyclerview:recyclerview:1.2.1"
    implementation "androidx.cardview:cardview:1,0,0"
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "com.google.android.gms:play-services-location:21.0.1"
    implementation "com.jakewharton:butterknife:10.2.3"
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'com.github.vietmap-company:maps-sdk-android:2.0.0'
    implementation 'com.github.vietmap-company:maps-sdk-navigation-ui-android:2.0.2'
    implementation 'com.github.vietmap-company:maps-sdk-navigation-android:2.0.1'
    implementation 'com.github.vietmap-company:vietmap-services-core:1.0.0'
    implementation 'com.github.vietmap-company:vietmap-services-directions-models:1.0.1'
    implementation 'com.github.vietmap-company:vietmap-services-turf-android:1.0.2'
    implementation 'com.github.vietmap-company:vietmap-services-android:1.1.2'
    implementation 'com.squareup.picasso:picasso:2.8'
    implementation 'com.github.vietmap-company:vietmap-services-geojson-android:1.0.0'
    implementation group: 'com.squareup.okhttp3', name: 'okhttp', version: '3.2.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
```
Configure the **jitpack repository** in the **setting.gradle** file
```gradle

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add two lines below to the repositories block (In setting.gradle file)
        maven { url 'https://plugins.gradle.org/m2' }
        maven { url 'https://jitpack.io' }
    }
}
```
With older projects, add to the **build.gradle file at module project**
```gradle
allprojects {
    repositories {
        google()
        maven { url "https://jitpack.io" }
    }
}
```
Upgrade the **compileSdk** and **targetSdk** to version **_33_**
```
compileSdk 33
```
```
targetSdk 33
```
Add the below permission request to the **AndroidManifest.xml** file
```xml
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
### **II**. Add some configure values for project

Add below colors to **res/values/colors.xml** file
```xml
    <color name="colorPrimary">#8D64F9</color>
    <color name="colorPrimaryDark">#7845F3</color>
    <color name="colorAccent">#F56FA3</color>
    <color name="red">#FF0000</color>
```
Create **styles.xml** file at **res/values** folder and add below code
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="CustomNavigationMapRoute" parent="NavigationMapRoute">
        <item name="upcomingManeuverArrowBorderColor">@color/red</item>
    </style>

    <style name="CustomNavigationView" parent="NavigationViewLight">
        <item name="navigationViewRouteStyle">@style/CustomNavigationMapRoute</item>
    </style>

    <style name="customInstructionView">
        <item name="navigationViewLocationLayerStyle">@style/NavigationLocationLayerStyle</item>
        <item name="navigationViewRouteOverviewDrawable">@drawable/ic_route_preview</item>
    </style>

    <style name="CustomInstructionView" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="navigationViewPrimary">@color/vietmap_navigation_view_color_primary</item>
        <item name="navigationViewSecondary">@color/vietmap_navigation_view_color_secondary</item>
        <item name="navigationViewAccent">@color/vietmap_navigation_view_color_accent</item>
        <item name="navigationViewPrimaryText">@color/vietmap_navigation_view_color_secondary</item>
        <item name="navigationViewSecondaryText">@color/vietmap_navigation_view_color_accent_text</item>
        <item name="navigationViewDivider">@color/vietmap_navigation_view_color_divider</item>

        <item name="navigationViewListBackground">@color/vietmap_navigation_view_color_list_background</item>

        <item name="navigationViewBannerBackground">@color/vietmap_navigation_view_color_banner_background</item>
        <item name="navigationViewBannerPrimaryText">@color/vietmap_navigation_view_color_banner_primary_text</item>
        <item name="navigationViewBannerSecondaryText">@color/vietmap_navigation_view_color_banner_secondary_text</item>
        <item name="navigationViewBannerManeuverPrimary">@color/vietmap_navigation_view_color_banner_maneuver_primary</item>
        <item name="navigationViewBannerManeuverSecondary">@color/vietmap_navigation_view_color_banner_maneuver_secondary</item>

        <item name="navigationViewProgress">@color/vietmap_navigation_view_color_progress</item>
        <item name="navigationViewProgressBackground">@color/vietmap_navigation_view_color_progress_background</item>

        <item name="navigationViewRouteStyle">@style/NavigationMapRoute</item>

        <item name="navigationViewLocationLayerStyle">@style/mapbox_LocationLayer</item>

        <item name="navigationViewDestinationMarker">@drawable/map_marker_light</item>

        <item name="navigationViewRouteOverviewDrawable">@drawable/ic_route_preview</item>

        <item name="navigationViewMapStyle">@string/navigation_guidance_day</item>
    </style>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

    <style name="TestNavigationViewDark" parent="NavigationViewDark">
        <!-- Map style URL -->
        <item name="navigationViewMapStyle">
            YOUR STYLE URL HERE
        </item>
    </style>

    <style name="TestNavigationViewLight" parent="NavigationViewLight">
        <!-- Map style URL -->
        <item name="navigationViewMapStyle">
            YOUR STYLE URL HERE
        </item>
    </style>

    <style name="progressBarBlue" parent="@style/Theme.AppCompat">
        <item name="colorAccent">@color/blue</item>
    </style>
</resources>

```
Add below code to **string.xml** file

```xml
<resources>
    <string name="app_name">Dẫn đường VietMap</string>

    <string name="title_mock_navigation">Giả bộ Điều hướng</string>
    <string name="description_mock_navigation">Giả bộ phiên điều hướng dùng máy định vị giả.</string>

    <string name="title_off_route_detection">Nhận ra Lạc đường</string>
    <string name="description_off_route_detection">Sử dụng lớp RouteUtils để xác định người dùng bị lạc đường.</string>

    <string name="title_reroute">Tìm Đường đi Mới</string>
    <string name="description_reroute">Thử chức năng tìm đường đi mới trong SDK Điều hướng</string>

    <string name="title_navigation_route_ui">Tuyến đường trên Bản đồ Điều hướng</string>
    <string name="description_navigation_route_ui">Vẽ tuyến đường trên bản đồ</string>

    <string name="title_navigation_launcher">Trình khởi động Điều hướng</string>
    <string name="description_navigation_launcher">Trải nghiệm giao diện người dùng có thể xen vào</string>

    <string name="title_end_navigation">Kết thúc Điều hướng</string>
    <string name="description_end_navigation">Cho biết cách kết thúc điều hướng dùng NavigationView</string>

    <string name="title_dual_navigation_map">Đôi Bản đồ Điều hướng</string>
    <string name="description_dual_navigation_map">Chỉ cách thêm NavigationView và MapView vào cùng bố cục</string>

    <string name="title_waypoint_navigation">Điều hướng giữa các Tọa độ điểm</string>
    <string name="description_waypoint_navigation">Điều hướng giữa các tọa độ điểm</string>

    <string name="title_embedded_navigation">Điều hướng được Nhúng</string>
    <string name="description_embedded_navigation">Điều hướng trong khung nhìn chứa các khung nhìn khác</string>

    <string name="title_fragment_navigation">NavigationView thực hiện bằng Fragment</string>
    <string name="description_fragment_navigation">NavigationView thực hiện bằng Fragment</string>

    <string name="settings">Thiết lập</string>
    <string name="simulate_route">Mô phỏng Tuyến đường</string>
    <string name="language">Ngôn ngữ</string>
    <string name="unit_type">Hệ Đo lường</string>
    <string name="route_profile">Chế độ</string>

    <string name="error_route_not_available">Tuyến đường hiện tại không có sẵn</string>
    <string name="error_select_longer_route">Vui lòng chọn một tuyến đường dài hơn</string>
    <string name="error_valid_route_not_found">Không tìm thấy tuyến đi được.</string>
    <string name="explanation_long_press_waypoint">Chạm lâu vào bản đồ để thả ghim tọa độ điểm</string>

    <string name="title_navigation_ui">Navigation UI</string>
    <string name="description_navigation_ui">Showcase a Navigation UI session. Optional with simulation.</string>


    <string name="title_component_navigation">VietMapNavigation with UI components</string>
    <string name="description_component_navigation">VietMapNavigation with UI components</string>

    <string name="unit_type_key" translatable="false">unit_type</string>
    <string name="simulate_route_key" translatable="false">simulate_route</string>
    <string name="language_key" translatable="false">language</string>
    <string name="route_profile_key" translatable="false">route_profile</string>
    <string name="default_locale" translatable="false">default_for_device</string>
    <string name="default_unit_type" translatable="false">default_for_device</string>
    <string name="current_night_mode" translatable="false">current_night_mode</string>

    <string name="new_location">Vĩ độ: %1$s Kinh độ: %2$s</string>
    <string name="map_view_style_url" translatable="false">YOUR_STYLE_URL_HERE</string>

    <string name="user_location_permission_explanation">Ứng dụng này cần sử dụng quyền vị trí để hoạt động chính xác.</string>
    <string name="user_location_permission_not_granted">Bạn chưa cung cấp quyền vị trí.</string>
    <string name="tts_guide">Nếu ứng dụng không phát âm dẫn đường, vui lòng nhấn nút cài đặt dưới đây và lựa chọn công cụ chuyển văn bản thành giọng nói về Google rồi thử lại.</string>

</resources>
```

### **Note**: Need to add styleUrl in position _*YOUR_STYLE_URL_HERE*_ for key map_view_style_url to run navigation



### **III**. Create a navigation activity


Create new **VietMapNavigationActivity**

Add below code to **xml** file of created **activity**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/vietmapNavigation">

    <vn.vietmap.services.android.navigation.ui.v5.NavigationView
        android:id="@+id/navigationView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:visibility="invisible"
        app:maplibre_cameraZoom="16"
        app:layout_constraintHeight_percent="0.5"
        app:layout_constraintTop_toTopOf="parent"
        app:navigationDarkTheme="@style/NavigationViewDark"
        app:navigationLightTheme="@style/NavigationViewLight"/>

    <vn.vietmap.vietmapsdk.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:maplibre_cameraZoom="16"
        android:visibility="visible"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHeight_percent="1"/>

    <ProgressBar
        android:id="@+id/loading"
        style="?android:attr/progressBarStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:indeterminate="true"
        android:visibility="invisible"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/launchNavigation"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginRight="16dp"
        android:src="@drawable/ic_navigation"
        android:tint="@android:color/white"
        android:visibility="invisible"
        app:backgroundTint="@color/colorPrimary"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>
</androidx.constraintlayout.widget.ConstraintLayout>

```

Activity needs to implement some of the following Listener classes to catch user events, navigation event and process them.


```java
public class VietMapNavigationActivity extends AppCompatActivity
        implements OnNavigationReadyCallback,
        ProgressChangeListener, OnMapReadyCallback,
        NavigationListener, Callback<DirectionsResponse>, 
        VietMapGL.OnMapClickListener, VietMapGL.OnMapLongClickListener,
        VietMapGL.OnMoveListener, OnRouteSelectionChangeListener,
        OffRouteListener, RouteListener, NavigationEventListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vietmap.getInstance(this);
        super.onCreate(savedInstanceState);
    }
}
```
* OnNavigationReadyCallback: Listens when the SDK starts navigation.
* ProgressChangeListener(location, routeProgress): Continuously listens to the user's current location, current route information, next route, and remaining distance that the user needs to travel.
* NavigationListener: Includes three functions:
 - onCancelNavigation: Listens when the user cancels the navigation.
 - onNavigationFinished: Listens when the user completes the journey.
* onNavigationRunning: Listens when the user is actively navigating.
* Callback(DirectionsResponse): Returns the result when the getRoute operation is completed.
* OnMapReadyCallback: Listens when the map initialization is completed and applies the style to the map.
* MapboxMap.OnMapClickListener, MapboxMap.OnMapLongClickListener, MapboxMap.OnMoveListener: Listen to map events such as click, long click, and move.
* OnRouteSelectionChangeListener(DirectionsRoute newRouteSelected):
* onNewPrimaryRouteSelected: Listens when the user selects a different route from the current route and returns the newly selected route.
* OffRouteListener: Listens when the user deviates from the intended route and needs to find an alternative route based on the user's current location.
* userOffRoute(Location currentLocation): Called when the user deviates from the intended route, providing the current location to find a new route.
* RouteListener: Listens when the user arrives at the destination.
* onArrival(): Called when the user reaches the destination.

Define necessary variables

```java
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
    private VietmapNavigation vietmapNavigation;
    private LocationEngine locationEngine;
    private NavigationMapRoute mapRoute;
    private VietMapGL vietmapGL;
    private ConstraintSet navigationMapConstraint;
    private ConstraintSet navigationMapExpandedConstraint;
    private boolean[] constraintChanged;
    private LocationComponent locationComponent;
    private ReplayRouteLocationEngine mockLocationEngine;
    private FusedLocationProviderClient fusedLocationClient;
    private int BEGIN_ROUTE_MILESTONE = 1001;
    private boolean reRoute = false;
    private boolean isArrived = false;
    private NavigationViewOptions.Builder mapviewNavigationOptions; 
```
Call necessary function in **onCreate** callback
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomNavigationNotification customNotification = new CustomNavigationNotification(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            customNotification.createNotificationChannel(this);
        }
        VietmapNavigationOptions options = VietmapNavigationOptions.builder()
                .navigationNotification(customNotification).build();
        vietmapNavigation = new VietmapNavigation(this, options);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializeViews(savedInstanceState);
        navigationView.initialize(this);
        navigationMapConstraint = new ConstraintSet();
        navigationMapConstraint.clone(customUINavigation);
        navigationMapExpandedConstraint = new ConstraintSet();
        navigationMapExpandedConstraint.clone(this, R.layout.vietmap_navigation_expand);
        constraintChanged = new boolean[]{false};
    }
```
**initializeViews** function
```java
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
    }
```
### In **onMapReady** callback function:
```java
    @Override
    public void onMapReady(@NonNull VietMapGL vietmapGL) {
        this.vietmapGL = vietmapGL;
        vietmapGL.setStyle(new Style.Builder().fromUri(YOUR_STYLE_URL_HERE), style -> {
            initLocationEngine();
            getCurrentLocation();
            enableLocationComponent(style);
            initMapRoute();
        });
        this.vietmapGL.addOnMapClickListener(this);
    }
```
```java
    private void initLocationEngine() {
        mockLocationEngine = new ReplayRouteLocationEngine();
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        long DEFAULT_INTERVAL_IN_MILLISECONDS = 5000;
        long DEFAULT_MAX_WAIT_TIME = 30000;
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mockLocationEngine.assignLastLocation(origin);
            return;
        }
    }

    private void initMapRoute() {
        mapRoute = new NavigationMapRoute(mapView, vietmapGL);
        mapRoute.setOnRouteSelectionChangeListener(this);
        mapRoute.addProgressChangeListener(new VietmapNavigation(this));
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                        }
                    });
            return;
        }
    }

    private void enableLocationComponent(Style style) {
        locationComponent = vietmapGL.getLocationComponent();

        if (locationComponent != null) {
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            locationComponent.zoomWhileTracking(DEFAULT_CAMERA_ZOOM);
            locationComponent.setRenderMode(RenderMode.GPS);
            locationComponent.setLocationEngine(locationEngine);
        }
    }
```


Create **_layout xml_** named _vietmap_navigation_expand_
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/vietmapNavigationExpand"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <vn.vietmap.services.android.navigation.ui.v5.NavigationView
        android:id="@+id/navigationView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:visibility="visible"
        app:vietmap_cameraZoom="15"
        app:layout_constraintHeight_percent="1"
        app:layout_constraintTop_toTopOf="@+id/vietmapNavigationExpand"
        app:layout_constraintBottom_toBottomOf="@+id/vietmapNavigationExpand"
        app:navigationDarkTheme="@style/NavigationViewDark"
        app:navigationLightTheme="@style/NavigationViewLight"/>
    <vn.vietmap.vietmapsdk.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:vietmap_cameraZoom="15"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@+id/vietmapNavigationExpand"
        app:layout_constraintHeight_percent="0"/>

    <ProgressBar
        android:id="@+id/loading"
        style="?android:attr/progressBarStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:indeterminate="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/launchNavigation"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginRight="16dp"
        android:src="@drawable/ic_navigation"
        android:tint="@android:color/white"
        android:visibility="gone"
        app:backgroundTint="@color/colorPrimary"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```
**expandCollapse** function:
```java
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
```
**stopNavigationFunction** 
```java
    void stopNavigationFunction() {
        navigationView.stopNavigation();
        vietmapNavigation.stopNavigation();
        launchNavigationFab.show();
    }
```
**onCancelNavigation** callback (Listener when user click on cancel navigation button):
```java
    @Override
    public void onCancelNavigation() {
        isNavigationRunning=false;
        expandCollapse();
        stopNavigationFunction();
    }
```
**onRunning** and **onNavigationReady** callback:
```java
    @Override
    public void onRunning(boolean b) {
        isNavigationRunning = b;
    }

    @Override
    public void onNavigationReady(boolean b) {
        isNavigationRunning = b;
    }
```
**onNewPrimaryRouteSelected** callback (Listen when user select a new direction route):
```java
    @Override
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        route=directionsRoute;
    }
```
Create **CustomNavigationNotification** class to show notification about current routing information
```java

public class CustomNavigationNotification implements NavigationNotification {

    private static final int CUSTOM_NOTIFICATION_ID = 91234821;
    private static final String STOP_NAVIGATION_ACTION = "stop_navigation_action";
    private final Notification customNotification;
    private final NotificationCompat.Builder customNotificationBuilder;
    private final NotificationManager notificationManager;
    private BroadcastReceiver stopNavigationReceiver;
    private int numberOfUpdates;

    public CustomNavigationNotification(Context applicationContext) {
        notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        customNotificationBuilder = new NotificationCompat.Builder(applicationContext, NAVIGATION_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Custom Navigation Notification")
                .setContentText("Display your own content here!")
                .setContentIntent(createPendingStopIntent(applicationContext));
        customNotification = customNotificationBuilder.build();
    }

    @Override
    public Notification getNotification() {
        return customNotification;
    }

    @Override
    public int getNotificationId() {
        return CUSTOM_NOTIFICATION_ID;
    }

    @Override
    public void updateNotification(RouteProgress routeProgress) {
        // Update the builder with a new number of updates
        customNotificationBuilder.setContentText("Number of updates: " + numberOfUpdates++);
        notificationManager.notify(CUSTOM_NOTIFICATION_ID, customNotificationBuilder.build());
    }

    @Override
    public void onNavigationStopped(Context context) {
        try {
            context.unregisterReceiver(stopNavigationReceiver);
        }catch(Exception e){}
        notificationManager.cancel(CUSTOM_NOTIFICATION_ID);
    }

    public void register(BroadcastReceiver stopNavigationReceiver, Context applicationContext) {
        this.stopNavigationReceiver = stopNavigationReceiver;
        applicationContext.registerReceiver(stopNavigationReceiver, new IntentFilter(STOP_NAVIGATION_ACTION));
    }

    private PendingIntent createPendingStopIntent(Context context) {
        Intent stopNavigationIntent = new Intent(STOP_NAVIGATION_ACTION);
        return PendingIntent.getBroadcast(context, 0, stopNavigationIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(Context context) {
        NotificationChannel chan = new NotificationChannel(NAVIGATION_NOTIFICATION_CHANNEL, "CustomNavigationNotification", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null) {
            service.createNotificationChannel(chan);
        }
    }
}
```
### **IV**. Fetch route (Find a route between two coordinates)
Fetch route API required two params is **_origin_** and **_destination_**, is the current position of the user and the target location.

Example:
```java
    Point origin = Point.fromLngLat(106.675884,10.759197);
    Point destination = Point.fromLngLat( 105.577136, 18.932147);
```
### From **_point_** and **_destination_** point, let call **fetchRoute** function as below code:
```java
    private void fetchRoute(Point origin, Point destination, @Nullable Double bearing) {
        NavigationRoute.Builder builder = NavigationRoute
                .builder(this)
                .apikey(YOUR_VIETMAP_APIKEY_HERE)
                .origin(origin, bearing, bearing)
                .destination(destination, bearing, bearing);
        
        builder.build().getRoute(this);
    }
```
Handle response of **fetchRoute** function:
```java
    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

        if (validRouteResponse(response)) {
            if (reRoute) {
                route = response.body().routes().get(0);
                initNavigationOptions();
                navigationView.updateCameraRouteOverview();
                vietmapNavigation.addNavigationEventListener(this);
                vietmapNavigation.startNavigation(route);
                navigationView.startNavigation(this.mapviewNavigationOptions.build());
                reRoute = false;
                isArrived = false;
            } else {
                launchNavigationFab.show();
                route = response.body().routes().get(0);
                mapRoute.addRoutes(response.body().routes());
                if (isNavigationRunning) {
                    launchNavigation();
                }
            }
        }
    }
```
### **V**. Start Navigation
After called the fetch route API, you should configure some options to start navigation
```java
    void initNavigationOptions() {
        VietmapNavigationOptions navigationOptions = VietmapNavigationOptions.builder()
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
    }
```
**progressChangeListener** callback function response **location** (current location of the user) and **routeProgress** (Information about the route the user is taking, the direction of the next turn, the distance to the next turn,...)
```java
    private ProgressChangeListener progressChangeListener = (location, routeProgress) -> {
        System.out.println("Progress Changing");
    };
```
```java
    private boolean validRouteResponse(Response<DirectionsResponse> response) {
        return response.body() != null && !response.body().routes().isEmpty();
    }
```
**initNavigationOptions** function will be called before starting the navigation
```java
    private void launchNavigation() {
        launchNavigationFab.hide();
        navigationView.setVisibility(View.VISIBLE);
        vietmapNavigation.addOffRouteListener(this);
        initNavigationOptions();
        vietmapNavigation.startNavigation(route);
        navigationView.startNavigation(this.mapviewNavigationOptions.build());
        isArrived = false;
    }
```
**launchNavigation** should call inside a button

In **launchNavigation** function, there're two **startNavigation** function called:
- **vietmapNavigation** is a controller to listen to all status and information of navigation, and return some navigation callback.
- **navigationView** will show the UI of navigation.
```java
    @Override
    public void userOffRoute(Location location) {
        if(isArrived) return;
            reRoute = true;
            fetchRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination);
    }
```
#### **userOffRoute** function return a callback when the user goes wrong with the returned route
```java
    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        
    }
```
#### **onProgressChange** function listen as the user moves, continuously update information about the route the user is traveling, the remaining distance,...
```java
    @Override
    public void onArrival() {
        if(isArrived) return;
        //Xử lý thông báo và kết thúc dẫn đường tại đây
        isArrived=true;
    }
```
**onArrival** function listen when user is arrival to the destination location



Adding the following **callbacks** functions to ensure proper initialization and memory management, as well as handling user actions, the NavigationView component must be linked to the activity's lifecycle using some callbacks below. This allows the NavigationView to properly handle the activity's lifecycle and respond accordingly.
```java

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
        mapView.onStart();
    }

    @Override
        public void onResume() {
        super.onResume();
        navigationView.onResume();
        mapView.onResume();
        if (locationEngine != null) {
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
        mapView.onLowMemory();
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
        navigationView.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
        }
    }

```
## At **MainActivity**, add the function to check location permission and push to the navigation activity
```java

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private PermissionsManager permissionsManager;

    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button =  findViewById(R.id.pushToNavigationScreen);
        Button ttsButton = findViewById(R.id.testSpeech);
        Button speechAgain = findViewById(R.id.speechAgain);
        Intent it = new Intent(this, VietMapNavigationActivity.class);
        button.setOnClickListener(view -> {
            startActivity(it);
            speechAgain.setVisibility(View.GONE);
        });
        speechAgain.setOnClickListener(view -> speakOut("Ngôn ngữ: Tiếng Việt"));
        ttsButton.setOnClickListener(view-> {
            startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
            speechAgain.setVisibility(View.VISIBLE);
        });
        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> setTextToSpeechLanguage());
    }

    private void setTextToSpeechLanguage() {
        Locale language =new Locale("vi","VN");
        int result = textToSpeech.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(this, "Không có dữ liệu ngôn ngữ", Toast.LENGTH_LONG).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Chưa hỗ trợ ngôn ngữ "+language.getLanguage() , Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "Ngôn ngữ: Tiếng Việt", Toast.LENGTH_LONG).show();
            speakOut("Ngôn ngữ: Tiếng Việt");
        }
    }
    private void speakOut(String speechContent) {
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(speechContent, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
```
In **activity_main.xml** file, add button layout
```xml

    <Button
        android:id="@+id/pushToNavigationScreen"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="36dp"
        android:text="Dẫn đường với VietMap"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.494"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
         />

    <TextView
        android:id="@+id/guideText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="36dp"
        android:layout_marginLeft="15dp"
        android:textSize="16dp"
        android:layout_marginRight="15dp"
        app:layout_constraintTop_toBottomOf="@+id/pushToNavigationScreen"
        app:layout_constraintStart_toStartOf="parent"
        android:text="@string/tts_guide"
        />

    <Button
        android:id="@+id/testSpeech"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="36dp"
        android:text="Cài đặt text to speech"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.494"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/guideText"
        />

    <Button
        android:id="@+id/speechAgain"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="36dp"
        android:text="Nghe lại"
        android:visibility="gone"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.494"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/testSpeech"
        />
```
# **Custom Navigaion UI**
```java
    navigationView.initViewConfig(true);
``` 
In the onCreate callback, add this code to hide all of the navigation UI, the sdk will show the map and navigation only.

```xml
    <androidx.appcompat.widget.LinearLayoutCompat
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        android:id="@+id/navigationAction"
        android:orientation="horizontal"
        app:layout_constraintBottom_toBottomOf="parent">
        <Button
            android:id="@+id/overViewRouteButton"
            android:layout_width="wrap_content"
            app:layout_constraintStart_toEndOf="@+id/stopNavigation"
            android:layout_height="wrap_content"
            app:layout_constraintTop_toBottomOf="@id/stopNavigation"
            android:layout_gravity="top"
            android:layout_margin="16dp"
            android:text="Over View"
            app:layout_anchorGravity="top|left"
            android:visibility="gone"
            android:textColor="@color/black"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
        <Button
            android:id="@+id/recenterBtnCustom"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toBottomOf="parent"
            android:layout_gravity="top"
            android:layout_margin="16dp"
            android:text="Recenter"
            android:visibility="gone"
            android:textColor="@color/black"
            app:layout_anchorGravity="top|left"
            app:layout_constraintStart_toStartOf="parent" />
        <Button
            android:id="@+id/stopNavigation"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/black"
            app:layout_constraintTop_toBottomOf="@id/overViewRouteButton"
            android:visibility="gone"
            app:layout_constraintBottom_toBottomOf="parent"
            android:layout_gravity="top"
            android:layout_margin="16dp"
            android:text="Stop"
            app:layout_anchorGravity="top|left"
            app:layout_constraintStart_toStartOf="parent" />
    </androidx.appcompat.widget.LinearLayoutCompat>
```
Add this code to xml layout of VietmapNavigationActivity

```java
    private Button recenterButton;
    private Button overViewRouteButton;
    private Button stopNavigation;
```

Define 3 buttons to show 3 action re-center to the navigation, overview all route, cancel navigation.

## **Handle when user click on 3 button above**
-   Define **NavigationPresenter** in **onCreate** function
```java
    NavigationPresenter navigationPresenter = navigationView.getNavigationPresenter();
```
### Handle above function:
-  **(recenterFunction)**:
```java
    recenterButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            navigationPresenter.onRecenterClick();
            changeNavigationActionState(true);
        }
    });
```
-  **(routeOverViewFunction)**:
```java
    overViewRouteButton.setOnClickListener(view -> {
        navigationPresenter.onRouteOverviewClick();
        changeNavigationActionState(false);
    });
```
-  **(stopNavigation)**:
```java
    stopNavigation.setOnClickListener(view -> {
        changeNavigationActionState(false);
        expandCollapse();
        stopNavigationFunction();
    });
```
-   Add below code to **stopNavigationFunction**:
```java
    void stopNavigationFunction(){
        navigationView.stopNavigation();
        vietmapNavigation.stopNavigation();
        launchNavigationFab.show();
        //Add below code
        recenterButton.setVisibility(View.GONE);
        overViewRouteButton.setVisibility(View.GONE);
        stopNavigation.setVisibility(View.GONE);
    }
```
-   Listen when user move map, then show the recenter button **(recenterButton)**:
```java
    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
        changeNavigationActionState(false);
    }
```
-   Create a function to show/hide 3 button above **(changeNavigationActionState)**:
```java
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
```
-   Add below code to the **initializeViews** function:
```java
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
        /// Add the below code
        overViewRouteButton = findViewById(R.id.overViewRouteButton);
        stopNavigation = findViewById(R.id.stopNavigation);
        recenterButton = findViewById(R.id.recenterBtnCustom);
    }
```
- Add below code to the **launchNavigation** function
```java
    private void launchNavigation() {
    ...
        changeNavigationActionState(true);
    ...
    }
```
On **stopNavigation** function:
```java
    void stopNavigationFunction(){
        navigationView.stopNavigation();
        vietmapNavigation.stopNavigation();
        recenterButton.setVisibility(View.GONE);
        overViewRouteButton.setVisibility(View.GONE);
        stopNavigation.setVisibility(View.GONE);
        launchNavigationFab.show();
    }
```

- All information about navigation will response in [_**onProgressChange**_](/README.md#onprogresschange-function-listen-as-the-user-moves-continuously-update-information-about-the-route-the-user-is-traveling-the-remaining-distance)

# Add **apikey** and  **styleUrl**
To ensure that the application does not crash when running, you need to add the full **styleUrl** and **apikey** that VietMap provides at the following locations:

[Add **_styleUrl_** in _src/values/string.xml_ file](/README.md#note-need-to-add-styleurl-in-position-your_style_url_here-for-key-map_view_style_url-to-run-navigation)

[Add **_styleUrl_** in **onMapReady** function](/README.md#in-onmapready-callback-function)


[Add **_apikey_** in **fetchRoute** function](/README.md#iv-fetch-route-find-a-route-between-two-coordinates)