package com.envizioners.dumpy.ownerclient.fragments.shared

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.SalesVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.DashboardVM
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.*
import kotlin.Exception

class DashboardFragment : Fragment() {

    private lateinit var tpCount: TextView
    private lateinit var completed: TextView
    private lateinit var pending: TextView
    private lateinit var accepted: TextView
    private lateinit var declined: TextView
    private lateinit var proceeded: TextView
    private lateinit var cancelled: TextView

    private lateinit var fsCard: CardView
    private lateinit var fsBtn: Button
    private lateinit var loadingDialog: LoadingDialog

    private var userType: String? = null
    private var userID: Int? = null

    private val viewModel: DashboardVM by lazy {
        ViewModelProvider(this).get(DashboardVM::class.java)
    }

    private val salesVM: SalesVM by lazy {
        ViewModelProvider(this).get(SalesVM::class.java)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading Dashboard...")
        loadingDialog.startLoading()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dashboardView = inflater.inflate(R.layout.f_dashboard, container, false)

        tpCount = dashboardView.findViewById(R.id.trade_proposal_count)
        completed = dashboardView.findViewById(R.id.tp_completed_count)
        pending = dashboardView.findViewById(R.id.tp_pending_count)
        accepted = dashboardView.findViewById(R.id.tp_accepted_count)
        declined = dashboardView.findViewById(R.id.tp_declined_count)
        proceeded = dashboardView.findViewById(R.id.tp_proceeded_count)
        cancelled = dashboardView.findViewById(R.id.tp_cancelled_count)
        fsCard = dashboardView.findViewById(R.id.dashboard_fs_card)
        fsBtn = dashboardView.findViewById(R.id.dashboard_fs_btn)

        fsBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Downloading...", Toast.LENGTH_SHORT).show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            userType = UserPreference.getUserType(requireContext(), "USER_TYPE")
            userID = if (userType == "DUMPY_RECYCLER"){
                UserPreference.getUserID(requireContext(), "USER_ID")
            }else{
                UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            }

            viewModel.getTpStatuses(userID!!, userType!!)
            if (userType == "DUMPY_OWNER"){
                fsCard.visibility = View.VISIBLE

                var establishmentID: Int = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
                fsBtn.setOnClickListener {
                    request.send {
                        if (it.allGranted()){
                            fsBtn.isEnabled = false
                            salesVM.getReport(userID!!, establishmentID)
                        }else{
                            runDialog()
                        }
                    }
                }
            }
        }

        viewModel.tradeProposalsStatusLiveData.observe(viewLifecycleOwner) {

            var total = 0
            var completedCount = 0
            var pendingCount = 0
            var acceptedCount = 0
            var declinedCount = 0
            var proceededCount = 0
            var cancelledCount = 0

            it.forEach { item ->
                total += item.count.toInt()
                when(item.tp_status){
                    "COMPLETED" -> completedCount += item.count.toInt()
                    "PENDING" -> pendingCount += item.count.toInt()
                    "ACCEPTED" -> acceptedCount += item.count.toInt()
                    "DECLINED" -> declinedCount += item.count.toInt()
                    "PROCEEDED" -> proceededCount += item.count.toInt()
                    "CANCELLED" -> cancelledCount += item.count.toInt()
                }
            }

            tpCount.text = total.toString()
            completed.text = completedCount.toString()
            pending.text = pendingCount.toString()
            accepted.text = acceptedCount.toString()
            declined.text = declinedCount.toString()
            proceeded.text = proceededCount.toString()
            cancelled.text = cancelledCount.toString()

            loadingDialog.dismissLoading()
        }
        return dashboardView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        salesVM.responseFile.observe(viewLifecycleOwner) { response ->
            try {
                writeResponseBodyToDisk(response as ResponseBody)
            }catch (exception: Exception){
                fsBtn.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "There's something wrong downloading your financial sh*t. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            loadingDialog.setLoadingScreenText("Downloading your establishment's financial status...")
            loadingDialog.startLoading()
            val futureStudioIconFile: File = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator.toString() + "financial_summary.xlsx")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize: Long = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream?.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.i("Download", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream?.flush()
                loadingDialog.dismissLoading()
                Toast.makeText(requireContext(), "Successfully downloaded your establishment's financial summary!\n${requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator.toString() + "financial_summary.xlsx"}", 5).show()
                true
            } catch (e: IOException) {
                fsBtn.isEnabled = true
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            fsBtn.isEnabled = true
            false
        }
    }

//    override fun onPermissionsResult(result: List<PermissionStatus>) {
//        Log.i("Perms: ", result.toString())
//        when {
//            result.allGranted() -> {
//                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
//            }
//            else -> {
//                runDialog()
//            }
//        }
//    }

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
        try {
            (requireActivity() as RecyclerActivity).updateActionBarTitle("Dashboard")
        }catch (exception: Exception){
            try {
                (requireActivity() as OwnerActivity).updateActionBarTitle("Dashboard")
            }catch (exception: Exception){
                (requireActivity() as StaffActivity).updateActionBarTitle("Dashboard")
            }
        }
    }
}