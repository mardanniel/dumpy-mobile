package com.envizioners.dumpy.ownerclient.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.FormValidation
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerAuthVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecyclerLoginHandler : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var submitButton: Button
    private lateinit var signUpLink: TextView

    private val viewModel: RecyclerAuthVM by lazy {
        ViewModelProvider(this).get(RecyclerAuthVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_login, container, false)

        email = view.findViewById(R.id.r_input_login_email)
        password = view.findViewById(R.id.r_input_login_password)
        submitButton = view.findViewById(R.id.r_input_loginbtn)
        signUpLink = view.findViewById(R.id.r_signup_link)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recyclerLoginLD.observe(viewLifecycleOwner) { response ->
            val (toastMessage, pop) = FormValidation.handleLoginValidation(response.form_validation)
            if (pop){
                if(response.exist && response.validated){
                    Toast.makeText(activity, "Success: Login Successful", Toast.LENGTH_SHORT).show();
                    CoroutineScope(Dispatchers.IO).launch {
                        UserPreference.saveUserType(requireContext(), "USER_TYPE", "DUMPY_RECYCLER")
                        UserPreference.saveUserEmail(requireContext(), "USER_EMAIL", email.text.toString())
                        UserPreference.saveUserPassword(requireContext(), "USER_PASSWORD", password.text.toString())
                        UserPreference.saveUserID(requireContext(), "USER_ID", response.result?.user_id!!.toInt())
                        UserPreference.saveUserAuthorization(requireContext(), "USER_API_KEY", response.api_key!!)
                    }
                    var intent = Intent(activity, RecyclerActivity::class.java)
                    intent.putExtra("result", response.result)
                    startActivity(intent)
                    val sfm = requireActivity().supportFragmentManager
                    val count = sfm.backStackEntryCount
                    for(i in 0 until count){
                        sfm.popBackStack()
                    }
                    requireActivity().finishAffinity()
                }else if (response.exist && !response.validated) {
                    Toast.makeText(
                        activity,
                        "Account not activated! Check your email for verification",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }else{
                Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show()
            }
            submitButton.isEnabled = true
        }
        submitButton.setOnClickListener {
            submitButton.isEnabled = false

            var inputEmail = email.text.toString()
            var inputPassword = password.text.toString()

            if (!inputEmail.isNullOrEmpty() && !inputPassword.isNullOrEmpty()){

                viewModel.loginInput(inputEmail, inputPassword)

            }else if (inputEmail.isNullOrEmpty() || inputPassword.isNullOrEmpty()){
                Toast.makeText(
                    activity,
                    "Please enter one of the missing fields.",
                    Toast.LENGTH_SHORT
                ).show();
            }else {
                Toast.makeText(
                    activity,
                    "Please enter email and password.",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        signUpLink.setOnClickListener {
            var sfm = requireActivity().supportFragmentManager
            sfm.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.index_fragment_container, RecyclerRegisterHandler()).commit()
        }
    }
}