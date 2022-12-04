package com.envizioners.dumpy.ownerclient

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatMenuFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.DashboardFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ReportFragment
import com.envizioners.dumpy.ownerclient.fragments.staff.ConfirmationsQueue
import com.envizioners.dumpy.ownerclient.fragments.staff.ProfileFragment
import com.envizioners.dumpy.ownerclient.notification.FirebaseService
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLoginResultCredentials
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ActionBarTitleVM
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StaffActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null
    private var junkshopStaffDetails: AuthStaffLoginResultCredentials? = null

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

        junkshopStaffDetails = intent.extras!!.get("result") as AuthStaffLoginResultCredentials

        Log.i("Staff Details: ", junkshopStaffDetails.toString())
        drawer =  findViewById(R.id.drawer_layout)
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.staff_drawer_menu)

        // Navigation header
        var fullname = "${junkshopStaffDetails?.js_staff_fname} ${junkshopStaffDetails?.js_staff_lname}"
        val viewHeader = navigationView.getHeaderView(0)
        val navViewHeaderBinding = NavHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.apply {
            navHeaderTitle.text = fullname
            navHeaderSubtitle.text = "Staff"
            val baseUrl = "https://dumpyph.com/uploads/js_staff/"
            var folderPath = if(junkshopStaffDetails?.js_staff_profile_image == "DEFAULT-IMG.png" || junkshopStaffDetails?.js_staff_profile_image == null){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + junkshopStaffDetails?.js_staff_email + "/" + junkshopStaffDetails?.js_staff_profile_image
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
        navigationView.setCheckedItem(R.id.staff_nav_dashboard)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.staff_nav_dashboard -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment()).commit()
            }
            R.id.staff_nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
            R.id.staff_nav_trade_proposals -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TradeProposalsFragment()).commit()
            }
            R.id.staff_nav_confirmations_queue -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ConfirmationsQueue()).commit()
            }
            R.id.staff_nav_chat -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ChatMenuFragment()).commit()
            }
            R.id.staff_nav_report -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReportFragment()).commit()
            }
            R.id.staff_nav_logout -> {
                CoroutineScope(Dispatchers.Main).launch {
                    UserPreference.clearPreferences(this@StaffActivity)
                }
                startActivity(Intent(this@StaffActivity, IndexActivity::class.java))
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