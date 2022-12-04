package com.envizioners.dumpy.ownerclient.fragments.owner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.OwnerActionsVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.StaffInfoVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StaffDetails : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var email: TextView
    private lateinit var fullName: TextView
    private lateinit var uName: TextView
    private lateinit var age: TextView
    private lateinit var hiredDate: TextView
    private lateinit var contact1: TextView
    private lateinit var contact2: TextView
    private lateinit var staffToggleStatus: Button
    private lateinit var staffRemove: Button
    private lateinit var loadingDialog: LoadingDialog

    private val TOGGLE_STATUS = 5
    private val REMOVE_STAFF = 6

    private val staffInfoVM: StaffInfoVM by lazy {
        ViewModelProvider(this).get(StaffInfoVM::class.java)
    }

    private val ownerActionsVM: OwnerActionsVM by lazy {
        ViewModelProvider(this).get(OwnerActionsVM::class.java)
    }

    private var staffID: Int = 0
    private var ownerID: Int = 0
    private var currPos: Int = 0
    private var accStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading staff profile...")
        loadingDialog.startLoading()

        staffID = arguments?.getString("staffID")?.toInt()!!
        currPos = arguments?.getInt("currPos")!!
        accStatus = arguments?.getString("accStatus")!!

        CoroutineScope(Dispatchers.Main).launch {
            ownerID = UserPreference.getUserID(requireContext(), "USER_ID")
            staffInfoVM.getStaffInfo(staffID, ownerID)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_o_staff_details, container, false)

        profileImage = view.findViewById(R.id.f_o_sd_image)
        email = view.findViewById(R.id.f_o_sd_email)
        fullName = view.findViewById(R.id.f_o_sd_name)
        uName = view.findViewById(R.id.f_o_sd_uname)
        age = view.findViewById(R.id.f_o_sd_age)
        hiredDate = view.findViewById(R.id.f_o_sd_hd)
        contact1 = view.findViewById(R.id.f_o_sd_contact)
        contact2 = view.findViewById(R.id.f_o_sd_contact2)
        staffToggleStatus = view.findViewById(R.id.staff_item_toggle_status)
        staffRemove = view.findViewById(R.id.staff_item_remove)

        staffInfoVM.staffInfoLD.observe(viewLifecycleOwner) { response ->
            val baseUrl = "https://dumpyph.com/uploads/js_staff/"
            var folderPath = if(response.js_staff_profile_picture == "DEFAULT-IMG.png" || response.js_staff_profile_picture.isNullOrEmpty()){
                baseUrl + "DEFAULT-IMG.png"
            }else{
                baseUrl + response.js_staff_email + "/" + response.js_staff_profile_picture
            }
            Glide.with(requireActivity())
                .load(folderPath)
                .transform(CircleCrop())
                .into(profileImage)

            email.text = response.js_staff_email
            fullName.text = "${response.js_staff_fname} ${response.js_staff_lname}"
            uName.text = response.js_staff_uname
            age.text = response.js_staff_age
            hiredDate.text = response.js_staff_hired_date
            contact1.text = response.js_staff_contact
            contact2.text = response.js_staff_email
            staffToggleStatus.text = if (accStatus == "0"){
                "Activate"
            }else "Disable"

            loadingDialog.dismissLoading()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ownerActionsVM.responseAction.observe(viewLifecycleOwner) { response ->
            var shouldPop = false
            val (action, transaction) = response
            var toastMessage = ""
            when(action){
                101 -> {
                    toastMessage = "Successfully ${if (transaction == TOGGLE_STATUS) "changed account status!" else "removed staff!"}"
                    shouldPop = true
                }
                104 -> {
                    toastMessage = "An unexpected error occurred. Please try again later."
                }
            }

            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            if (shouldPop){
                val sfm = requireActivity().supportFragmentManager
                sfm.setFragmentResult(
                    "transaction",
                    bundleOf(
                        "TRANSACTION_CODE" to transaction,
                        "POSITION" to currPos
                    )
                )
                sfm.popBackStack()
            }
            loadingDialog.dismissLoading()
        }

        staffToggleStatus.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Change account status")
                .setMessage("Are you sure you want to change the account status of this user?")
                .setPositiveButton("Change account status") { dialog, _ ->
                    dialog.dismiss()
                    loadingDialog.setLoadingScreenText("Changing account status...")
                    loadingDialog.startLoading()
                    ownerActionsVM.toggleStatus(staffID, ownerID)
                }
                .setNegativeButton("Go back") { dialog, _ ->
                    dialog.cancel()
                }.create().show()

        }

        staffRemove.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Remove Staff")
                .setMessage("Are you sure you want to remove this staff? This action is irreversible.")
                .setPositiveButton("Remove staff") { dialog, _ ->
                    dialog.dismiss()
                    loadingDialog.setLoadingScreenText("Removing staff...")
                    loadingDialog.startLoading()
                    ownerActionsVM.removeStaff(staffID, ownerID)
                }
                .setNegativeButton("Go back") { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }

    }
}