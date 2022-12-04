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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.databinding.NavHeaderBinding
import com.envizioners.dumpy.ownerclient.fragments.junkshop.TradeProposalsFragment
import com.envizioners.dumpy.ownerclient.fragments.owner.*
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatMenuFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.DashboardFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.LeaderboardFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ReportFragment
import com.envizioners.dumpy.ownerclient.notification.FirebaseService
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLoginResult
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLoginResultJunkshop
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLoginResultCredentials
import com.envizioners.dumpy.ownerclient.utils.FragmentUtils
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ActionBarTitleVM
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OwnerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null
    private var authOwnerResult: AuthOwnerLoginResult? = null
    private var junkshopOwnerDetails: AuthOwnerLoginResultCredentials? = null
    private var junkshopDetails: AuthOwnerLoginResultJunkshop? = null

    private val viewModel: ActionBarTitleVM by lazy {
        ViewModelProvider(this).get(ActionBarTitleVM::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(applicationContext)
                .clearDiskCache()
        }

        CoroutineScope(Dispatchers.Main).launch {
            Glide.get(applicationContext)
                .clearMemory()
        }

        // Get intent from Login
        authOwnerResult = intent.extras!!.get("result") as AuthOwnerLoginResult
        junkshopOwnerDetails = authOwnerResult!!.owner
        junkshopDetails = authOwnerResult!!.junkshop

        // Initialize navigation drawer
        drawer =  findViewById(R.id.drawer_layout)
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.owner_drawer_menu)

        // Navigation header
        var fullname = "${junkshopOwnerDetails!!.jsOwnerFname} ${junkshopOwnerDetails!!.jsOwnerMname} ${junkshopOwnerDetails!!.jsOwnerLname}"
        val viewHeader = navigationView.getHeaderView(0)
        val navViewHeaderBinding = NavHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.apply {
            navHeaderTitle.text = fullname
            navHeaderSubtitle.text = "Business Owner"
            val baseUrl = "https://dumpyph.com/uploads/js_owner/"
            var folderPath = if(junkshopOwnerDetails?.jsOwnerProfileImage == "DEFAULT-IMG.png"){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + junkshopOwnerDetails?.jsOwnerEmail + "/" + junkshopOwnerDetails?.jsOwnerProfileImage
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
            val bundle = Bundle()
            val dashboard = DashboardFragment()
            val transaction = this.supportFragmentManager.beginTransaction()
            bundle.putString("header", junkshopDetails!!.js_name)
            dashboard.arguments = bundle
            transaction.replace(R.id.fragment_container, dashboard).commitNow()
        }

        viewModel.title.observe(this){ title ->
            supportActionBar?.title = title
        }

        viewModel.updateActionBarTitle("Dashboard")
        navigationView.setCheckedItem(R.id.owner_nav_dashboard)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.owner_nav_dashboard -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment()).commit()
            }
            R.id.owner_nav_profile -> {
                viewModel.updateActionBarTitle("Profile")
//                // Temporary Implementation, might change
//                val bundle = Bundle()
//                val profile = ProfileFragment()
//                val transaction = this.supportFragmentManager.beginTransaction()
//
//                var fullname = "${junkshopOwnerDetails!!.jsOwnerFname} ${junkshopOwnerDetails!!.jsOwnerMname} ${junkshopOwnerDetails!!.jsOwnerLname}"
//
//                bundle.putString("ownerFULLNAME", fullname)
//                bundle.putString("ownerBIRTHDATE", junkshopOwnerDetails!!.jsOwnerBirthdate)
//                bundle.putString("ownerADDRESS", junkshopOwnerDetails!!.jsOwnerAddress)
//                bundle.putString("ownerCONTACT1", junkshopOwnerDetails!!.jsOwnerContact)
//                bundle.putString("ownerCONTACT2", junkshopOwnerDetails!!.jsOwnerEmail)
//
//
//
//                profile.arguments = bundle
//                transaction.replace(R.id.fragment_container, ProfileFragment()).commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
            R.id.owner_nav_geofencing -> {
                viewModel.updateActionBarTitle("Geofencing")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, GeofencingFragment()).commit()
            }
            R.id.owner_nav_chat -> {
                viewModel.updateActionBarTitle("Chat")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ChatMenuFragment()).commit()
            }
            R.id.owner_nav_exchange_request -> {
                viewModel.updateActionBarTitle("Exchange Requests")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ExchangeRequestFragment()).commit()
            }
            R.id.owner_nav_manage_staff -> {
                viewModel.updateActionBarTitle("Manage Staff")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ManageStaffFragment()).commit()
            }
            R.id.owner_nav_trade_proposal -> {
                viewModel.updateActionBarTitle("Trade Proposals")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TradeProposalsFragment()).commit()
            }
            R.id.owner_nav_leaderboard -> {
                viewModel.updateActionBarTitle("Leaderboard")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LeaderboardFragment()).commit()
            }
            R.id.owner_nav_report -> {
                viewModel.updateActionBarTitle("Report")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReportFragment()).commit()
            }
            R.id.owner_nav_logout -> {
                CoroutineScope(Dispatchers.Main).launch {
                    UserPreference.clearPreferences(this@OwnerActivity)
                }
                startActivity(Intent(this@OwnerActivity, IndexActivity::class.java))
                finish()
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