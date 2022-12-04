package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.Manifest
import android.R.attr.data
import android.app.Activity
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
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.tradeproposal.CreateTradeItem
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CreateTradeForm : Fragment(), PermissionRequest.Listener {

    private lateinit var spinner: Spinner
    private lateinit var itemQuantity: EditText
    private lateinit var itemQuantityLabel: TextView
    private lateinit var uploadedImage: ImageView
    private var uploadedImageURI: Uri? = null
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var uploadImageButton: FloatingActionButton
    private lateinit var tradesList: List<List<String>>
    private lateinit var getImage: ActivityResultLauncher<Intent>

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tradesListString = arguments?.getString("tradesList")!!
        var gson = Gson()
        val type = object : TypeToken<List<List<String>>>() {}.type
        tradesList = gson.fromJson(tradesListString, type)
        Log.i("Parcel (CTFragment): ", tradesList.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val createTradeForm = inflater.inflate(R.layout.f_r_trade_form, container, false)

        val itemsList = mutableListOf<String>()
        val quantityLabels = mutableListOf<String>()

        tradesList.forEach{
            itemsList.add("${it[0]} (${it[1]})")
            quantityLabels.add(it.last())
        }

        spinner = createTradeForm.findViewById(R.id.trade_form_items_list)
        itemQuantity = createTradeForm.findViewById(R.id.trade_form_quantity)
        itemQuantityLabel = createTradeForm.findViewById(R.id.trade_form_quantity_label)
        uploadedImage = createTradeForm.findViewById(R.id.trade_form_image_file)
        submitButton = createTradeForm.findViewById(R.id.trade_form_submit)
        cancelButton = createTradeForm.findViewById(R.id.trade_form_cancel)
        uploadImageButton = createTradeForm.findViewById(R.id.trade_image_file_upload_button)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            itemsList
        )

        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemQuantityLabel?.text = quantityLabels[position]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        return createTradeForm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        request.addListener(this)
        getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var result = it.data?.data.toString()
            if (it.data?.data.toString() != null && !result.startsWith("null")) {
                if (result.endsWith(".png") || result.endsWith(".jpg") || result.endsWith(".jpeg")) {
                    Log.i("EndswithImageFileFormat", result)
                    uploadedImage.setImageURI(it.data?.data)
                    uploadedImageURI = it.data?.data
                } else {
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
                        selectionArgs =
                            arrayOf(DocumentsContract.getDocumentId(it.data?.data).split(":")[1])
                    } else { // files selected from all other sources, especially on Samsung devices
                        databaseUri = it.data?.data!!
                        selection = null
                        selectionArgs = null
                    }
                    try {
                        val projection = arrayOf(
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.ORIENTATION,
                            MediaStore.Images.Media.DATE_TAKEN
                        ) // some example data you can query
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

                    uploadedImage.setImageURI(finalUri)
                    uploadedImageURI = finalUri
                }
            }
        }
        uploadImageButton.setOnClickListener {
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
        submitButton.setOnClickListener {
            addItemToMainList()
        }

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addItemToMainList(){
        if(!itemQuantity.text.isNullOrEmpty() && uploadedImageURI != null){
            var selectedItemPos = spinner.selectedItemPosition
            var transaction = activity?.supportFragmentManager
            transaction?.setFragmentResult(
                "create_trade_item",
                bundleOf(
                    "create_trade_form_result" to
                            CreateTradeItem(
                                "${tradesList[selectedItemPos][0]}-${tradesList[selectedItemPos][1]}",
                                uploadedImageURI!!,
                                itemQuantity?.text.toString(),
                                itemQuantityLabel?.text.toString()
                            )
                )
            )
            transaction?.popBackStack()
        }else{
            Toast.makeText(
                requireContext(),
                "Some fields are empty! Be sure to check it out!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        Log.i("Perms: ", result.toString())
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