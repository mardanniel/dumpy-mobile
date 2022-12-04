package com.envizioners.dumpy.ownerclient.fragments.shared

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.shared.ChatMenuJViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.shared.ChatMenuRViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuJListResultBody
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuRListResultBody
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ChatVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ChatMenuFragment : Fragment() {

    private var userID: Int = 0
    private var userTYPE: String = ""
    private lateinit var chatMenuRViewAdapter: ChatMenuRViewAdapter
    private lateinit var chatMenuJViewAdapter: ChatMenuJViewAdapter
    private lateinit var chatMenuEmpty: TextView
    private var loadingDialog: LoadingDialog? = null
    private val chatVM: ChatVM by lazy {
        ViewModelProvider(this).get(ChatVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog?.setLoadingScreenText("Fetching conversations...")
        loadingDialog?.startLoading()

        CoroutineScope(Dispatchers.IO).launch {
            userTYPE = UserPreference.getUserType(requireContext(), "USER_TYPE")
            userID = if (userTYPE == "DUMPY_RECYCLER"){
                UserPreference.getUserID(requireContext(), "USER_ID")
            }else{
                UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            }
            Log.i("UTYPE: ", userID.toString())
            Log.i("UBI: ", userID.toString())
            chatVM.getConversationList(userID, userTYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_chat_menu, container, false)

        chatMenuEmpty = view.findViewById(R.id.f_chat_menu_empty)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatVM.chatMenuLD.observe(viewLifecycleOwner) { response ->
            if (response.isEmpty()){
                chatMenuEmpty.visibility = View.VISIBLE
            }
            initRecyclerView(view, response)
            loadingDialog?.dismissLoading()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initRecyclerView(view: View, chatMenuList: List<Any>){
        Log.i("RSTFCMF", userTYPE)
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_chat_menu_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        if(userTYPE == "DUMPY_RECYCLER"){
            chatMenuRViewAdapter = ChatMenuRViewAdapter(
                userID,
                userTYPE,
                chatMenuList as List<ChatMenuRListResultBody>,
                this.requireActivity().supportFragmentManager
            )
            recyclerView.adapter = chatMenuRViewAdapter
        }else{
            chatMenuJViewAdapter = ChatMenuJViewAdapter(
                userID,
                userTYPE,
                chatMenuList as List<ChatMenuJListResultBody>,
                this.requireActivity().supportFragmentManager
            )
            recyclerView.adapter = chatMenuJViewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as RecyclerActivity).updateActionBarTitle("Chat Menu")
        }catch (exception: Exception){
            try {
                (requireActivity() as OwnerActivity).updateActionBarTitle("Chat Menu")
            }catch (exception: Exception){
                (requireActivity() as StaffActivity).updateActionBarTitle("Chat Menu")
            }
        }
    }
}