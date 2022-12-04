package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.recycler_main.RecyclerProfile
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerProfileVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ProfilePictureVM
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

    // Components
    private lateinit var profileImage: ImageView
    private lateinit var profileImageChangeBTN: FloatingActionButton
    private lateinit var profileName: TextView
    private lateinit var profileAge: TextView
    private lateinit var profileContact1: TextView
    private lateinit var profileContact2: TextView
    private lateinit var loadingDialog: LoadingDialog

    private var profilePicUri: Uri? = null

    private val baseUrl = "https://dumpyph.com/uploads/recycler/"

    private var folderPath = ""

    private var userID: Int = 0
    private var userEmail: String = ""

    private val rpVM: RecyclerProfileVM by lazy {
        ViewModelProvider(this).get(RecyclerProfileVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading your profile...")
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            rpVM.getUserInfo(userID)
        }
    }

    private val profilePictureVM: ProfilePictureVM by lazy {
        ViewModelProvider(this).get(ProfilePictureVM::class.java)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    private lateinit var getImage: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_profile, container, false)

        profileImage = view.findViewById(R.id.f_r_profile_image)
        profileImageChangeBTN = view.findViewById(R.id.f_r_change_profile_photo_btn)
        profileName = view.findViewById(R.id.f_r_profile_name)
        profileAge = view.findViewById(R.id.f_r_profile_age)
        profileContact1 = view.findViewById(R.id.f_r_profile_contact)
        profileContact2 = view.findViewById(R.id.f_r_profile_email)

        rpVM.recyclerInfo.observe(viewLifecycleOwner) { response ->

            userEmail = response.user_email

            folderPath = if(response.user_profile_image == "DEFAULT-IMG.png" || response.user_profile_image.isEmpty()){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + response.user_email + "/" + response.user_profile_image
            }

            Glide.with(requireActivity())
                .load(folderPath)
                .transform(CircleCrop())
                .into(profileImage)

            profileName.text = response.user_full_name
            profileAge.text = response.user_age
            profileContact1.text = response.user_contact
            profileContact2.text = response.user_email
            loadingDialog.dismissLoading()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Profile")

        request.addListener(this)

        profilePictureVM.changePicResponse.observe(viewLifecycleOwner) { response ->
            var toastMessage = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully changed profile picture!"
                    Glide.with(requireActivity())
                        .load(baseUrl + userEmail + "/" +profilePicUri?.pathSegments?.last())
                        .transform(CircleCrop())
                        .into(profileImage)
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
            profileImageChangeBTN.isEnabled = true
        }

        getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var imageUri = processFileUri(it.data?.data)
            if (imageUri != null){
                profileImageChangeBTN.isEnabled = false
                profilePictureVM.changeRecyclerPic(
                    userID,
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

        profileImageChangeBTN.setOnClickListener {

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
}