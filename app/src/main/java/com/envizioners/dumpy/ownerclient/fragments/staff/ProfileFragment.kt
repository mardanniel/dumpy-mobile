package com.envizioners.dumpy.ownerclient.fragments.staff

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ProfilePictureVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.StaffInfoVM
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), PermissionRequest.Listener {

    private lateinit var staffProfileImage: ImageView
    private lateinit var changeProfileButton: FloatingActionButton
    private lateinit var staffName: TextView
    private lateinit var staffAge: TextView
    private lateinit var staffContact: TextView
    private lateinit var staffEmail: TextView
    private lateinit var staffHiredDate: TextView
    private lateinit var staffWorkplace: TextView
    private lateinit var staffWorkplaceOwner: TextView
    private lateinit var loadingDialog: LoadingDialog

    private var profilePicUri: Uri? = null

    private var userID: Int = 0
    private var userEmail: String = ""
    private var employerID: Int = 0
    private val baseUrl = "https://dumpyph.com/uploads/js_staff/"
    private var folderPath = ""

    private val staffInfoVM: StaffInfoVM by lazy {
        ViewModelProvider(this).get(StaffInfoVM::class.java)
    }

    private val profilePictureVM: ProfilePictureVM by lazy {
        ViewModelProvider(this).get(ProfilePictureVM::class.java)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    private lateinit var getImage: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading profile...")
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            employerID = UserPreference.getUserEmployerID(requireContext(), "USER_EMPLOYER_ID")
            staffInfoVM.getStaffInfo(
                userID,
                employerID
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_s_profile, container, false)

        staffProfileImage = view.findViewById(R.id.f_s_profile_image)
        changeProfileButton = view.findViewById(R.id.f_s_change_profile_photo_btn)
        staffName = view.findViewById(R.id.f_s_profile_name)
        staffAge = view.findViewById(R.id.f_s_profile_age)
        staffContact = view.findViewById(R.id.f_s_profile_contact)
        staffEmail = view.findViewById(R.id.f_s_profile_email)
        staffHiredDate = view.findViewById(R.id.f_s_profile_hired_date)
        staffWorkplace = view.findViewById(R.id.f_s_profile_workplace)
        staffWorkplaceOwner = view.findViewById(R.id.f_s_profile_workplace_owner)

        staffInfoVM.staffInfoLD.observe(viewLifecycleOwner) { response ->

            userEmail = response.js_staff_email
            folderPath = if(response.js_staff_profile_picture == "DEFAULT-IMG.png" || response.js_staff_profile_picture.isEmpty()){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + response.js_staff_email + "/" + response.js_staff_profile_picture
            }
            Glide.with(requireActivity())
                .load(folderPath)
                .transform(CircleCrop())
                .into(staffProfileImage)

            staffName.text = "${response.js_staff_fname} ${response.js_staff_lname}"
            staffAge.text = response.js_staff_age
            staffContact.text = response.js_staff_contact
            staffEmail.text = response.js_staff_email
            staffHiredDate.text = response.js_staff_hired_date
            staffWorkplace.text = response.js_name
            staffWorkplaceOwner.text = "${response.js_owner_fname} ${response.js_owner_mname} ${response.js_owner_lname} "

            loadingDialog.dismissLoading()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        request.addListener(this)

        profilePictureVM.changePicResponse.observe(viewLifecycleOwner) { response ->
            var toastMessage = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully changed profile picture!"
                    Glide.with(requireActivity())
                        .load(baseUrl + userEmail + "/" +profilePicUri?.pathSegments?.last())
                        .transform(CircleCrop())
                        .into(staffProfileImage)
                }
                else -> {
                    toastMessage = "An unexpected error occurred while changing your profile picture. Please try again later."
                }
            }
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
            changeProfileButton.isEnabled = true
        }

        getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var imageUri = processFileUri(it.data?.data)
            if (imageUri != null){
                changeProfileButton.isEnabled = false
                profilePictureVM.changeStaffPic(
                    userID,
                    userEmail,
                    imageUri
                )
                profilePicUri = imageUri
            }else{
                Toast.makeText(
                    requireContext(),
                    "Selected image cannot be processed. Please try another image.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        changeProfileButton.setOnClickListener {
            if (request.checkStatus().allGranted()){
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
                    .setImageProviderInterceptor { imageProvider ->
                        Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                    }
                    .createIntent {
                        getImage.launch(it)
                    }
            }else{
                request.send()
            }
        }
    }

    private fun processFileUri(uri: Uri?): Uri? {
        var finalUriCaptured: Uri? = null
        if (uri != null && !uri.toString().startsWith("null")) {
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

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyGranted() -> {
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
                    .setImageProviderInterceptor { imageProvider ->
                        Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                    }
                    .createIntent {
                        getImage.launch(it)
                    }
            }
            result.allGranted() -> {
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
                    .setImageProviderInterceptor { imageProvider ->
                        Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                    }
                    .createIntent {
                        getImage.launch(it)
                    }
            }
            else -> {
                runDialog()
            }
        }
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

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as StaffActivity
        currActivity.updateActionBarTitle("Profile")
    }

}