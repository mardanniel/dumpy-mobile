package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.NearbyEstablishmentViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.recycler_main.EstablishmentsLocResultBody
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.SearchJunkshopVM
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchJunkshopFragment : Fragment(),
    OnMapReadyCallback,
    NearbyEstablishmentViewAdapter.ClickListener
{

    // Google Map
    private lateinit var googleMap: GoogleMap

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Circle Colors
    private var nearestColor = Color.parseColor("#222F9F25")
    private var farthestColor = Color.parseColor("#22259f7e")

    // Permission
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()
    private var isLocationPermissionOk: Boolean = false

    // Components
    private lateinit var searchJunkshopBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var toggleLocsBtn: Button
    private var toggleLocVal: Int = 1

    private var markersLocs: MutableList<Marker>? = null

    // ViewModel
    private val establishmentLoc: SearchJunkshopVM by lazy {
        ViewModelProvider(this).get(SearchJunkshopVM::class.java)
    }

    // Nearby Establishments Item Adapter
    private lateinit var nearbyEstablishmentViewAdapter: NearbyEstablishmentViewAdapter

    // Main View
    private lateinit var mainView: View

    private var recyclerID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            recyclerID = UserPreference.getUserID(requireContext(), "USER_ID")
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Setting up the map...")
        loadingDialog.startLoading()

        val searchEstablishmentView = inflater.inflate(R.layout.f_r_searchjunkshop, container, false)
        searchJunkshopBottomSheet = BottomSheetBehavior.from(searchEstablishmentView.findViewById(R.id.bs_layout))
        toggleLocsBtn = searchEstablishmentView.findViewById(R.id.nel_show_locations)
        mainView = searchEstablishmentView

        return searchEstablishmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Search")

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionOk =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

                if (isLocationPermissionOk){
                    setUpGoogleMap()
                }
                else{
                    Toast.makeText(requireContext(), "LocationPermDenied", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.search_junkshop_map) as SupportMapFragment?)
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Dumpy Location Permission")
                    .setMessage("In order for this feature to work, location permission needs to be granted.")
                    .setPositiveButton("Ok") { _, _ ->
                        requestLocation()
                    }.create().show()
            }
            else -> {
                requestLocation()
            }
        }
    }

    private fun requestLocation() {
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }

        applyUISettings()
        loadingDialog.setLoadingScreenText("Setting up location updates...")
        setUpLocationUpdate()
    }

    private fun applyUISettings(){
        with(googleMap){
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        12.8797,
                        121.7740
                    ),
                    5f
                )
            )
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            uiSettings.isTiltGesturesEnabled = false
            uiSettings.isScrollGesturesEnabled = false
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isZoomGesturesEnabled = false
            uiSettings.isRotateGesturesEnabled = false
            uiSettings.isIndoorLevelPickerEnabled = false
            setPadding(
                0,
                0,
                0,
                pxToDp(searchJunkshopBottomSheet.peekHeight) + 1
            )
        }
    }

    private fun pxToDp(px: Int): Int {
        return (px * resources.displayMetrics.density).toInt()
    }

    private fun setUpLocationUpdate() {

        locationRequest = LocationRequest.create()
        locationRequest.interval = 60000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                for (location in locationResult?.locations!!) {
                    Log.i("onLocationResult", " ${location.longitude} ${location.latitude}")
                }
            }
            override fun onLocationAvailability(locAvailability: LocationAvailability) {
                if(locAvailability.isLocationAvailable){
                    Log.i("onLocationAvailability", "Location Available!")
                }else{
                    Log.i("onLocationAvailability", "Location not Available!")
                }
            }
        }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("LocationUpdate: ", "Start ${task.result}")
            }
        }
        moveCameraToUserLocation()
    }

    private fun moveCameraToUserLocation(){
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isLocationPermissionOk = false
                return
            }

            var locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnCompleteListener {
                if(it.isSuccessful){
                    if(it.result != null){
                        location = it.result
                        setupMarkers(location)
                    }
                }
            }
        } catch (exception: Exception){
            Log.i("mapException", exception.message.toString())
        }
        loadingDialog.dismissLoading()
    }

    private fun setupMarkers(currLocation: Location){
        location = currLocation
        establishmentLoc.getNearest(location.latitude, location.longitude)
        establishmentLoc.searchLD.observe(viewLifecycleOwner) { response ->
            if (response.isNotEmpty()){
                toggleLocsBtn.apply {
                    this.visibility = View.VISIBLE
                    this.setOnClickListener {
                        if (toggleLocVal == 0){
                            searchJunkshopBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            this.text = "Show"
                            toggleLocVal = 1
                        }else{
                            searchJunkshopBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                            this.text = "Hide"
                            toggleLocVal = 0
                        }
                    }
                }
            }
            initMapList(response)
            changeBottomSheetHeader(response.size)
            response.forEach { item ->
                googleMap.addMarker(
                    MarkerOptions().apply {
                        title(item.js_name)
                        position(LatLng(item.js_latitude.toDouble(), item.js_longtitude.toDouble()))
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_google_establishment))
                    }
                )
            }
        }
        var latLng= LatLng(location.latitude, location.longitude)
        var latLngBounds = LatLngBounds(
            LatLng(location.latitude - 0.02, location.longitude - 0.02),
            LatLng(location.latitude + 0.02, location.longitude + 0.02)
        )
        with(googleMap){
            animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))
            setLatLngBoundsForCameraTarget(latLngBounds)
            addMarker(
                MarkerOptions().apply {
                    title("My location")
                    position(latLng)
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_google_user_location))
                }
            )
            // Max Search Range
            addCircle(
                CircleOptions().apply {
                    center(latLng)
                    radius(2000.0)
                    fillColor(nearestColor)
                    strokeWidth(3f)
                }
            )
            // Nearby Range
            addCircle(
                CircleOptions().apply {
                    center(latLng)
                    radius(150.0)
                    fillColor(farthestColor)
                    strokeWidth(3f)
                }
            )
            uiSettings.isScrollGesturesEnabled = true
        }
    }

    private fun initMapList(establishmentList: List<EstablishmentsLocResultBody>){
        val recyclerView = mainView.findViewById<RecyclerView>(R.id.nel_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        nearbyEstablishmentViewAdapter = NearbyEstablishmentViewAdapter(
            recyclerID,
            establishmentList,
            this,
            this.requireActivity().supportFragmentManager
        )
        recyclerView.adapter = nearbyEstablishmentViewAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun changeBottomSheetHeader(count: Int){
        val nelHeaderText = mainView.findViewById<TextView>(R.id.nel_header_text)
        val nelProgressBar = mainView.findViewById<ProgressBar>(R.id.nel_header_pb)

        nelHeaderText.text = "Found $count establishments!"
        nelProgressBar.visibility = View.GONE
    }

    override fun onItemClick(position: Int, lat: Double, lng: Double) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    lat,
                    lng
                )
            )
        )
        markersLocs?.get(position)?.showInfoWindow()
    }
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("TAG", "stopLocationUpdates: Location Update Stop")
    }
}