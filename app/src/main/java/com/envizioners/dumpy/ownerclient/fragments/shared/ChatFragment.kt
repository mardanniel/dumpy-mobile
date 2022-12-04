package com.envizioners.dumpy.ownerclient.fragments.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.shared.ChatViewAdapter
import com.envizioners.dumpy.ownerclient.response.shared.ChatDocument
import com.envizioners.dumpy.ownerclient.response.shared.ChatMessagesItem
import com.envizioners.dumpy.ownerclient.viewmodels.shared.ChatVM
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.newSingleThreadContext
import java.lang.Exception
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var chatViewAdapter: ChatViewAdapter
    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var messageBox: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var receiverTextView: TextView

    private var senderID: String? = ""
    private var receivedSenderType: String? = ""
    private var senderType: String = ""
    private var receiverType: String = ""
    private var receiverID: String? = ""
    private var receiverName: String? = ""
    private var chatToken: String? = ""
    private var searchJunkshopReferrer: Boolean = false
    private var chatList = mutableListOf<ChatMessagesItem>()


    private val chatVM: ChatVM by lazy {
        ViewModelProvider(this).get(ChatVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchJunkshopReferrer = arguments?.getBoolean("searchJunkshopReferrer")!!

        if (!searchJunkshopReferrer){
            chatToken = arguments?.getString("chatToken")
        }

        senderID = arguments?.getString("senderID")
        receivedSenderType = arguments?.getString("senderType")
        receiverID = arguments?.getString("receiverID")
        receiverName = arguments?.getString("receiverName")

        Log.i("RST", receivedSenderType.toString())

        if (receivedSenderType != "DUMPY_RECYCLER"){
            senderType = "DUMPY_JS"
            receiverType = "DUMPY_RECYCLER"
        }else{
            senderType = "DUMPY_RECYCLER"
            receiverType = "DUMPY_JS"
        }

        firestoreDB = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_chat, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(searchJunkshopReferrer){
            chatVM.getConversationToken(
                senderID!!.toInt(),
                receiverID!!.toInt()
            )

            chatVM.chatToken.observe(viewLifecycleOwner) { response ->

                Log.i("ChatToken", response)
                chatToken = response.replace("\"", "")
                setUpComponents(view)
                initChat()
                initRecyclerView(view)
            }

        }else{
            setUpComponents(view)
            initChat()
            initRecyclerView(view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpComponents(view: View){
        receiverTextView = view.findViewById(R.id.f_chat_receiver)
        messageBox = view.findViewById(R.id.f_chat_message_box)
        sendMessageBtn = view.findViewById(R.id.f_chat_send_btn)

        receiverTextView.text = "You are now chatting with\n$receiverName"

        sendMessageBtn.setOnClickListener {
            if (!messageBox.text.isNullOrEmpty()){
                sendMessage(messageBox.text.toString())
                messageBox.text = null
            }else{
                Toast.makeText(
                    requireContext(),
                    "Type your message in the message field!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_chat_log)
        var rvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        rvLayoutManager.reverseLayout = true
        recyclerView.layoutManager = rvLayoutManager
        chatViewAdapter = ChatViewAdapter(
            senderType,
            receiverName!!,
            chatList
        )
        recyclerView.adapter = chatViewAdapter
    }

    private fun initChat(){
        Log.i("ChatToken: ", chatToken!!)
        val docRef = firestoreDB.collection("chat").document(chatToken!!)
        var changed = false

        docRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()){
                    createConversation()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("FirestoreDB", "get failed with ", exception)
            }

        docRef.addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                if (snapshot.exists())
                    if(snapshot?.data?.get("messages") != null){

                        var gson = Gson()
                        val type = object : TypeToken<MutableList<ChatMessagesItem>>() {}.type
                        var docResponse: MutableList<ChatMessagesItem> = gson.fromJson(gson.toJson(snapshot.data!!["messages"]), type)
                        var docLength = docResponse.size

                        if(!changed){
                            if (chatList.size == 0){
                                chatList.addAll(0, docResponse.reversed())
                                Log.i("FirestoreDB", "Added: $docResponse")
                            }else{
                                val difference = docResponse.minus(chatList)
                                chatList.addAll(0, difference)
                                Log.i("FirestoreDB", "Added new: $difference")
                            }
                        }

                        chatViewAdapter.notifyDataSetChanged()

                        changed = false
                        if(docLength > 49){
                            controlConversationLength()
                            changed = true
                        }
                    }else{
                        chatList.addAll(listOf())
                    }
            }else{
                createConversation()
            }
        }
    }

    private fun controlConversationLength(){
        val docRef = firestoreDB.collection("chat").document(chatToken!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    if (document != null) {
                        var gson = Gson()
                        val type = object : TypeToken<MutableList<ChatMessagesItem>>() {}.type
                        var docResponse: MutableList<ChatMessagesItem> = gson.fromJson(document.data?.get("messages").toString(), type)
                        docResponse.removeAt(0)
                        docRef.update(
                            mapOf(
                                "messages" to docResponse
                            )
                        )
                        chatList.removeAt(0)
                        chatViewAdapter.notifyItemRemoved(0)
                        Log.i("FirestoreDB", "DocumentSnapshot data: $docResponse")
                    } else {
                        Log.i("FirestoreDB", "No such document")
                    }
                }else{
                    createConversation()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("FirestoreDB", "get failed with ", exception)
            }
    }

    private fun sendMessage(message: String){
        firestoreDB.collection("chat").document(chatToken!!).update(
            mapOf(
                "count" to FieldValue.increment(
                    1L
                ),
                "messages" to FieldValue.arrayUnion(
                    ChatMessagesItem(
                        receiverID!!.toInt(),
                        receiverType,
                        senderID!!.toInt(),
                        senderType,
                        Date().time,
                        message.trim()
                    )
                )
            )
        )
        chatViewAdapter.notifyDataSetChanged()
    }

    private fun createConversation(){
        firestoreDB.collection("chat").document(chatToken!!).set(
            ChatDocument(
                0,
                listOf()
            )
        )
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as RecyclerActivity).updateActionBarTitle("Chat")
        }catch (exception: Exception){
            try {
                (requireActivity() as OwnerActivity).updateActionBarTitle("Chat")
            }catch (exception: Exception){
                (requireActivity() as StaffActivity).updateActionBarTitle("Chat")
            }
        }
    }
}