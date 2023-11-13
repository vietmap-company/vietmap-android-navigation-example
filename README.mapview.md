# **VietMap Navigation Android SDK documentation**
## Table of contents
[1.  Gradle and AndroidManifest configure](/README.mapview.md#i-add-dependencies-below-to-buildgradle-module-app)

[2. Create mapview activity](/README.mapview.md#iii-create-a-mapview-activity)

[2. Add necessary variable to the activity](/README.mapview.md#add-necessary-variable-to-the-activity)

[3. Add necessary functions to onCreate function](/README.mapview.md#add-below-code-to-oncreated-function)

[4. Add a marker with standard window info](/README.mapview.md#add-a-marker-with-standard-info-window)

[5. Add a polyline](/README.mapview.md#add-polyline)

[5. Add a polygon](/README.mapview.md#add-a-polygon)

[6. Implement on map click listener](/README.mapview.md#implement-on-map-click-listener)

[7. Show user location to the map](/README.mapview.md#show-user-location-to-the-map)

[8. Add some necessary function](/README.mapview.md#add-some-necessary-function)

[9. Add apikey and styleUrl](/README.mapview.md#add-apikey)

###  **I**. Add dependencies below to build.gradle module app
```gradle
    implementation 'com.github.vietmap-company:maps-sdk-android:2.0.4'
    implementation 'com.github.vietmap-company:maps-sdk-plugin-localization-android:2.0.0'
    implementation 'com.github.vietmap-company:vietmap-services-geojson-android:1.0.0'
    implementation 'com.github.vietmap-company:vietmap-services-turf-android:1.0.2'
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.google.code.gson:gson:2.10.1'
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
- Add the necessary permission request to the **AndroidManifest.xml** file

```xml
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

### **III**. Create a mapview activity

Create new **VietMapMapViewActivity**

Add below code to **xml** file of created **activity**
```xml
    <vn.vietmap.vietmapsdk.maps.MapView
        android:id="@+id/vmMapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:vietmap_cameraZoom="1"
        android:visibility="visible"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHeight_percent="1"/>
```

### Add necessary variable to the activity
```kotlin

    private lateinit var mapView: MapView
    private lateinit var vietMapGL: VietMapGL

    private var polylines: MutableList<Polyline>? = null
    private var polylineOptions: ArrayList<PolylineOptions?>? = ArrayList()
    private var polygon: Polygon? = null
    
    companion object {
        private const val STATE_POLYLINE_OPTIONS = "polylineOptions"
        private val HOCHIMINH = LatLng(10.791257, 106.669189)
        private val NINHTHUAN = LatLng(11.550254, 108.960579)
        private val DANANG = LatLng(16.045746, 108.202241)
        private val HUE = LatLng(16.469602, 107.577462)
        private val NGHEAN = LatLng(18.932151, 105.577207)
        private val HANOI = LatLng(21.024696, 105.833099)
    }
```
### Add below code to **onCreated** function
```kotlin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viet_map_map_view_example_v2)

        if (savedInstanceState != null) {
            polylineOptions = savedInstanceState.getParcelableArrayList(STATE_POLYLINE_OPTIONS)
        } else {
            polylineOptions!!.addAll(allPolylines)
        }
        mapView = findViewById(R.id.vmMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { vietMapGL: VietMapGL ->
            this.vietMapGL = vietMapGL
            vietMapGL.setStyle(
                Style.Builder()
                    .fromUri("https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE")
            )
            vietMapGL.setOnPolylineClickListener { polyline: Polyline ->
                Toast.makeText(
                    this ,
                    "You clicked on polyline with id = " + polyline.id,
                    Toast.LENGTH_SHORT
                ).show()
            }

            polylines = vietMapGL.addPolylines(polylineOptions!!)

            polygon = vietMapGL.addPolygon(
                PolygonOptions()
                    .addAll(Config.STAR_SHAPE_POINTS)
                    .fillColor(Config.BLUE_COLOR)
            )

            vietMapGL?.addOnMapClickListener(this)
        }
    }
```
### Add a marker with standard info window
```kotlin
    private fun addMarker(position:LatLng ): Marker {
        return vietMapGL!!.addMarker(
            MarkerOptions()
                .position(position)
                .title("Vietmap")
                .snippet("Vietmap Android SDK")
                .icon(
                    IconUtils().drawableToIcon(
                        this,
                        R.drawable.ic_launcher_foreground,
                        ResourcesCompat.getColor(resources,R.color.black , theme)
                    )
                )
        )
    }
```

### Add polyline
```kotlin
    polylineOptions!!.addAll(allPolylines)
    polylines = vietMapGL.addPolylines(polylineOptions!!)

    private val allPolylines: List<PolylineOptions?>
        private get() {
            val options: MutableList<PolylineOptions?> = ArrayList()
            options.add(generatePolyline(HOCHIMINH, NINHTHUAN, "#F44336"))
            options.add(generatePolyline(NINHTHUAN, DANANG, "#FF5722"))
            options.add(generatePolyline(DANANG, HUE, "#673AB7"))
            options.add(generatePolyline(HUE, NGHEAN, "#009688"))
            options.add(generatePolyline(NGHEAN, HANOI, "#795548"))
            return options
        }

    private fun generatePolyline(start: LatLng, end: LatLng, color: String): PolylineOptions {
        val line = PolylineOptions()
        line.add(start)
        line.add(end)
        line.color(Color.parseColor(color))
        return line
    }
```

### Add a polygon
```kotlin
    val STAR_SHAPE_POINTS: ArrayList<LatLng?> = object : ArrayList<LatLng?>() {
        init {
            add(LatLng(10.791257, 106.669189))
            add(LatLng(11.550254, 108.960579))
            add(LatLng(16.045746, 108.202241))
            add(LatLng(16.469602, 107.577462))
        }
    }
    polygon = vietMapGL.addPolygon(
        PolygonOptions()
            .addAll(Config.STAR_SHAPE_POINTS)
            .fillColor(Color.parseColor("#3bb2d0"))
    )
```
### Implement on map click listener
```kotlin
    /// Make the activity implement OnMapClickListener from VietMapGL
    class VietMapMapViewExampleV2 : AppCompatActivity(),VietMapGL.OnMapClickListener{}
    
    /// Add map click listener to vietMapGL
    vietMapGL?.addOnMapClickListener(this)
    
    /// Handle onMapClick logic with clicked latLng response
    override fun onMapClick(latLng: LatLng): Boolean {
        addMarker(latLng)
        addCustomInfoWindowAdapter()
        return false
    }
```

### Show user location to the map
- Define necessary variable to start tracking user location
```kotlin
    private var locationComponent: LocationComponent? = null
    private var locationEngine: LocationEngine? = null
```
- Add below functions to the activity
```kotlin
    private fun enableLocationComponent(style: Style?) {
        locationComponent = vietMapGL!!.locationComponent
        if (locationComponent != null) {
            locationComponent!!.activateLocationComponent(
                LocationComponentActivationOptions.builder(
                    this, style!!
                ).build()
            )
            if (!checkPermission()) {
                return
            }
            locationComponent!!.setCameraMode(
                CameraMode.TRACKING_GPS_NORTH, 750L, 18.0, 0.0, 0.0, null
            )
            locationComponent!!.isLocationComponentEnabled = true
            locationComponent!!.zoomWhileTracking(19.0)
            locationComponent!!.renderMode = RenderMode.GPS
            locationComponent!!.locationEngine = locationEngine
        }
        updateMyLocationTrackingMode()
        updateMyLocationRenderMode()
    }

    private fun updateMyLocationTrackingMode() {
        val vietmapTrackingMode = intArrayOf(
            CameraMode.NONE,
            CameraMode.TRACKING,
            CameraMode.TRACKING_COMPASS,
            CameraMode.TRACKING_GPS
        )
        locationComponent!!.cameraMode = vietmapTrackingMode[0]
    }

    private fun updateMyLocationRenderMode() {
        val vietmapRenderModes = intArrayOf(RenderMode.NORMAL, RenderMode.COMPASS, RenderMode.GPS)
        locationComponent!!.renderMode = vietmapRenderModes[0]
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initLocationEngine() {
        locationEngine =
            LocationEngineProvider.getBestLocationEngine(this)
    }
```
- Add the **initLocationEngine** and **enableLocationComponent** functions to the callback of setStyle function
```kotlin
    vietMapGL.setStyle(
        Style.Builder()
            .fromUri("https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE")
    ){
        initLocationEngine()
        enableLocationComponent(it)
    }
```


### Add some necessary function
```kotlin
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
```
# Add **apikey**  
To ensure that the application does not crash when running, you need to add the **apikey** that VietMap provides at the **YOUR_API_KEY_HERE** keyword to use the SDK. You can get the **apikey** at [https://maps.vietmap.vn/](https://maps.vietmap.vn/)
