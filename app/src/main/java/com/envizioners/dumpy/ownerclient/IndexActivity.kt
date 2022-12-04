package com.envizioners.dumpy.ownerclient

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.fragments.authentication.OwnerLoginHandler
import com.envizioners.dumpy.ownerclient.fragments.authentication.RecyclerLoginHandler
import com.envizioners.dumpy.ownerclient.fragments.authentication.StaffLoginHandler
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.viewmodels.owner.OwnerAuthVM
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerAuthVM
import com.envizioners.dumpy.ownerclient.viewmodels.staff.StaffLoginVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class IndexActivity : AppCompatActivity() {

    private lateinit var indexMainPartProgressBar: ProgressBar
    private lateinit var indexMainPartButtonsLayout: ConstraintLayout
    private lateinit var recyclerRoute: Button
    private lateinit var ownerRoute: Button
    private lateinit var staffRoute: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index_activity)

        indexMainPartProgressBar = findViewById(R.id.index_main_part_progress_bar)
        indexMainPartButtonsLayout = findViewById(R.id.index_buttons_layout)
        recyclerRoute = findViewById(R.id.index_ownerbtn)
        ownerRoute = findViewById(R.id.index_recyclerbtn)
        staffRoute = findViewById(R.id.index_staffbtn)

        try {
            CoroutineScope(Dispatchers.Main).launch {

                val userType = UserPreference.getUserType(this@IndexActivity, "USER_TYPE")
                val userEmail = UserPreference.getUserEmail(this@IndexActivity, "USER_EMAIL")
                val userPassword = UserPreference.getUserPassword(this@IndexActivity, "USER_PASSWORD")
                Log.i("UserPreference: ", "${userType}, ${userEmail}. $userPassword")

                if (!userType.isNullOrEmpty()){
                    when(userType){
                        "DUMPY_OWNER" -> {
                            Log.i("TYPE: ", "${userType}")
                            try {
                                val viewModel: OwnerAuthVM = ViewModelProvider(this@IndexActivity).get(OwnerAuthVM::class.java)
                                viewModel.loginInput(
                                    userEmail,
                                    userPassword
                                )
                                viewModel.ownerLoginLD.observe(this@IndexActivity) { response ->
                                    Log.i("ResponseIndex: ", response.toString())
                                    if(response.exist && response.validated){
                                        Toast.makeText(this@IndexActivity, "Welcome Back, OWNER ${response.result?.owner?.jsOwnerFname}", Toast.LENGTH_SHORT).show();
                                        var intent: Intent = Intent(this@IndexActivity, OwnerActivity::class.java)
                                        intent.putExtra("result", response.result)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }catch (exception: Exception){
                                Log.i("DataStore: ",exception.toString())
                            }
                        }
                        "DUMPY_RECYCLER" -> {
                            try {
                                val viewModel: RecyclerAuthVM = ViewModelProvider(this@IndexActivity).get(RecyclerAuthVM::class.java)
                                viewModel.loginInput(
                                    userEmail,
                                    userPassword
                                )
                                viewModel.recyclerLoginLD.observe(this@IndexActivity) { response ->
                                    if(response.exist && response.validated){
                                        Toast.makeText(this@IndexActivity, "Welcome Back, RECYCLER ${response.result?.user_fname}", Toast.LENGTH_SHORT).show();
                                        var intent: Intent = Intent(this@IndexActivity, RecyclerActivity::class.java)
                                        intent.putExtra("result", response.result)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }catch (exception: Exception){
                                Log.i("DataStore: ",exception.toString())
                            }
                        }
                        "DUMPY_STAFF" -> {
                            try {
                                val viewModel: StaffLoginVM = ViewModelProvider(this@IndexActivity).get(StaffLoginVM::class.java)
                                viewModel.loginInput(
                                    userEmail,
                                    userPassword
                                )
                                viewModel.staffLoginLD.observe(this@IndexActivity) { response ->
                                    if(response.exist){
                                        Toast.makeText(this@IndexActivity, "Welcome Back, STAFF ${response.result?.js_staff_fname}", Toast.LENGTH_SHORT).show();
                                        var intent: Intent = Intent(this@IndexActivity, StaffActivity::class.java)
                                        intent.putExtra("result", response.result)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }catch (exception: Exception){
                                Log.i("DataStore: ",exception.toString())
                            }
                        }
                    }
                }else{
                    Log.i("Index: ", "Aww Man!")
                }
            }
        }catch (exception: Exception){
            Toast.makeText(applicationContext, "An unexpected error occured. Please restart the application.", Toast.LENGTH_SHORT).show()
            Log.i("Exception", exception.message.toString())
        }

        recyclerRoute.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.index_fragment_container, OwnerLoginHandler()).commit()
        }

        ownerRoute.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.index_fragment_container, RecyclerLoginHandler()).commit()
        }

        staffRoute.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.index_fragment_container, StaffLoginHandler()).commit()
        }

        indexMainPartProgressBar.visibility = View.GONE
        indexMainPartButtonsLayout.visibility = View.VISIBLE
    }
}