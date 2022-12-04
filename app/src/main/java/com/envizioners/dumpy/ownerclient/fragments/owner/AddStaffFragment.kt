package com.envizioners.dumpy.ownerclient.fragments.owner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.owner_main.AddStaffResponse
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.AddStaffVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddStaffFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var fName: EditText
    private lateinit var lName: EditText
    private lateinit var uName: EditText
    private lateinit var contactNum: EditText
    private lateinit var age: EditText
    private lateinit var password: EditText
    private lateinit var confPassword: EditText
    private lateinit var addButton: Button
    private lateinit var backButton: Button

    private lateinit var loadingDialog: LoadingDialog

    private var userID: Int = 0

    private val addStaffVM: AddStaffVM by lazy {
        ViewModelProvider(this).get(AddStaffVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_o_add_staff, container, false)

        email = view.findViewById(R.id.f_s_as_email_field)
        fName = view.findViewById(R.id.f_s_as_fname_field)
        lName = view.findViewById(R.id.f_s_as_lname_field)
        uName = view.findViewById(R.id.f_s_as_uname_field)
        contactNum = view.findViewById(R.id.f_s_as_contact_field)
        age = view.findViewById(R.id.f_s_as_age_field)
        password = view.findViewById(R.id.f_s_as_password_field)
        confPassword = view.findViewById(R.id.f_s_as_confirm_password_field)
        addButton = view.findViewById(R.id.f_s_as_add)
        backButton = view.findViewById(R.id.f_s_as_back)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addButton.setOnClickListener {
            if (!email.text.isNullOrEmpty() && !fName.text.isNullOrEmpty() &&
                !lName.text.isNullOrEmpty() && !uName.text.isNullOrEmpty() &&
                !contactNum.text.isNullOrEmpty() && !age.text.isNullOrEmpty() &&
                !password.text.isNullOrEmpty() && !confPassword.text.isNullOrEmpty()){
                if (password.text.toString() == confPassword.text.toString()){
                    loadingDialog.setLoadingScreenText("Registering staff...")
                    loadingDialog.startLoading()
                    addStaffVM.addStaff(
                        email.text.toString(),
                        password.text.toString(),
                        confPassword.text.toString(),
                        contactNum.text.toString(),
                        uName.text.toString(),
                        fName.text.toString(),
                        lName.text.toString(),
                        age.text.toString().toInt(),
                        userID
                    )
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Confirm password doesn't match your password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Some fields are empty! I wonder what it is...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        addStaffVM.response.observe(viewLifecycleOwner) { response ->
            loadingDialog.dismissLoading()
            val (toastMessage, pop) = getFormValidationResponse(response)
            if (pop) {
                Toast.makeText(requireContext(), "Successfully registered staff!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }else{
                Toast.makeText(
                    requireContext(),
                    toastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFormValidationResponse(addStaffResp: AddStaffResponse): Pair<String, Boolean>{
        var mainString = "\n"
        if(addStaffResp.age.isNotEmpty()) mainString += addStaffResp.age + "\n"
        if(addStaffResp.confirm_pass.isNotEmpty()) mainString += addStaffResp.confirm_pass + "\n"
        if(addStaffResp.contact.isNotEmpty()) mainString += addStaffResp.contact + "\n"
        if(addStaffResp.email.isNotEmpty()) mainString += addStaffResp.email + "\n"
        if(addStaffResp.fname.isNotEmpty()) mainString += addStaffResp.fname + "\n"
        if(addStaffResp.lname.isNotEmpty()) mainString += addStaffResp.lname + "\n"
        if(addStaffResp.password.isNotEmpty()) mainString += addStaffResp.password + "\n"
        if(addStaffResp.username.isNotEmpty()) mainString += addStaffResp.username + "\n"
        return if (mainString.length > 2){
            Pair(mainString, false)
        }else{
            Pair(mainString, true)
        }
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as OwnerActivity
        currActivity.updateActionBarTitle("Add Staff")
    }
}