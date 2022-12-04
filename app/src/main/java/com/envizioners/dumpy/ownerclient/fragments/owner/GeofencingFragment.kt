package com.envizioners.dumpy.ownerclient.fragments.owner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
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
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.viewmodels.owner.GeofencingVM
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

class GeofencingFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener
{

    // Components
    private lateinit var geofencingBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var geofencingLoading: ProgressBar

    private lateinit var geofencingActionsConstraint: ConstraintLayout

    private lateinit var geofencingStatus: TextView
    private lateinit var geofencingActionButton: Button
    private var isActionButtonTapped: Boolean = false

    private lateinit var geofencingLOCDETAILS: ConstraintLayout
    private lateinit var geofencingLAT: TextView
    private lateinit var geofencingLNG: TextView

    private lateinit var geofencingTOPACTIONS: ConstraintLayout
    private lateinit var geofencingCANCEL: Button
    private lateinit var geofencingGPS: Button
    private lateinit var geofencingONDRAGDETAILS: TextView
    private var isCancelTapped: Boolean = false

    private lateinit var geofencingMIDACTIONS: ConstraintLayout
    private lateinit var geofencingSUBMIT: Button

    private lateinit var geofencingBOTACTIONS: ConstraintLayout
    private lateinit var geofencingCHANGELOC: Button

    // Google Map
    private lateinit var googleMap: GoogleMap

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var fetchEstablishmentName: String? = null
    private var fetchedLat: Double? = null
    private var fetchedLng: Double? = null

    private var dragLat: Double? = null
    private var dragLng: Double? = null

    // Circle
    private var currLocCircle: Circle? = null
    private var nearestColor = Color.parseColor("#222F9F25")

    // Marker
    private var currLocMarker: Marker? = null

    // ViewModel
    private val geofencingVM: GeofencingVM by lazy {
        ViewModelProvider(this).get(GeofencingVM::class.java)
    }

    // Details
    private var userID: Int = 0
    private var establishmentID: Int = 0

    // Permission
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()
    private var isLocationPermissionOk: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            geofencingVM.getEstablishmentLocation(userID)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_geofencing, container, false)

        geofencingBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.f_o_geofencing_bottom_sheet))
        geofencingLoading = view.findViewById(R.id.f_o_geofencing_loading)
        geofencingActionsConstraint = view.findViewById(R.id.f_o_geofencing_actions)
        geofencingStatus = view.findViewById(R.id.f_o_geofencing_status)
        geofencingActionButton = view.findViewById(R.id.f_o_geofencing_setup)

        geofencingLOCDETAILS = view.findViewById(R.id.f_o_location_details_constraint)
        geofencingLAT = view.findViewById(R.id.f_o_geofencing_latitude)
        geofencingLNG = view.findViewById(R.id.f_o_geofencing_longitude)

        geofencingTOPACTIONS = view.findViewById(R.id.f_o_top_geofencing_action)
        geofencingCANCEL = view.findViewById(R.id.f_o_geofencing_cancel)
        geofencingGPS = view.findViewById(R.id.f_o_geofencing_gps)
        geofencingONDRAGDETAILS = view.findViewById(R.id.f_o_geofencing_marker_drag_details)

        geofencingMIDACTIONS = view.findViewById(R.id.f_o_mid_geofencing_action)
        geofencingSUBMIT = view.findViewById(R.id.f_o_geofencing_submit)
        geofencingBOTACTIONS = view.findViewById(R.id.f_o_bot_geofencing_action)
        geofencingCHANGELOC = view.findViewById(R.id.f_o_geofencing_change_location)

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionOk =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                if (isLocationPermissionOk)
                    setUpGoogleMap()
                else
                    Toast.makeText(requireContext(), "LocationPermDenied", Toast.LENGTH_SHORT)
                        .show()
            }

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.geofencing_map) as SupportMapFragment?)
        mapFragment?.getMapAsync(this)

        geofencingVM.saveEL.observe(viewLifecycleOwner) { response ->
            var shouldAct = false
            var toastMessage: String = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully updated location!"
                    shouldAct = true
                }
                102 -> {
                    toastMessage = "Failed to update location. Please try again later."
                }
                else -> {
                    toastMessage = "An unexpected error occurred. Please try again later."
                }
            }
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
//            #8BC34A
//            2F9F25
            if (shouldAct){
                if (currLocCircle == null){
                    geofencingStatus.text = "ON"
                    geofencingStatus.setTextColor(Color.parseColor("#8BC34A"))
                    geofencingActionButton.text = "Actions"
                    geofencingActionButton.setBackgroundColor(Color.parseColor("#2F9F25"))
                    currLocCircle = googleMap.addCircle(
                        CircleOptions().apply {
                            center(
                                LatLng(
                                    dragLat!!,
                                    dragLng!!
                                )
                            )
                            radius(
                                150.0
                            )
                            fillColor(
                                nearestColor
                            )
                            strokeWidth(
                                3f
                            )
                        }
                    )
                }
                currLocMarker?.position = LatLng(
                    dragLat!!,
                    dragLng!!
                )
                geofencingLAT.text = "Latitude: ${dragLat!!}"
                geofencingLNG.text = "Longitude: ${dragLng!!}"
                fetchedLat = dragLat!!
                fetchedLng = dragLng!!
            }else{
                currLocCircle?.center = LatLng(
                    fetchedLat!!,
                    fetchedLng!!
                )
                currLocMarker?.position = LatLng(
                    fetchedLat!!,
                    fetchedLng!!
                )
            }
            geofencingLoading.visibility = View.GONE
            geofencingActionsConstraint.visibility = View.VISIBLE
        }
        currLocMarker?.isDraggable = false
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

        with(googleMap){
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        12.8797,
                        121.7740
                    ), 5f
                )
            )
            uiSettings.isTiltGesturesEnabled = false
            setOnMarkerDragListener(this@GeofencingFragment)
            setPadding(0,0,0, pxToDp(geofencingBottomSheet.peekHeight))
        }
        setupCircle()
    }
    
    private fun pxToDp(px: Int): Int {
        return (px * resources.displayMetrics.density).toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun setupCircle(){
        geofencingVM.getEL.observe(viewLifecycleOwner) { response ->
            fetchEstablishmentName = response.js_name
            fetchedLat = response.js_latitude?.toDouble()
            fetchedLng = response.js_longtitude?.toDouble()

            // If LatLng != null
            fetchedLat.apply {
                if (this != null){
                    geofencingStatus.text = "ON"
                    geofencingActionButton.text = "Actions"
                    geofencingLAT.text = "Latitude: $this"
                    geofencingLNG.text = "Longitude $fetchedLng"

                    geofencingActionButton.setOnClickListener {
                        if (isActionButtonTapped){
                            geofencingBOTACTIONS.visibility = View.GONE
                        }else{
                            geofencingBOTACTIONS.visibility = View.VISIBLE
                        }
                        isActionButtonTapped = !isActionButtonTapped
                    }
                    geofencingCHANGELOC.setOnClickListener {
                        geofencingTOPACTIONS.visibility = View.VISIBLE
                        geofencingMIDACTIONS.visibility = View.VISIBLE
                        geofencingLOCDETAILS.visibility = View.GONE
                        geofencingBOTACTIONS.visibility = View.GONE
                        currLocMarker?.setIcon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                        currLocMarker?.isDraggable = true
                    }

                    geofencingCANCEL.setOnClickListener {
                        geofencingTOPACTIONS.visibility = View.GONE
                        geofencingMIDACTIONS.visibility = View.GONE
                        geofencingLOCDETAILS.visibility = View.VISIBLE
                        geofencingBOTACTIONS.visibility = View.VISIBLE
                        currLocMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_maps_google_establishment
                            )
                        )
                        currLocMarker?.isDraggable = false
                        currLocMarker?.position = LatLng(
                            fetchedLat!!,
                            fetchedLng!!
                        )
                        currLocCircle?.center = LatLng(
                            fetchedLat!!,
                            fetchedLng!!
                        )
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    fetchedLat!!,
                                    fetchedLng!!
                                ),
                                17.5f
                            )
                        )
                        try {
                            stopLocationUpdates()
                        }catch (exception: java.lang.Exception){
                            Log.i("LocationUpdates: ", exception.message!!)
                        }
                    }
                    enableGPSButton()
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                fetchedLat!!,
                                fetchedLng!!
                            ),
                            17.5f
                        )
                    )
                    currLocMarker =  googleMap.addMarker(
                        MarkerOptions().apply {
                            title(
                                fetchEstablishmentName
                            )
                            position(
                                LatLng(
                                    fetchedLat!!,
                                    fetchedLng!!
                                )
                            )
                            icon(
                                BitmapDescriptorFactory.fromResource(
                                    R.drawable.ic_maps_google_establishment
                                )
                            )
                        }
                    )
                    currLocCircle = googleMap.addCircle(
                        CircleOptions().apply {
                            center(
                                LatLng(
                                    fetchedLat!!,
                                    fetchedLng!!
                                )
                            )
                            radius(
                                150.0
                            )
                            fillColor(
                                nearestColor
                            )
                            strokeWidth(
                                3f
                            )
                        }
                    )
                }else{
                    geofencingStatus.text = "OFF"
                    geofencingStatus.setTextColor(Color.parseColor("#E53935"))
                    geofencingActionButton.text = "Setup Now"
                    geofencingActionButton.setBackgroundColor(Color.parseColor("#E53935"))
                    geofencingLAT.text = "Latitude: Not yet set!"
                    geofencingLNG.text = "Longitude: Not yet set!"
                    geofencingTOPACTIONS.visibility = View.VISIBLE
                    geofencingMIDACTIONS.visibility = View.VISIBLE
                    geofencingLOCDETAILS.visibility = View.GONE
                    geofencingBOTACTIONS.visibility = View.GONE
                    geofencingCANCEL.visibility = View.GONE
                    currLocMarker?.setIcon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        )
                    )
                    enableGPSButton()
                }
            }

            geofencingSUBMIT.setOnClickListener {
                if (dragLat != null){
                    geofencingTOPACTIONS.visibility = View.GONE
                    geofencingMIDACTIONS.visibility = View.GONE
                    geofencingActionsConstraint.visibility = View.GONE
                    geofencingBOTACTIONS.visibility = View.GONE
                    geofencingLOCDETAILS.visibility = View.VISIBLE
                    geofencingLoading.visibility = View.VISIBLE
                    currLocMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            R.drawable.ic_maps_google_establishment
                        )
                    )
                    currLocMarker?.isDraggable = false
                    geofencingVM.saveEstablishmentLocation(
                        establishmentID,
                        dragLat!!,
                        dragLng!!
                    )
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Unable to get last device location. Please try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            geofencingLoading.visibility = View.GONE
            geofencingActionsConstraint.visibility = View.VISIBLE
            geofencingBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun enableGPSButton(){
        geofencingGPS.setOnClickListener {
            setUpLocationUpdate()
        }
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
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("LocationUpdate: ", "Start ${task.result}")
            }
        }
        getUserLocation()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("TAG", "stopLocationUpdates: Location Update Stop")
    }

    private fun getUserLocation(){
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
                    Log.i("TaskLocRes", "Success")
                    if(it.result != null){
                        Log.i("TaskLocRes", "!Null")
                        location = it.result
                        if (currLocMarker == null){
                            Log.i("TaskLocRes", "Success")
                            currLocMarker =  googleMap.addMarker(
                                MarkerOptions().apply {
                                    title(
                                        "Your Location"
                                    )
                                    position(
                                        LatLng(
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                }
                            )
                            currLocMarker?.position = LatLng(
                                location.latitude,
                                location.longitude
                            )
                        }
                        currLocMarker?.setIcon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                        currLocMarker?.position = LatLng(
                            location.latitude,
                            location.longitude
                        )
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ),
                                17.5f
                            )
                        )
                        currLocCircle?.center = LatLng(
                            location.latitude,
                            location.longitude
                        )
                        dragLat = location.latitude
                        dragLng = location.longitude
                    }else{
                        geofencingTOPACTIONS.visibility = View.GONE
                        geofencingMIDACTIONS.visibility = View.GONE
                        geofencingLOCDETAILS.visibility = View.VISIBLE
                        geofencingBOTACTIONS.visibility = View.VISIBLE
                        currLocMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_maps_google_establishment
                            )
                        )
                        currLocMarker?.position = LatLng(
                            fetchedLat!!,
                            fetchedLng!!
                        )
                        try {
                            stopLocationUpdates()
                        }catch (exception: java.lang.Exception){
                            Log.i("LocationUpdates: ", exception.message!!)
                        }
                    }
                }
                currLocMarker?.isDraggable = true
            }
            stopLocationUpdates()
        } catch (exception: Exception){
            Log.i("mapException", exception.message.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMarkerDragStart(marker: Marker) {
        dragLat = marker.position.latitude
        dragLng = marker.position.longitude
        if (dragLat != null){
            geofencingONDRAGDETAILS.text = "Started dragging marker.\n(${dragLat}, ${dragLng})"
        }else{
            geofencingONDRAGDETAILS.text = ""
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMarkerDrag(marker: Marker) {
        geofencingONDRAGDETAILS.text = "Current marker position: (%f, %f)".format(
            marker.position.latitude,
            marker.position.longitude
        )
        currLocCircle?.center = LatLng(
            marker.position.latitude,
            marker.position.longitude
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onMarkerDragEnd(marker: Marker) {
        dragLat = marker.position.latitude
        dragLng = marker.position.longitude
        geofencingONDRAGDETAILS.text = "Drag the marker to the location of your establishment.\n(${dragLat}, ${dragLng})"
    }
}