# **VietMap Navigation Android SDK documentation**

[<img src="https://bizweb.dktcdn.net/100/415/690/themes/804206/assets/logo.png?1689561872933" height="40"/> </p>](https://vietmap.vn/maps-api)
## Table of contents
[1.  Gradle and AndroidManifest configure](/README.md#i-add-dependencies-below-to-buildgradle-module-app)

[2. Request location permission in MainActivity](/README.md#request-location-permission-in-mainactivity)

[2. Add some configure values for project](/README.md#ii-add-some-configure-values-for-project)

[3. Create a navigation activity](/README.md#iii-create-a-navigation-activity)

[4. Fetch route (Find a route between two coordinates)](/README.md#fetch-route-between-2-point-with-bearing)

[5. Implement some necessary function](/README.md#implement-some-necessary-function)

[5. Some useful function](/README.md#some-useful-function)

[6. Add some additional functions](/README.md#add-some-additional-functions)

[7. Add apikey and styleUrl for project](/README.md#add-apikey-and-styleurl)

###  **I**. Add dependencies below to build.gradle module app
```gradle
    implementation "androidx.recyclerview:recyclerview:1.2.1"
    implementation "androidx.cardview:cardview:1,0,0"
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "com.google.android.gms:play-services-location:21.0.1"
    implementation "com.jakewharton:butterknife:10.2.3"
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'com.github.vietmap-company:maps-sdk-android:2.0.5'
    implementation 'com.github.vietmap-company:maps-sdk-navigation-ui-android:2.0.5'
    implementation 'com.github.vietmap-company:maps-sdk-navigation-android:2.0.4'
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
With older projects, place it to the **build.gradle file at module project**
```gradle
allprojects {
    repositories {
        google()
        maven { url "https://jitpack.io" }
    }
}
```
Upgrade the **compileSdk** and **targetSdk** to version **_34_**
```
compileSdk 34
```
```
targetSdk 34
```
Add the necessary permission request to the **AndroidManifest.xml** file

```xml
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```


## Request location permission in **MainActivity**
- Add the function to check location permission and push to the navigation activity
- Configure system speech engine to google, our SDK use system speech engine to speak out the instruction
```kotlin
class MainActivity : AppCompatActivity(), PermissionsListener {

    private var permissionsManager: PermissionsManager? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.pushToNavigationScreen)
        val ttsButton: Button = findViewById(R.id.testSpeech)
        val speechAgain: Button = findViewById(R.id.speechAgain)
        val intent = Intent(this, VietMapNavigationExampleV2::class.java)
        button.setOnClickListener {
            startActivity(intent)
            speechAgain.visibility = GONE
        }
        speechAgain.setOnClickListener { speakOut("Language: Vietnamese") }
        ttsButton.setOnClickListener {
            startActivity(Intent("com.android.settings.TTS_SETTINGS"))
            speechAgain.visibility = VISIBLE
        }
        permissionsManager = PermissionsManager(this)
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager!!.requestLocationPermissions(this)
        }

        textToSpeech = TextToSpeech(
            applicationContext
        ) { setTextToSpeechLanguage() }
    }


    private fun setTextToSpeechLanguage() {
        val language = Locale("vi", "VN")
        when (textToSpeech!!.setLanguage(language)) {
            TextToSpeech.LANG_MISSING_DATA -> {
                Toast.makeText(this, "Missing language data", Toast.LENGTH_LONG).show()
                return
            }
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                Toast.makeText(
                    this,
                    "Language not supported " + language.language,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            else -> {
                Toast.makeText(this, "Language: Vietnamese", Toast.LENGTH_LONG).show()
                speakOut("Language: Vietnamese")
            }
        }
    }

    private fun speakOut(speechContent: String) {
        val utteranceId: String = UUID.randomUUID().toString()
        textToSpeech!!.speak(speechContent, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(
            this, "This app needs location permissions in order to show its functionality.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
        } else {
            Toast.makeText(
                this, "You didn't grant location permissions.",
                Toast.LENGTH_LONG
            ).show()
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
        android:text="Navigation Screen"
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
        android:text="System text to speech setting"
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
        android:text="Speech again"
        android:visibility="gone"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.494"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/testSpeech"
        />
```

### **III**. Create a navigation activity

Create new **VietMapNavigationActivity**

Add below code to **xml** file of created **activity**

```xml

    <vn.vietmap.vietmapsdk.maps.MapView
        android:id="@+id/ktMapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:vietmap_cameraZoom="1"
        android:visibility="visible"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHeight_percent="1"/>


    <Button
        android:id="@+id/btnStopNavigation"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_gravity="end|bottom"
        android:layout_marginRight="10dp"
        android:layout_marginBottom="10dp"
        app:layout_constraintBottom_toTopOf="@id/btnOverview"
        android:onClick="btnStartStop_click"
        android:text="StopNavigation" />

    <Button
        android:id="@+id/btnOverview"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="start|bottom"
        android:layout_marginBottom="10dp"
        android:layout_marginRight="10dp"
        android:onClick="btnStartStop_click"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toTopOf="@id/btnRecenter"
        android:text="Overview" />

    <Button
        android:id="@+id/btnRecenter"
        android:layout_marginRight="10dp"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="start|bottom"
        android:layout_marginBottom="10dp"
        android:onClick="btnStartStop_click"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toTopOf="@id/btnStartNavigation"
        android:text="ReCenter" />
    <Button
        android:id="@+id/btnStartNavigation"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="center|bottom"
        android:layout_marginBottom="10dp"
        android:layout_marginRight="10dp"
        android:onClick="btnStartStop_click"
        android:text="StartNavigation"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
```

Activity needs to implement some of the following Listener classes to catch user events, navigation event and process them.

```kotlin
class VietMapNavigationExampleV2 : AppCompatActivity() , OnMapReadyCallback, ProgressChangeListener,
    OffRouteListener, MilestoneEventListener, NavigationEventListener, NavigationListener,
    FasterRouteListener, SpeechAnnouncementListener, BannerInstructionsListener, RouteListener,
    VietMapGL.OnMapLongClickListener, VietMapGL.OnMapClickListener,
    MapView.OnDidFinishRenderingMapListener{
    override fun onCreate(savedInstanceState: Bundle?) {

        // this function must be called before super.onCreate(savedInstanceState), otherwise the app will crash
        Vietmap.getInstance(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viet_map_navigation_example_v2)
    }
}
```
Import below packages to the activity
```kotlin

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.graphics.Color.blue
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.model.CurrentCenterPoint
import com.example.ultis.IconUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import vn.vietmap.services.android.navigation.ui.v5.camera.CameraOverviewCancelableCallback
import vn.vietmap.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.RouteListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import vn.vietmap.services.android.navigation.ui.v5.voice.NavigationSpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayerProvider
import vn.vietmap.services.android.navigation.v5.location.engine.LocationEngineProvider
import vn.vietmap.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine
import vn.vietmap.services.android.navigation.v5.milestone.Milestone
import vn.vietmap.services.android.navigation.v5.milestone.MilestoneEventListener
import vn.vietmap.services.android.navigation.v5.milestone.VoiceInstructionMilestone
import vn.vietmap.services.android.navigation.v5.navigation.*
import vn.vietmap.services.android.navigation.v5.navigation.camera.RouteInformation
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener
import vn.vietmap.services.android.navigation.v5.route.FasterRouteListener
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener
import vn.vietmap.services.android.navigation.v5.routeprogress.RouteProgress
import vn.vietmap.services.android.navigation.v5.snap.SnapToRoute
import vn.vietmap.services.android.navigation.v5.utils.RouteUtils
import vn.vietmap.vietmapsdk.Vietmap
import vn.vietmap.vietmapsdk.annotations.Marker
import vn.vietmap.vietmapsdk.annotations.MarkerOptions
import vn.vietmap.vietmapsdk.camera.CameraPosition
import vn.vietmap.vietmapsdk.camera.CameraUpdate
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory
import vn.vietmap.vietmapsdk.geometry.LatLng
import vn.vietmap.vietmapsdk.geometry.LatLngBounds
import vn.vietmap.vietmapsdk.location.LocationComponent
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions
import vn.vietmap.vietmapsdk.location.engine.LocationEngine
import vn.vietmap.vietmapsdk.location.modes.CameraMode
import vn.vietmap.vietmapsdk.location.modes.RenderMode
import vn.vietmap.vietmapsdk.maps.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
```
### Define below necessary variables 

```kotlin
    private var mapView: MapView? = null
    private var vietmapGL: VietMapGL? = null
    private var currentRoute: DirectionsRoute? = null
    private var routeClicked: Boolean = false
    private var locationEngine: LocationEngine? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var directionsRoutes: List<DirectionsRoute>? = null
    private val snapEngine = SnapToRoute()
    private var simulateRoute = false
    private var primaryRouteIndex = 0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var navigation: VietmapNavigation? = null
    private var speechPlayerProvider: SpeechPlayerProvider? = null
    private var speechPlayer: SpeechPlayer? = null
    private var routeProgress: RouteProgress? = null
    private val routeUtils = RouteUtils()
    private var voiceInstructionsEnabled = true
    private var isBuildingRoute = false
    private var origin = Point.fromLngLat(106.675789, 10.759050)
    private var destination = Point.fromLngLat(106.686777, 10.775056)
    private var locationComponent: LocationComponent? = null
    private var currentCenterPoint: CurrentCenterPoint? = null
    private var isOverviewing = false
    private var animateBuildRoute = true
    private var isNavigationInProgress = false
    private var isNavigationCanceled = false
    private var zoom = 20.0
    private var bearing = 0.0
    private var tilt = 0.0
    private var padding: IntArray = intArrayOf(150, 500, 150, 500)
    private var isRunning: Boolean = false
    private var options: VietMapGLOptions? = null
    private val navigationOptions =
        VietmapNavigationOptions.builder().build()
```

- You can find **CurrentCenterPoint** class in **model** folder of this project ([here](/app/src/main/java/com/example/vietmapandroidnavigationexamplev2/model/CurrentCenterPoint.kt))
### Call necessary function in **onCreate** callback
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Vietmap.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viet_map_navigation_example_v2)
        initLocationEngine()
        speechPlayerProvider = SpeechPlayerProvider(this, "vi", true);
        speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
        mapView = findViewById(R.id.ktMapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        options = VietMapGLOptions.createFromAttributes(this).compassEnabled(false)
        mapView = MapView(this, options)
        navigation = VietmapNavigation(
            this, navigationOptions, locationEngine!!
        )
        mapView!!.getMapAsync(this)
        val btnStopNavigation: Button = findViewById(R.id.btnStopNavigation)
        btnStopNavigation.setOnClickListener {
            finishNavigation()
        }
        val btnStartNavigation: Button = findViewById(R.id.btnStartNavigation)
        btnStartNavigation.setOnClickListener {
            startNavigation()
        }
        val btnOverview: Button = findViewById(R.id.btnOverview)
        btnOverview.setOnClickListener {
            overViewRoute()
        }
        val btnRecenter: Button = findViewById(R.id.btnRecenter)
        btnRecenter.setOnClickListener {
            recenter()
        }
    }
```
### Add some necessary function below
```kotlin

    // Overview the route on the map, show all the route on the map inside the screen
    private fun overViewRoute() {
        isOverviewing = true
        routeProgress?.let { showRouteOverview(padding, it) }
    }

    /// clear all the route in the map
    private fun clearRoute() {
        if (navigationMapRoute != null) {
            navigationMapRoute?.removeRoute()
        }
        currentRoute = null
    }
    
    /// init the map route and add some listener
    private fun initMapRoute() {
        if (vietmapGL != null) {
            /// add "vmadmin_province" layer to map to show the route below the roadname, this layer is provided by Vietmap tile map.
            navigationMapRoute = NavigationMapRoute(mapView!!, vietmapGL!!, "vmadmin_province")
        }

        /// callback when user click on another route, this function will be called and show the new route on the map
        navigationMapRoute?.setOnRouteSelectionChangeListener {
            routeClicked = true
            currentRoute = it
            val routePoints: List<Point> =
                currentRoute?.routeOptions()?.coordinates() as List<Point>
            animateVietmapGLForRouteOverview(padding, routePoints)
            primaryRouteIndex = try {
                it.routeIndex()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
            if (isRunning) {
                finishNavigation(isOffRouted = true)
                startNavigation()
            }
        }

        vietmapGL?.addOnMapClickListener(this)
    }

    /// stop current navigation 
    private fun finishNavigation(isOffRouted: Boolean = false) {

        zoom = 15.0
        bearing = 0.0
        tilt = 0.0
        isNavigationCanceled = true

        if (!isOffRouted) {
            isNavigationInProgress = false
            moveCameraToOriginOfRoute()
        }

        if (currentRoute != null) {
            isRunning = false
            navigation!!.stopNavigation()
            navigation!!.removeFasterRouteListener(this)
            navigation!!.removeMilestoneEventListener(this)
            navigation!!.removeNavigationEventListener(this)
            navigation!!.removeOffRouteListener(this)
            navigation!!.removeProgressChangeListener(this)
        }

    }

    private fun moveCameraToOriginOfRoute() {
        currentRoute?.let {
            try {
                val originCoordinate = it.routeOptions()?.coordinates()?.get(0)
                originCoordinate?.let {
                    val location = LatLng(originCoordinate.latitude(), originCoordinate.longitude())
                    moveCamera(location, null)
                }
            } catch (e: java.lang.Exception) {
                Timber.i(String.format("moveCameraToOriginOfRoute, %s", "Error: ${e.message}"))
            }
        }
    }

    /// move camera to a specific location with bearing
    private fun moveCamera(location: LatLng, bearing: Float?) {

        val cameraPosition = CameraPosition.Builder().target(location).zoom(zoom).tilt(tilt)

        if (bearing != null) {
            cameraPosition.bearing(bearing.toDouble())
        }

        var duration = 3000
        if (!animateBuildRoute) duration = 1
        vietmapGL?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition.build()), duration
        )
    }

    /// start navigation with current selected route
    private fun startNavigation() {
        tilt = 60.0
        zoom = 19.0
        isOverviewing = false
        isNavigationCanceled = false
        vietmapGL?.locationComponent?.cameraMode = CameraMode.TRACKING_GPS_NORTH

        if (currentRoute != null) {
            if (simulateRoute) {
                val mockLocationEngine = ReplayRouteLocationEngine()
                mockLocationEngine.assign(currentRoute)
                navigation!!.locationEngine = mockLocationEngine
            } else {
                locationEngine?.let {
                    navigation!!.locationEngine = it
                }
            }
            isRunning = true
            vietmapGL?.locationComponent?.locationEngine = null
            navigation!!.addNavigationEventListener(this)
            navigation!!.addFasterRouteListener(this)
            navigation!!.addMilestoneEventListener(this)
            navigation!!.addOffRouteListener(this)
            navigation!!.addProgressChangeListener(this)
            navigation!!.snapEngine = snapEngine
            currentRoute?.let {
                isNavigationInProgress = true
                navigation!!.startNavigation(currentRoute!!)
                recenter()
            }
        }
    }

    /// recenter the map to current location in the route
    private fun recenter() {
        isOverviewing = false
        if (currentCenterPoint != null) {
            moveCamera(
                LatLng(currentCenterPoint!!.latitude, currentCenterPoint!!.longitude),
                currentCenterPoint!!.bearing
            )
        }
    }

    /// init location engine to get current location of user
    /// mock location engine is used to simulate route, will used in demo and test case
    /// location engine provider is used to get current location of user, will used in real case
    private fun initLocationEngine() {
        locationEngine = if (simulateRoute) {
            ReplayRouteLocationEngine()
        } else {
            LocationEngineProvider.getBestLocationEngine(this)
        }
    }
```


### Implement some necessary function 

```kotlin
    /// this function will be called when map is ready to use, you can add some listener here to handle user interaction with map
    override fun onMapReady(p0: VietMapGL) {
        vietmapGL = p0
        vietmapGL!!.setStyle(
            Style.Builder()
                .fromUri("https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE")
        ) { style: Style? ->
            initLocationEngine()
            enableLocationComponent(style)
            initMapRoute()
        }
        vietmapGL!!.addOnMapClickListener(this)
        vietmapGL!!.addOnMapLongClickListener(this) 
    }

    /// init the location component to start tracking user location and show it on the map
    /// using location engine to get current location of user
    private fun enableLocationComponent(style: Style?) {
        locationComponent = vietmapGL!!.locationComponent
        if (locationComponent != null) {
            locationComponent!!.activateLocationComponent(
                LocationComponentActivationOptions.builder(
                    this, style!!
                ).build()
            )
            if (checkPermission()) {
                return
            }
            locationComponent!!.setCameraMode(
                CameraMode.TRACKING_GPS_NORTH, 750L, 18.0, 10000.0, 10000.0, null
            )
            locationComponent!!.isLocationComponentEnabled = true
            locationComponent!!.zoomWhileTracking(19.0)
            locationComponent!!.renderMode = RenderMode.GPS
            locationComponent!!.locationEngine = locationEngine
        }

    }

    override fun onMapClick(p0: LatLng): Boolean {
        addMarker(p0)
        // handle on Map click here
        return false
    }

    /// show route overview
    private fun showRouteOverview(padding: IntArray?, currentRouteProgress: RouteProgress) {
        val routeInformation: RouteInformation =
            buildRouteInformationFromProgress(currentRouteProgress)
        animateCameraForRouteOverview(routeInformation, padding!!)
    }

    private fun buildRouteInformationFromProgress(routeProgress: RouteProgress?): RouteInformation {
        return if (routeProgress == null) {
            RouteInformation.create(null, null, null)
        } else RouteInformation.create(routeProgress.directionsRoute(), null, null)
    }

    private fun animateCameraForRouteOverview(
        routeInformation: RouteInformation, padding: IntArray
    ) {
        val cameraEngine = navigation!!.cameraEngine
        val routePoints = cameraEngine.overview(routeInformation)
        if (routePoints.isNotEmpty()) {
            animateVietmapGLForRouteOverview(padding, routePoints)
        }
    }

    private fun animateVietmapGLForRouteOverview(padding: IntArray, routePoints: List<Point>) {
        if (routePoints.size <= 1) {
            return
        }
        val resetUpdate: CameraUpdate = buildResetCameraUpdate()
        val overviewUpdate: CameraUpdate = buildOverviewCameraUpdate(padding, routePoints)
        vietmapGL?.animateCamera(
            resetUpdate, 150,  CameraOverviewCancelableCallback(overviewUpdate, vietmapGL)
        )
    }


    private fun buildResetCameraUpdate(): CameraUpdate {
        val resetPosition: CameraPosition = CameraPosition.Builder().tilt(0.0).bearing(0.0).build()
        return CameraUpdateFactory.newCameraPosition(resetPosition)
    }


    private fun buildOverviewCameraUpdate(
        padding: IntArray, routePoints: List<Point>
    ): CameraUpdate {
        val routeBounds = convertRoutePointsToLatLngBounds(routePoints)
        return CameraUpdateFactory.newLatLngBounds(
            routeBounds, padding[0], padding[1], padding[2], padding[3]
        )
    }

    private fun convertRoutePointsToLatLngBounds(routePoints: List<Point>): LatLngBounds {
        val latLngs: MutableList<LatLng> = ArrayList()
        for (routePoint in routePoints) {
            latLngs.add(LatLng(routePoint.latitude(), routePoint.longitude()))
        }
        return LatLngBounds.Builder().includes(latLngs).build()
    }

    override fun onMilestoneEvent(p0: RouteProgress?, p1: String?, p2: Milestone?) {
        if (voiceInstructionsEnabled) {
            playVoiceAnnouncement(p2)
        }
        if (p0 != null && p2 != null) {
            if (routeUtils.isArrivalEvent(p0, p2) && isNavigationInProgress) {
                vietmapGL?.locationComponent?.locationEngine = locationEngine
                finishNavigation()
            }
        }
    }

    private fun playVoiceAnnouncement(milestone: Milestone?) {
        if (milestone is VoiceInstructionMilestone) {
            var announcement = SpeechAnnouncement.builder()
                .voiceInstructionMilestone(milestone as VoiceInstructionMilestone?).build()
            speechPlayer!!.play(announcement)
        }
    }

    override fun onRunning(p0: Boolean) {
        /// Handle navigation running events here
    }

    override fun onCancelNavigation() {
        /// Handle a navigation cancel event here
    }

    override fun onNavigationFinished() {
        /// Handle a navigation finished event here
    }

    override fun onNavigationRunning() {
        /// Handle a navigation running event here
    }

    override fun fasterRouteFound(p0: DirectionsRoute?) {
        p0?.let {
            currentRoute = p0
            finishNavigation()
            startNavigation()
        }
    }

    override fun willVoice(p0: SpeechAnnouncement?): SpeechAnnouncement {
        /// return null if you turn off voice instruction
        return p0!!
    }

    override fun willDisplay(p0: BannerInstructions?): BannerInstructions {
        /// return null if you turn off banner instruction
        return p0!!
    }

    override fun allowRerouteFrom(p0: Point?): Boolean {

        return true
    }


    override fun onOffRoute(offRoutePoint: Point?) {
        doOnNewRoute(offRoutePoint)
    }

    override fun onRerouteAlong(p0: DirectionsRoute?) {
        p0?.let {
            currentRoute = p0
            finishNavigation()
            startNavigation()
        }
    }

    override fun onFailedReroute(p0: String?) {
        // handle failed reroute here
    }

    override fun onArrival() {
        vietmapGL?.locationComponent?.locationEngine = locationEngine
        // handle arrival here
        println("You have arrived at your destination")
    }

    override fun onMapLongClick(latLng: LatLng): Boolean {
        getCurrentLocation()
        destination = Point.fromLngLat(latLng.longitude, latLng.latitude)
        if (origin != null) {
            fetchRouteWithBearing(false)
        }
        return false
    }

    override fun onDidFinishRenderingMap(p0: Boolean) {
        /// Handle did finish rendering map event here
    }


    private fun getCurrentLocation() {
        if (checkPermission()) {
            fusedLocationClient!!.lastLocation.addOnSuccessListener(
                this
            ) { location: Location? ->
                if (location != null) {
                    origin = Point.fromLngLat(
                        location.longitude, location.latitude
                    )
                }
            }
        }
    }


    private fun addMarker(position:LatLng ): Marker {
        return vietmapGL!!.addMarker(
            MarkerOptions()
                .position(position)
                .icon(
                    IconUtils().drawableToIcon(
                        this,
                        R.drawable.continue_straight,
                        ResourcesCompat.getColor(resources,R.color.blue , theme)
                    )
                )
        )
    }
    
    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } 
```
### Fetch route between 2 point, with bearing
```kotlin 
    private fun fetchRouteWithBearing(isStartNavigation: Boolean) {
        if (checkPermission()) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                this
            ) { location: Location? ->
                if (location != null) {
                    fetchRoute(isStartNavigation, location.bearing)
                }
            }
        } else {
            fetchRoute(isStartNavigation, null)
        }
    }


    private fun fetchRoute(isStartNavigation: Boolean, bearing: Float?) {
        val builder =
            NavigationRoute.builder(this).apikey("YOUR_API_KEY_HERE")
                .origin(origin, bearing?.toDouble(), bearing?.toDouble())
                .destination(destination, bearing?.toDouble(), bearing?.toDouble())
        builder.build().getRoute(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>
            ) {
                directionsRoutes = response.body()!!.routes()
                currentRoute = if (directionsRoutes!!.size <= primaryRouteIndex) {
                    directionsRoutes!![0]
                } else {
                    directionsRoutes!![primaryRouteIndex]
                }

                // Draw the route on the map
                if (navigationMapRoute != null) {
                    navigationMapRoute?.removeRoute()
                } else {
                    navigationMapRoute =
                        NavigationMapRoute(mapView!!, vietmapGL!!, "vmadmin_province")

                }

                //show multiple route to map
                if (response.body()!!.routes().size > 1) {
                    navigationMapRoute?.addRoutes(directionsRoutes!!)
                } else {
                    navigationMapRoute?.addRoute(currentRoute)
                }


                isBuildingRoute = false
                // get route point from current route
                val routePoints: List<Point> =
                    currentRoute?.routeOptions()?.coordinates() as List<Point>
                animateVietmapGLForRouteOverview(padding, routePoints)
                //Start Navigation again from new Point, if it was already in Progress
                if (isNavigationInProgress || isStartNavigation) {
                    startNavigation()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                isBuildingRoute = false

            }
        })
    }
```
### Some useful function
```kotlin
    /// this function will response to the progress change of navigation, which contain all of data while user is in navigation
    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {

        /// Get current speed of user by using location?.speed
        // location?.speed

        /*
        val bannerInstructionsList: List<BannerInstructions> =
            routeProgress.currentLegProgress().currentStep().bannerInstructions() as List<BannerInstructions>

        /// the modifier and type will guide you to the next turn direction

        currentModifier = bannerInstructionsList?.get(0)?.primary()?.modifier()
        currentModifierType= bannerInstructionsList?.get(0)?.primary()?.type()
        // val util = RouteUtils()
        // arrived = util.isArrivalEvent(routeProgress) && util.isLastLeg(routeProgress)
         
        
        /// You can get the distance remaining to destination by using
        distanceRemaining = routeProgress.distanceRemaining().toFloat()

        /// You can get the duration remaining to destination by using
        durationRemaining = routeProgress.durationRemaining()

        /// You can get the distance traveled by using
        distanceTraveled = routeProgress.distanceTraveled().toFloat()
        legIndex = routeProgress.currentLegProgress()?.stepIndex()
        // stepIndex = routeProgress.stepIndex
        val leg = routeProgress.currentLeg()
        if (leg != null)
            currentLeg = VietMapRouteLeg(leg)

        /// You can get the current step guide text by using
        currentStepInstruction = bannerInstructionsList?.get(0)
            ?.primary()
            ?.text()
        
        /// You can get the distance traveled from the last turn by using
        currentLegDistanceTraveled = routeProgress.currentLegProgress()?.distanceTraveled()?.toFloat()

        currentLegDistanceRemaining = routeProgress.currentLegProgress()?.distanceRemaining()?.toFloat()

        /// You can get the distance remaining to the next turn by using
        distanceToNextTurn = routeProgress.stepDistanceRemaining().toFloat()
         */
        if (!isNavigationCanceled && location != null && routeProgress != null) {
            try {
                val noRoutes: Boolean = directionsRoutes?.isEmpty() ?: true

                val newCurrentRoute: Boolean = !routeProgress!!.directionsRoute()
                    .equals(directionsRoutes?.get(primaryRouteIndex))
                val isANewRoute: Boolean = noRoutes || newCurrentRoute
                if (isANewRoute) {
                } else {

                    currentCenterPoint =
                        CurrentCenterPoint(location.latitude, location.longitude, location.bearing)

                    /// update the map camera to current location in realtime, if user is not overviews the route
                    if (!isOverviewing) {
                        this.routeProgress = routeProgress
                        moveCamera(LatLng(location.latitude, location.longitude), location.bearing)
                    }

                    /// snap the location of user to the route, which always show the location of user on the route
                    if (!isBuildingRoute) {
                        val snappedLocation: Location =
                            snapEngine.getSnappedLocation(location, routeProgress)
                        vietmapGL?.locationComponent?.forceLocationUpdate(snappedLocation)
                    }

                    if (simulateRoute && !isBuildingRoute) {
                        vietmapGL?.locationComponent?.forceLocationUpdate(location)
                    }

                }

                /// This function will calculate when the user is near the next turn, and make the map tilt to 0 degree, which help user easy to find the next turn
                handleProgressChange(location, routeProgress)
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun handleProgressChange(location: Location, routeProgress: RouteProgress) {
        val distanceRemainingToNextTurn =
            routeProgress.currentLegProgress()?.currentStepProgress()?.distanceRemaining()
        if (distanceRemainingToNextTurn != null && distanceRemainingToNextTurn < 30) {

            val resetPosition: CameraPosition =
                CameraPosition.Builder().tilt(0.0).zoom(17.0).build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(resetPosition)
            vietmapGL?.animateCamera(
                cameraUpdate, 1000
            )
        } else {
            if (routeProgress.currentLegProgress().currentStepProgress()
                    .distanceTraveled() > 30 && !isOverviewing
            ) {
                recenter()
            }
        }
    }

    override fun userOffRoute(location: Location?) {
        location?.let {
            if (checkIfUserOffRoute(it)) {
                speechPlayer!!.onOffRoute()
                doOnNewRoute(Point.fromLngLat(location.longitude, location.latitude))
            }
        }

    }

    private fun doOnNewRoute(offRoutePoint: Point?) {
        if (!isBuildingRoute) {
            isBuildingRoute = true
            offRoutePoint?.let {
                finishNavigation(isOffRouted = true)
                moveCamera(LatLng(it.latitude(), it.longitude()), null)
            }
            origin = offRoutePoint
            isNavigationInProgress = true
            fetchRouteWithBearing(false)
        }
    }

    private fun checkIfUserOffRoute(location: Location): Boolean {
        val snapLocation: Location = snapEngine.getSnappedLocation(location, routeProgress)
        val distance: Double = calculateDistanceBetween2Point(location, snapLocation)
        return distance > 30

    }

    private fun calculateDistanceBetween2Point(location1: Location, location2: Location): Double {
        /// this function calculate distance between 2 point in the earth, with planetary arc
        val radius = 6371000.0 // meters

        val dLat = (location2.latitude - location1.latitude) * PI / 180.0
        val dLon = (location2.longitude - location1.longitude) * PI / 180.0

        val a =
            sin(dLat / 2.0) * sin(dLat / 2.0) + cos(location1.latitude * PI / 180.0) * cos(location2.latitude * PI / 180.0) * sin(
                dLon / 2.0
            ) * sin(dLon / 2.0)
        val c = 2.0 * kotlin.math.atan2(sqrt(a), sqrt(1.0 - a))

        return radius * c
    }
```

### Add some additional functions
```kotlin

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume() 
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
        if (locationEngine != null) {
        }
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
        if (locationEngine != null) {
        }
    }
 
```


- All information about navigation will response in [_**onProgressChange**_](/README.md#onprogresschange-function-listen-as-the-user-moves-continuously-update-information-about-the-route-the-user-is-traveling-the-remaining-distance)

# Add **apikey** and  **styleUrl**
To ensure that the application does not crash when running, you need to add the **apikey** that VietMap provides at the **YOUR_API_KEY_HERE** keyword to use the SDK. You can get the **apikey** at [https://maps.vietmap.vn/](https://maps.vietmap.vn/)
