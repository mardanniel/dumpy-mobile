package com.envizioners.dumpy.ownerclient.fragments.shared

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.CreateTradeVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ReportVM
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportFragment : Fragment(), PermissionRequest.Listener {

    private lateinit var report_type: RadioGroup
    private lateinit var report_text: EditText
    private lateinit var upload_report_img_btn: Button
    private lateinit var report_img: ImageView
    private lateinit var submit_report: Button
    private lateinit var loadingDialog: LoadingDialog

    private var report_image_uri: Uri? = null
    private var sourceID = 0
    private var sourceRole = ""
    private var reportType = "bug_report"

    private val reportVM: ReportVM by lazy {
        ViewModelProvider(this).get(ReportVM::class.java)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            sourceID = UserPreference.getUserID(requireContext(), "USER_ID")
            val userType = UserPreference.getUserType(requireContext(), "USER_TYPE")
            sourceRole = if (userType == "DUMPY_RECYCLER"){
                "RECYCLER"
            }else if(userType == "DUMPY_OWNER"){
                "OWNER"
            }else{
                "STAFF"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_report, container, false)
        report_type = view.findViewById(R.id.f_report_report_type)
        report_text = view.findViewById(R.id.f_report_report_concern)
        report_img = view.findViewById(R.id.f_report_img)
        upload_report_img_btn = view.findViewById(R.id.f_report_upload_img_report)
        submit_report = view.findViewById(R.id.f_report_submit_report)
        loadingDialog = LoadingDialog(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reportVM.reportMLD.observe(viewLifecycleOwner) { response ->
            try {
                var toastMessage = when(response.result){
                    "REPORT_SUBMITTED_WITH_IMAGE" -> "Successfully sent your report with image!"
                    "REPORT_SUBMITTED" -> "Successfully sent your report!"
                    else -> "An unexpected error occurred please try again later."
                }
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            }catch (exception: Exception){
                Toast.makeText(
                    requireContext(),
                    "An unexpected error occurred. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("ReportFragment", exception.message!!)
            }
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
            loadingDialog.dismissLoading()
        }

        var getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var result = it.data?.data.toString()
            if(it.data?.data.toString() != null && !result.startsWith("null")){
                if (result.endsWith(".png") || result.endsWith(".jpg") || result.endsWith(".jpeg")){
                    Log.i("EndswithImageFileFormat", result)
                    report_img.setImageURI(it.data?.data)
                    report_image_uri = it.data?.data
                }else{
                    Log.i("DiffFileFormat", it.data?.data.toString())
                    var imagePath: String? = ""
                    var path: String = it.data?.data?.path.toString() // uri = any content Uri
                    val databaseUri: Uri
                    val selection: String?
                    val selectionArgs: Array<String>?
                    if ("/document/image:" in path || "/document/image%3A" in path) {
                        // files selected from "Documents"
                        databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        selection = "_id=?"
                        selectionArgs = arrayOf(DocumentsContract.getDocumentId(it.data?.data).split(":")[1])
                    } else {
                        databaseUri = it.data?.data!!
                        selection = null
                        selectionArgs = null
                    }
                    try {
                        val projection = arrayOf(MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.ORIENTATION,
                            MediaStore.Images.Media.DATE_TAKEN) // some example data you can query
                        val cursor = context?.contentResolver?.query(databaseUri,
                            projection, selection, selectionArgs, null)
                        if (cursor!!.moveToFirst()) {
                            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        }
                        cursor?.close()
                    }catch (exception: Exception){
                        Log.i("ImagePathFailed", exception.message!!)
                    }

                    var finalUri: Uri = Uri.parse(imagePath)

                    report_img.setImageURI(finalUri)
                    report_image_uri = finalUri
                }
            }
        }

        upload_report_img_btn.setOnClickListener {
            request.send()
            ImagePicker.Companion.with(this)
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
                    Log.d("ImagePicker", "Selected ImageProvider: "+imageProvider.name)
                }
                .createIntent {
                    getImage.launch(it)
                }
        }

        report_type.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.f_report_bug_option -> {
                    reportType = "bug_report"
                }
                R.id.f_report_user_option -> {
                    reportType = "user_report"
                }
            }
        }

        submit_report.setOnClickListener {
            if(!report_text.text.isNullOrEmpty()){
                loadingDialog.setLoadingScreenText("Sending your report")
                loadingDialog.startLoading()
                reportVM.insertTrade(
                    sourceID,
                    sourceRole,
                    report_text.text.toString().trim(),
                    reportType,
                    report_image_uri
                )
            }
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_SHORT).show()
            result.anyShouldShowRationale() -> {
                Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_SHORT).show()
                request.send()
            }
            result.allGranted() -> Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as RecyclerActivity).updateActionBarTitle("Report")
        }catch (exception: java.lang.Exception){
            try {
                (requireActivity() as OwnerActivity).updateActionBarTitle("Report")
            }catch (exception: java.lang.Exception){
                (requireActivity() as StaffActivity).updateActionBarTitle("Report")
            }
        }
    }
}