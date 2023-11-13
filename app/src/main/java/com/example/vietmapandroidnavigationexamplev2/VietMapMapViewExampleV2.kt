package com.example.vietmapandroidnavigationexamplev2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import vn.vietmap.services.android.navigation.v5.location.engine.LocationEngineProvider
import vn.vietmap.viet_navigation.util.IconUtils
import vn.vietmap.vietmapsdk.annotations.*
import vn.vietmap.vietmapsdk.geometry.LatLng
import vn.vietmap.vietmapsdk.location.LocationComponent
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions
import vn.vietmap.vietmapsdk.location.engine.LocationEngine
import vn.vietmap.vietmapsdk.location.modes.CameraMode
import vn.vietmap.vietmapsdk.location.modes.RenderMode
import vn.vietmap.vietmapsdk.maps.MapView
import vn.vietmap.vietmapsdk.maps.Style
import vn.vietmap.vietmapsdk.maps.VietMapGL

class VietMapMapViewExampleV2 : AppCompatActivity(),VietMapGL.OnMapClickListener {
    private lateinit var mapView: MapView
    private lateinit var vietMapGL: VietMapGL

    private var polylines: MutableList<Polyline>? = null
    private var polylineOptions: ArrayList<PolylineOptions?>? = ArrayList()

    private var locationComponent: LocationComponent? = null
    private var locationEngine: LocationEngine? = null
    private var polygon: Polygon? = null
    companion object {
        private const val STATE_POLYLINE_OPTIONS = "polylineOptions"
        private val HOCHIMINH = LatLng(10.791257, 106.669189)
        private val NINHTHUAN = LatLng(11.550254, 108.960579)
        private val DANANG = LatLng(16.045746, 108.202241)
        private val HUE = LatLng(16.469602, 107.577462)
        private val NGHEAN = LatLng(18.932151, 105.577207)
        private val HANOI = LatLng(21.024696, 105.833099)
        private const val FULL_ALPHA = 1.0f
        private const val PARTIAL_ALPHA = 0.5f
        private const val NO_ALPHA = 0.0f
    }


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
            ){
                initLocationEngine()
                enableLocationComponent(it)
            }
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
    private fun addCustomInfoWindowAdapter() {
        vietMapGL.infoWindowAdapter = object : VietMapGL.InfoWindowAdapter {
            private val tenDp = resources.getDimension(R.dimen.fab_margin_bottom).toInt()
            override fun getInfoWindow(marker: Marker): View? {
                val textView = TextView(this@VietMapMapViewExampleV2)
                textView.text = marker.title
                textView.setTextColor(Color.WHITE)
                textView.setPadding(tenDp, tenDp, tenDp, tenDp)
                return textView
            }
        }
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // activity uses singleInstance for testing purposes
                // code below provides a default navigation when using the app
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal object Config {
        val BLUE_COLOR = Color.parseColor("#3bb2d0")
        val RED_COLOR = Color.parseColor("#AF0000")
        const val FULL_ALPHA = 1.0f
        const val PARTIAL_ALPHA = 0.5f
        const val NO_ALPHA = 0.0f
        val STAR_SHAPE_POINTS: ArrayList<LatLng?> = object : ArrayList<LatLng?>() {
            init {
                add(LatLng(10.791257, 106.669189))
                add(LatLng(11.550254, 108.960579))
                add(LatLng(16.045746, 108.202241))
                add(LatLng(16.469602, 107.577462))
            }
        }
        val BROKEN_SHAPE_POINTS = STAR_SHAPE_POINTS.subList(0, STAR_SHAPE_POINTS.size - 3)

    }

    override fun onMapClick(p0: LatLng): Boolean {
        addMarker(p0)
        addCustomInfoWindowAdapter()
        return false
    }
}