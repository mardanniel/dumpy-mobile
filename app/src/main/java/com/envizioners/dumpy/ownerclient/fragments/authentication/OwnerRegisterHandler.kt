package com.envizioners.dumpy.ownerclient.fragments.authentication

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerRegister
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerRegister
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.OwnerAuthVM
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class OwnerRegisterHandler : Fragment(), PermissionRequest.Listener {

    // Owner Details
    private lateinit var email: EditText
    private lateinit var fname: EditText
    private lateinit var mname: EditText
    private lateinit var lname: EditText
    private lateinit var contact: EditText
    private lateinit var birthDate: DatePicker
    private lateinit var gender: RadioGroup
    private lateinit var street: EditText
    private lateinit var barangay: EditText
    private lateinit var city: EditText
    private lateinit var password: EditText
    private lateinit var confPassword: EditText

    private var selectedGender: String? = null

    // Establishment Info
    private lateinit var establishmentName: EditText
    private lateinit var establishmentStreet: EditText
    private lateinit var establishmentBarangay: EditText
    private lateinit var establishmentCity: EditText

    // Image and Buttons
    private lateinit var establishmentPhoto: ImageView
    private lateinit var establishmentPhotoUpload: FloatingActionButton
    private lateinit var establishmentBusinessPermit: ImageView
    private lateinit var establishmentBusinessPermitUpload: FloatingActionButton
    private lateinit var establishmentSanitaryPermit: ImageView
    private lateinit var establishmentSanitaryPermitUpload: FloatingActionButton
    private lateinit var establishmentMayorsResidence: ImageView
    private lateinit var establishmentMayorsResidenceUpload: FloatingActionButton

    // Image Uri
    private var establishmentPhotoURI: Uri? = null
    private var establishmentBusinessPermitURI: Uri? = null
    private var establishmentSanitaryPermitURI: Uri? = null
    private var establishmentMayorsResidenceURI: Uri? = null

    // Result Codes
    private val ESTABLISHMENT_PHOTO = 101
    private val BUSINESS_PERMIT = 102
    private val SANITARY_PERMIT = 103
    private val MAYORS_RESIDENCE = 104

    private var currCode: Int = 0

    // Actions
    private lateinit var submit: Button
    private lateinit var agree: CheckBox

    // Loading Dialog
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var currDate: Calendar

    private lateinit var getImage: ActivityResultLauncher<Intent>

    // Permissions
    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    private val ownerAuthVM: OwnerAuthVM by lazy {
        ViewModelProvider(this).get(OwnerAuthVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_o_register_handler, container, false)

        email = view.findViewById(R.id.f_o_register_email_field)
        fname = view.findViewById(R.id.f_o_register_fname_field)
        mname = view.findViewById(R.id.f_o_register_mname_field)
        lname = view.findViewById(R.id.f_o_register_lname_field)
        contact = view.findViewById(R.id.f_o_register_contact_field)
        birthDate = view.findViewById(R.id.f_o_register_birthdate_field)
        gender = view.findViewById(R.id.f_o_register_gender_field)
        street = view.findViewById(R.id.f_o_register_street_field)
        barangay = view.findViewById(R.id.f_o_register_barangay_field)
        city = view.findViewById(R.id.f_o_register_city_field)
        password = view.findViewById(R.id.f_o_register_password_field)
        confPassword = view.findViewById(R.id.f_o_register_confirm_password_field)

        establishmentName = view.findViewById(R.id.f_o_register_establishment_name_field)
        establishmentStreet = view.findViewById(R.id.f_o_register_establishment_street_field)
        establishmentBarangay = view.findViewById(R.id.f_o_register_establishment_barangay_field)
        establishmentCity = view.findViewById(R.id.f_o_register_establishment_city_field)

        establishmentPhoto = view.findViewById(R.id.f_o_register_establishment_photo)
        establishmentPhotoUpload = view.findViewById(R.id.f_o_register_establishment_photo_btn)
        establishmentBusinessPermit = view.findViewById(R.id.f_o_register_business_permit)
        establishmentBusinessPermitUpload = view.findViewById(R.id.f_o_register_business_permit_btn)
        establishmentSanitaryPermit = view.findViewById(R.id.f_o_register_sanitary_permit)
        establishmentSanitaryPermitUpload = view.findViewById(R.id.f_o_register_sanitary_permit_btn)
        establishmentMayorsResidence = view.findViewById(R.id.f_o_register_mayor_residence)
        establishmentMayorsResidenceUpload = view.findViewById(R.id.f_o_register_mayor_residence_btn)

        submit = view.findViewById(R.id.f_o_register_submit)
        agree = view.findViewById(R.id.f_o_register_agree)
        loadingDialog = LoadingDialog(requireActivity())

        currDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        request.addListener(this)

        getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when(currCode){
                ESTABLISHMENT_PHOTO -> {
                    establishmentPhoto.setImageURI(processFileUri(it.data?.data))
                    establishmentPhotoURI = processFileUri(it.data?.data)
                }
                BUSINESS_PERMIT -> {
                    establishmentBusinessPermit.setImageURI(processFileUri(it.data?.data))
                    establishmentBusinessPermitURI = processFileUri(it.data?.data)
                }
                SANITARY_PERMIT -> {
                    establishmentSanitaryPermit.setImageURI(processFileUri(it.data?.data))
                    establishmentSanitaryPermitURI = processFileUri(it.data?.data)
                }
                MAYORS_RESIDENCE -> {
                    establishmentMayorsResidence.setImageURI(processFileUri(it.data?.data))
                    establishmentMayorsResidenceURI = processFileUri(it.data?.data)
                }
            }
        }

        establishmentPhotoUpload.setOnClickListener {
            openImagePicker(ESTABLISHMENT_PHOTO)
        }

        establishmentBusinessPermitUpload.setOnClickListener {
            openImagePicker(BUSINESS_PERMIT)
        }

        establishmentSanitaryPermitUpload.setOnClickListener {
            openImagePicker(SANITARY_PERMIT)
        }

        establishmentMayorsResidenceUpload.setOnClickListener {
            openImagePicker(MAYORS_RESIDENCE)
        }

        gender.setOnCheckedChangeListener { _, i ->
            selectedGender = when(i){
                R.id.f_o_register_male_option -> "Male"
                else -> "Female"
            }
        }

        submit.setOnClickListener {
            if (!email.text.isNullOrEmpty() && !fname.text.isNullOrEmpty() &&
                !lname.text.isNullOrEmpty() && !mname.text.isNullOrEmpty() &&
                !contact.text.isNullOrEmpty() && !selectedGender.isNullOrEmpty() &&
                !street.text.isNullOrEmpty() && !barangay.text.isNullOrEmpty() &&
                !city.text.isNullOrEmpty() && !password.text.isNullOrEmpty() &&
                !confPassword.text.isNullOrEmpty() && !establishmentName.text.isNullOrEmpty() &&
                !establishmentStreet.text.isNullOrEmpty() && !establishmentBarangay.text.isNullOrEmpty() &&
                !establishmentCity.text.isNullOrEmpty() && establishmentPhotoURI != null &&
                establishmentBusinessPermitURI != null && establishmentMayorsResidenceURI != null &&
                establishmentMayorsResidenceURI != null){
                if (agree.isChecked){
                    if (currDate.get(Calendar.YEAR) - birthDate.year > 18){
                        if (validateEmail(email.text.toString())){
                            loadingDialog.setLoadingScreenText("Registering...")
                            loadingDialog.startLoading()
                            ownerAuthVM.registerOwner(
                                email.text.toString(),
                                fname.text.toString(),
                                mname.text.toString(),
                                lname.text.toString(),
                                contact.text.toString(),
                                "${birthDate.year}-${String.format("%02d", birthDate.month)}-${String.format("%02d", birthDate.dayOfMonth)}",
                                selectedGender!!,
                                street.text.toString(),
                                barangay.text.toString(),
                                city.text.toString(),
                                password.text.toString(),
                                confPassword.text.toString(),
                                "TRUE",
                                establishmentName.text.toString(),
                                establishmentStreet.text.toString(),
                                establishmentBarangay.text.toString(),
                                establishmentCity.text.toString(),
                                establishmentPhotoURI!!,
                                establishmentBusinessPermitURI!!,
                                establishmentSanitaryPermitURI!!,
                                establishmentMayorsResidenceURI!!
                            )
                        }else{
                            Toast.makeText(
                                requireContext(),
                                "Invalid email address.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

        ownerAuthVM.ownerRegisterMLD.observe(viewLifecycleOwner) { response ->
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
    }

    private fun openImagePicker(requestCode: Int){
        Log.i("RCTOBESENT", requestCode.toString())
        if (request.checkStatus().anyGranted() || request.checkStatus().allGranted()){
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .galleryMimeTypes(
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent {
                    currCode = requestCode
                    getImage.launch(it)
                }
        }else{
            request.send()
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.allGranted() -> {
                Log.i("Permissions", "All Granted!")
            }
            result.anyGranted() -> {
                Log.i("Permissions", result.toString())
            }
            else -> {
                runDialog()
            }
        }
    }

    private fun processFileUri(uri: Uri?): Uri? {
        var finalUriCaptured: Uri? = null
        if (uri != null) {
            val result = uri.toString()
            if (result.endsWith(".png") || result.endsWith(".jpg") || result.endsWith(".jpeg")) {
                Log.i("EndswithImageFileFormat", result)
                finalUriCaptured = uri
            } else {
                Log.i("DiffFileFormat", uri.toString())
                var imagePath: String? = ""
                var path: String = uri.path.toString() // uri = any content Uri
                val databaseUri: Uri
                val selection: String?
                val selectionArgs: Array<String>?
                if ("/document/image:" in path || "/document/image%3A" in path) {
                    databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    selection = "_id=?"
                    selectionArgs =
                        arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
                } else {
                    databaseUri = uri
                    selection = null
                    selectionArgs = null
                }
                try {
                    val projection = arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.ORIENTATION,
                        MediaStore.Images.Media.DATE_TAKEN
                    )
                    val cursor = context?.contentResolver?.query(
                        databaseUri,
                        projection, selection, selectionArgs, null
                    )
                    if (cursor!!.moveToFirst()) {
                        imagePath =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    }
                    cursor.close()
                } catch (exception: Exception) {
                    Log.i("ImagePathFailed", exception.message!!)
                }

                var finalUri: Uri = Uri.parse(imagePath)

                finalUriCaptured = finalUri
            }
        }

        return finalUriCaptured
    }

    private fun runDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Dumpy System")
            .setMessage("Permissions needs to be granted in order for this function to work.")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Go back") { dialog, _ ->
                dialog.cancel()
            }.create().show()
    }

    private fun validateEmail(field: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(field).matches()

    private fun getFormValidationResponse(registrationResponse: AuthOwnerRegister): Pair<String, Boolean>{
        var mainString = "\n"
        if(registrationResponse.email.isNotEmpty()) mainString += registrationResponse.email + "\n"
        if(registrationResponse.fname.isNotEmpty()) mainString += registrationResponse.fname + "\n"
        if(registrationResponse.mname.isNotEmpty()) mainString += registrationResponse.mname + "\n"
        if(registrationResponse.lname.isNotEmpty()) mainString += registrationResponse.lname + "\n"
        if(registrationResponse.contact.isNotEmpty()) mainString += registrationResponse.contact + "\n"
        if(registrationResponse.birthdate.isNotEmpty()) mainString += registrationResponse.birthdate + "\n"
        if(registrationResponse.gender.isNotEmpty()) mainString += registrationResponse.gender + "\n"
        if(registrationResponse.address_street.isNotEmpty()) mainString += registrationResponse.address_street + "\n"
        if(registrationResponse.address_brgy.isNotEmpty()) mainString += registrationResponse.address_brgy + "\n"
        if(registrationResponse.address_city.isNotEmpty()) mainString += registrationResponse.address_city + "\n"
        if(registrationResponse.password.isNotEmpty()) mainString += registrationResponse.password + "\n"
        if(registrationResponse.confPassword.isNotEmpty()) mainString += registrationResponse.confPassword + "\n"
        if(registrationResponse.agree.isNotEmpty()) mainString += registrationResponse.agree + "\n"
        if(registrationResponse.js_name.isNotEmpty()) mainString += registrationResponse.js_name + "\n"
        if(registrationResponse.js_address_street.isNotEmpty()) mainString += registrationResponse.js_address_street + "\n"
        if(registrationResponse.js_address_barangay.isNotEmpty()) mainString += registrationResponse.js_address_barangay + "\n"
        if(registrationResponse.js_address_city.isNotEmpty()) mainString += registrationResponse.js_address_city + "\n"
        if(registrationResponse.junkshop_photo.isNotEmpty()) mainString += registrationResponse.junkshop_photo + "\n"
        if(registrationResponse.business_permit.isNotEmpty()) mainString += registrationResponse.business_permit + "\n"
        if(registrationResponse.sanitary_permit.isNotEmpty()) mainString += registrationResponse.sanitary_permit + "\n"
        if(registrationResponse.mayor_residence.isNotEmpty()) mainString += registrationResponse.mayor_residence + "\n"
        return if (mainString.length > 2){
            Pair(mainString, false)
        }else{
            Pair(mainString, true)
        }
    }
}