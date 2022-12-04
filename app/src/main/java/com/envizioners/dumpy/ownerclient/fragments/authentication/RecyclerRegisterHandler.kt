package com.envizioners.dumpy.ownerclient.fragments.authentication

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerRegister
import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLoginResultCredentials
import com.envizioners.dumpy.ownerclient.response.owner_main.AddStaffResponse
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerAuthVM
import java.util.*

//    https://dumpyph.com/api/auth/recycler/register

class RecyclerRegisterHandler : Fragment() {

    private lateinit var email: EditText
    private lateinit var fname: EditText
    private lateinit var mname: EditText
    private lateinit var lname: EditText
    private lateinit var contact: EditText
    private lateinit var birthDate: DatePicker
    private lateinit var street: EditText
    private lateinit var barangay: EditText
    private lateinit var city: EditText
    private lateinit var password: EditText
    private lateinit var confPassword: EditText
    private lateinit var submit: Button
    private lateinit var agree: CheckBox
    private lateinit var agreeText: TextView
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var currDate: Calendar

    private val recyclerAuthVM: RecyclerAuthVM by lazy {
        ViewModelProvider(this).get(RecyclerAuthVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_register_handler, container, false)

        email = view.findViewById(R.id.f_r_register_email_field)
        fname = view.findViewById(R.id.f_r_register_fname_field)
        mname = view.findViewById(R.id.f_r_register_mname_field)
        lname = view.findViewById(R.id.f_r_register_lname_field)
        contact = view.findViewById(R.id.f_r_register_contact_field)
        birthDate = view.findViewById(R.id.f_r_register_birthdate_field)
        street = view.findViewById(R.id.f_r_register_street_field)
        barangay = view.findViewById(R.id.f_r_register_barangay_field)
        city = view.findViewById(R.id.f_r_register_city_field)
        password = view.findViewById(R.id.f_r_register_password_field)
        confPassword = view.findViewById(R.id.f_r_register_confirm_password_field)
        submit = view.findViewById(R.id.f_r_register_submit)
        agree = view.findViewById(R.id.f_r_register_agree)
        agreeText = view.findViewById(R.id.f_r_register_checkbox_text)

        currDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAuthVM.recyclerRegisterMLD.observe(viewLifecycleOwner) { response ->
            loadingDialog.dismissLoading()
            val (toastMessage, pop) = getFormValidationResponse(response)
            if (pop) {
                Toast.makeText(requireContext(), "You have successfully registered! Please check your email for verification!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }else{
                Toast.makeText(
                    requireContext(),
                    toastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        submit.setOnClickListener {
            if (!email.text.isNullOrEmpty() && !fname.text.isNullOrEmpty() &&
                !lname.text.isNullOrEmpty() && !mname.text.isNullOrEmpty() &&
                !contact.text.isNullOrEmpty() && !street.text.isNullOrEmpty() &&
                !barangay.text.isNullOrEmpty() && !city.text.isNullOrEmpty() &&
                !password.text.isNullOrEmpty() && !confPassword.text.isNullOrEmpty()){
                if (agree.isChecked){
                    if (currDate.get(Calendar.YEAR) - birthDate.year > 18){
                        loadingDialog.setLoadingScreenText("Registering...")
                        loadingDialog.startLoading()
                        recyclerAuthVM.register(
                            email.text.toString(),
                            fname.text.toString(),
                            mname.text.toString(),
                            lname.text.toString(),
                            contact.text.toString(),
                            "${birthDate.year}-${String.format("%02d", birthDate.month)}-${String.format("%02d", birthDate.dayOfMonth)}",
                            street.text.toString(),
                            barangay.text.toString(),
                            city.text.toString(),
                            password.text.toString(),
                            confPassword.text.toString(),
                           "TRUE"
                        )
                    }else{
                        Toast.makeText(
                            requireContext(),
                            "You must be 18 and above to be able to register.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    Toast.makeText(
                        requireContext(),
                        "In order to proceed with the registration, we must ensure first that you agree with the terms and conditions and privacy policy.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Some fields are empty!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFormValidationResponse(registrationResponse: AuthRecyclerRegister): Pair<String, Boolean>{
        var mainString = "\n"
        if(registrationResponse.email.isNotEmpty()) mainString += registrationResponse.email + "\n"
        if(registrationResponse.fname.isNotEmpty()) mainString += registrationResponse.fname + "\n"
        if(registrationResponse.mname.isNotEmpty()) mainString += registrationResponse.mname + "\n"
        if(registrationResponse.lname.isNotEmpty()) mainString += registrationResponse.lname + "\n"
        if(registrationResponse.contact.isNotEmpty()) mainString += registrationResponse.contact + "\n"
        if(registrationResponse.birthdate.isNotEmpty()) mainString += registrationResponse.birthdate + "\n"
        if(registrationResponse.address_street.isNotEmpty()) mainString += registrationResponse.address_street + "\n"
        if(registrationResponse.address_brgy.isNotEmpty()) mainString += registrationResponse.address_brgy + "\n"
        if(registrationResponse.address_city.isNotEmpty()) mainString += registrationResponse.address_city + "\n"
        if(registrationResponse.password.isNotEmpty()) mainString += registrationResponse.password + "\n"
        if(registrationResponse.confirm_password.isNotEmpty()) mainString += registrationResponse.confirm_password + "\n"
        if(registrationResponse.agree.isNotEmpty()) mainString += registrationResponse.agree + "\n"
        return if (mainString.length > 2){
            Pair(mainString, false)
        }else{
            Pair(mainString, true)
        }
    }
}