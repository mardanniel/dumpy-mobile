package com.envizioners.dumpy.ownerclient

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.databinding.NavHeaderBinding
import com.envizioners.dumpy.ownerclient.fragments.owner.*
import com.envizioners.dumpy.ownerclient.fragments.recycler.*
import com.envizioners.dumpy.ownerclient.fragments.recycler.ProfileFragment
import com.envizioners.dumpy.ownerclient.fragments.recycler.TradeProposalsFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatMenuFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.DashboardFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.LeaderboardFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ReportFragment
import com.envizioners.dumpy.ownerclient.notification.FirebaseService
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerLoginResultCredentials
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ActionBarTitleVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.UDTVM
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecyclerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null
    private var authRecyclerDetails: AuthRecyclerLoginResultCredentials? = null

    private val viewModel: ActionBarTitleVM by lazy {
        ViewModelProvider(this).get(ActionBarTitleVM::class.java)
    }

    private val udtVM: UDTVM by lazy {
        ViewModelProvider(this).get(UDTVM::class.java)
    }

    private lateinit var loadingDialog: LoadingDialog

    private var userID: Int = 0
    private var userROLE: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

        loadingDialog = LoadingDialog(this)

        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(applicationContext)
                .clearDiskCache()
        }

        CoroutineScope(Dispatchers.Main).launch {
            Glide.get(applicationContext)
                .clearMemory()
        }

        // Notification Service
        FirebaseService.sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {
                    userID = UserPreference.getUserID(applicationContext, "USER_ID")
                    userROLE = UserPreference.getUserType(applicationContext, "USER_TYPE")
                    udtVM.repository.setUDT(
                        userID,
                        it,
                        userROLE
                    )
                }
                FirebaseService.token = it
                Log.i("FCM TOKEN: ", it)
            }
            .addOnFailureListener { e ->
                Log.i("FCM TOKEN: ", "Status: Failed")
                e.printStackTrace()
            }

        udtVM.isSet.observe(this) { response ->
            when(response){
                true -> Log.i("FCM TOKEN: ","Updated UDT")
                false -> Log.i("FCM TOKEN: ", "Failed to Update UDT")
            }
        }

        authRecyclerDetails = intent.extras!!.get("result") as AuthRecyclerLoginResultCredentials

        Log.i("Recycler Details: ", authRecyclerDetails.toString())
        drawer =  findViewById(R.id.drawer_layout)
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.recycler_drawer_menu)

        // Navigation header
        var fullname = "${authRecyclerDetails?.user_fname} ${authRecyclerDetails?.user_mname} ${authRecyclerDetails?.user_lname}"
        val viewHeader = navigationView.getHeaderView(0)
        val navViewHeaderBinding = NavHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.apply {
            navHeaderTitle.text = fullname
            navHeaderSubtitle.text = "Recycler"
            val baseUrl = "https://dumpyph.com/uploads/recycler/"
            var folderPath = if(authRecyclerDetails?.user_profile_image == "DEFAULT-IMG.png" || authRecyclerDetails?.user_profile_image.isNullOrEmpty()){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + authRecyclerDetails?.user_email + "/" + authRecyclerDetails?.user_profile_image
            }
            Glide.with(applicationContext)
                .load(folderPath)
                .transform(CircleCrop())
                .into(navHeaderImage)
        }

        // Navigation view listener
        navigationView.setNavigationItemSelectedListener(this)

        // Initialize actionbar (hamburger)
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        var toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment()).commit()
        }

        viewModel.title.observe(this){ title ->
            supportActionBar?.title = title
        }

        viewModel.updateActionBarTitle("Dashboard")
        navigationView.setCheckedItem(R.id.recycler_nav_dashboard)

        udtVM.isUnset.observe(this){ response ->
            if (response) {
                Log.i("Unset UDT", "Success")
            }else {
                Log.i("Unset UDT", "Failed")
            }
            CoroutineScope(Dispatchers.Main).launch {
                UserPreference.clearPreferences(this@RecyclerActivity)
            }
            loadingDialog.dismissLoading()
            startActivity(Intent(this@RecyclerActivity, IndexActivity::class.java))
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.recycler_nav_dashboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()
            }
            R.id.recycler_nav_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            }
            R.id.recycler_nav_geofencing -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SearchJunkshopFragment()).commit()
            }
            R.id.recycler_nav_trade_proposal -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, TradeProposalsFragment()).commit()
            }
            R.id.recycler_nav_chat -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChatMenuFragment()).commit()
            }
            R.id.recycler_nav_exchange_request -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ExchangePointsFragment()).commit()
            }
            R.id.recycler_nav_leaderboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LeaderboardFragment()).commit()
            }
            R.id.recycler_nav_report -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ReportFragment()).commit()
            }
            R.id.recycler_nav_logout -> {
                CoroutineScope(Dispatchers.Main).launch {
                    loadingDialog.setLoadingScreenText("Logging out...")
                    loadingDialog.startLoading()
                    udtVM.unsetUDT(
                        userID,
                        userROLE
                    )
                }
            }
        }
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)){
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun updateActionBarTitle(title: String){
        viewModel.updateActionBarTitle(title)
    }
}