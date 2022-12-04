package com.envizioners.dumpy.ownerclient.fragments.owner

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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.owner.ProfileTradesListViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.recycler.ExchangePointsViewAdapter
import com.envizioners.dumpy.ownerclient.fragments.recycler.CreateTradeForm
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.PointsDetail
import com.envizioners.dumpy.ownerclient.utils.FragmentUtils
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.OwnerProfileVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ProfilePictureVM
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), PermissionRequest.Listener {

    private lateinit var profileImage: ImageView
    private lateinit var changeProfileButton: FloatingActionButton
    private lateinit var profileName: TextView
    private lateinit var profileAge: TextView
    private lateinit var profileNumber: TextView
    private lateinit var email: TextView
    private lateinit var establishmentName: TextView
    private lateinit var rating: TextView
    private lateinit var establistmentAddress: TextView
    private lateinit var tradeList: RecyclerView
    private lateinit var tradesListAction: Button
    private lateinit var businessPermit: ImageView
    private lateinit var sanitaryPermit: ImageView
    private lateinit var mayorResidence: ImageView
    private lateinit var ptlViewAdapter: ProfileTradesListViewAdapter
    private lateinit var loadingDialog: LoadingDialog

    private var profilePicUri: Uri? = null

    private lateinit var gson: Gson

    private val baseUrl = "https://dumpyph.com/uploads/js_owner/"

    private var folderPath = ""

    private val ownerprofileVM: OwnerProfileVM by lazy {
        ViewModelProvider(this).get(OwnerProfileVM::class.java)
    }

    private val profilePictureVM: ProfilePictureVM by lazy {
        ViewModelProvider(this).get(ProfilePictureVM::class.java)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    private lateinit var getImage: ActivityResultLauncher<Intent>

    private var userID: Int = 0
    private var userEmail: String = ""
    private var establishmentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        gson = Gson()
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            userEmail = UserPreference.getUserEmail(requireContext(), "USER_EMAIL")
            establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            ownerprofileVM.getProfileDetails(userID)
        }
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading your profile...")
        loadingDialog.startLoading()
        startPostponedEnterTransition()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileView = inflater.inflate(R.layout.f_o_profile, container, false)

        profileImage = profileView.findViewById(R.id.f_o_profile_image)
        changeProfileButton = profileView.findViewById(R.id.f_o_change_profile_photo_btn)
        profileName = profileView.findViewById(R.id.f_o_profile_name)
        profileAge = profileView.findViewById(R.id.f_o_profile_age)
        profileNumber = profileView.findViewById(R.id.f_o_profile_contact)
        email = profileView.findViewById(R.id.f_o_profile_email)
        establishmentName = profileView.findViewById(R.id.f_o_profile_establishment_name)
        rating = profileView.findViewById(R.id.f_o_profile_establishment_rating)
        establistmentAddress = profileView.findViewById(R.id.f_o_profile_establishment_address)
        tradeList = profileView.findViewById(R.id.f_o_profile_trades_list)
        tradesListAction = profileView.findViewById(R.id.f_o_profile_trades_list_action)
        businessPermit = profileView.findViewById(R.id.f_o_profile_business_permit)
        sanitaryPermit = profileView.findViewById(R.id.f_o_profile_sanitary_permit)
        mayorResidence = profileView.findViewById(R.id.f_o_profile_mayor_residence_permit)

        ownerprofileVM.profileResponse.observe(viewLifecycleOwner) { response ->
            folderPath =
                if (response.js_owner_profile_image == "DEFAULT-IMG.png" || response.js_owner_profile_image.isEmpty()) {
                    baseUrl + "DEFAULT-IMG.png"
                } else {
                    baseUrl + response.js_owner_email + "/" + response.js_owner_profile_image
                }

            Glide.with(requireActivity())
                .load(folderPath)
                .transform(CircleCrop())
                .into(profileImage)

            Glide.with(requireActivity())
                .load(baseUrl + response.js_owner_email + "/" + response.js_business_permit)
                .error(R.drawable.ic_broken_image)
                .into(businessPermit)

            Glide.with(requireActivity())
                .load(baseUrl + response.js_owner_email + "/" + response.js_sanitary_permit)
                .error(R.drawable.ic_broken_image)
                .into(sanitaryPermit)

            Glide.with(requireActivity())
                .load(baseUrl + response.js_owner_email + "/" + response.js_mayor_residence)
                .error(R.drawable.ic_broken_image)
                .into(mayorResidence)

            profileName.text = response.js_owner_name
            profileAge.text = response.js_owner_age.toString()
            profileNumber.text = response.js_owner_contact
            email.text = response.js_owner_email
            establishmentName.text = response.js_name
            rating.text = response.js_rating
            establistmentAddress.text = response.js_owner_address
            initRecyclerView(response.js_trades_list)

            tradesListAction.apply {
                this.text = if (response.js_trades_list.isEmpty()){
                    "Setup trade proposal"
                }else "Edit"
            }

            tradesListAction.setOnClickListener {
                ownerprofileVM.checkTradeProposalStatus(establishmentID!!)
                tradesListAction.isEnabled = false
            }

            ownerprofileVM.ctps.observe(viewLifecycleOwner) { resp ->
                when (resp){
                    101 -> {
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        val args = Bundle()
                        args.putString("tradesList", gson.toJson(response.js_trades_list))
                        val mtl = ManageTradesList()
                        mtl.arguments = args
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        transaction.replace(R.id.fragment_container, mtl).addToBackStack("profileFragment").commit()
                    }else -> {
                        Toast.makeText(
                            requireContext(),
                            "There are still existing trade proposals made by the recyclers in your establishment. Kindly clear all transactions before editing trade proposals.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                tradesListAction.isEnabled = true
            }
            loadingDialog.dismissLoading()

        }
        return profileView
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
            changeProfileButton.isEnabled = true
        }

        getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var imageUri = processFileUri(it.data?.data)
            if (imageUri != null){
                changeProfileButton.isEnabled = false
                profilePictureVM.changeOwnerPic(
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

    private fun initRecyclerView(tradesList: List<List<String>>){
        val recyclerView = tradeList
        recyclerView.layoutManager = LinearLayoutManager(activity)
        ptlViewAdapter = ProfileTradesListViewAdapter(tradesList)
        recyclerView.adapter = ptlViewAdapter
    }
}