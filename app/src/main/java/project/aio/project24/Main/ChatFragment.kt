package project.aio.project24.Main

import UserGoogleDetailsManager
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.aio.project24.Api.ChatsApi.Chat
import project.aio.project24.Api.ChatsApi.ChatApiService
import project.aio.project24.Api.ChatsApi.ChatRespository
import project.aio.project24.Api.ChatsApi.ChatViewModel
import project.aio.project24.Api.ChatsApi.ChatViewModelFactory
import project.aio.project24.Api.FollowupApi.FollowupApiService
import project.aio.project24.Api.FollowupApi.FollowupsModel
import project.aio.project24.Api.GmailApi.GmailAuthCodeApiService
import project.aio.project24.Api.MessagesApi.Message
import project.aio.project24.Api.MessagesApi.MessageApiService
import project.aio.project24.Api.MessagesApi.MessageRepository
import project.aio.project24.Api.MessagesApi.MessageViewModel
import project.aio.project24.Api.MessagesApi.MessageViewModelFactory
import project.aio.project24.Api.Sockets.SocketManager
import project.aio.project24.Authentication.GmailAuthenticationClass
import project.aio.project24.Managers.Functions.ChatManager
import project.aio.project24.Managers.Functions.CommonFunctionManager
import project.aio.project24.Managers.Functions.ConnectionManager.ConnectionFunctions
import project.aio.project24.Managers.Functions.ConnectionManager.ConnectionService
import project.aio.project24.Managers.Functions.MessageManager
import project.aio.project24.Managers.Functions.PermissionManagers.NotificationPermissionManagers
import project.aio.project24.Managers.Functions.PreferenceDatabaseManager
import project.aio.project24.Managers.PreferenceManagers.AuthPreferenceManager
import project.aio.project24.Managers.PreferenceManagers.ChatCurrentPreferenceManager
import project.aio.project24.Managers.PreferenceManagers.GmailAuthorisationPreferenceManager
import project.aio.project24.Managers.PreferenceManagers.NotificationPreferenceManager
import project.aio.project24.R
import project.aio.project24.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class ChatFragment : Fragment(), ChatHistoryAdapter.OnItemClickListener {

    override fun onItemClick(chat: Chat) {
        chatCurrentPreferenceManager.setCurrentChatId(chat._id)
        currentChatId = chat._id
        messageList.clear()
        adapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.IO).launch {
            socketManager.getAllMessages(chatCurrentPreferenceManager.getCurrentChatId())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    companion object {
        private val RC_SIGN_IN = 1001
    }

    private lateinit var messageManager: MessageManager

    private lateinit var adapter: MessageAdapter
    private lateinit var followupAdapter: FollowupAdapter

    private lateinit var messageviewModel: MessageViewModel
    private lateinit var chatViewModel: ChatViewModel


    private lateinit var binding: FragmentChatBinding
    private lateinit var chatCurrentPreferenceManager: ChatCurrentPreferenceManager
    private lateinit var rvMessages: RecyclerView
    private lateinit var rvFollowups: RecyclerView

    private val messageList = mutableListOf<Message>()

    private var currentChatId: String = ""

    private lateinit var authPreferenceManager: AuthPreferenceManager

    private lateinit var gmailAuthenticationClass: GmailAuthenticationClass

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var gmailAuthorisationPreferenceManager: GmailAuthorisationPreferenceManager

    private lateinit var chatManager: ChatManager

    private lateinit var userGoogleDetailsManager: UserGoogleDetailsManager

    private var commonFunctionManager = CommonFunctionManager()

    private val socketManager = SocketManager

    lateinit var connectionService: ConnectionFunctions

    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private lateinit var rvChatHistory: RecyclerView

    private lateinit var notificationPreferenceManager: NotificationPreferenceManager

    private lateinit var preferenceDatabaseManager: PreferenceDatabaseManager

    private lateinit var notificationPermissionManagers: NotificationPermissionManagers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatCurrentPreferenceManager = ChatCurrentPreferenceManager(requireContext())
        authPreferenceManager = AuthPreferenceManager(requireContext())
        gmailAuthorisationPreferenceManager = GmailAuthorisationPreferenceManager(requireContext())
        userGoogleDetailsManager = UserGoogleDetailsManager(requireContext())
        connectionService = ConnectionFunctions(requireContext())
        notificationPreferenceManager = NotificationPreferenceManager(requireContext())
        preferenceDatabaseManager = PreferenceDatabaseManager(requireContext())
        notificationPermissionManagers = NotificationPermissionManagers(requireActivity(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val root = binding.root

//        ------------------------------------------------------------------------------------------
        //Testing Websocket Socket.io

        notificationPermissionManagers.checkPermissions()

        chatCurrentPreferenceManager.setCurrentChatId("")


//        scheduler.scheduleAtFixedRate({
//            if (connectionService.isConnectingToInternet) {
        Log.d("testing", "Connecting to socket")
        try {
            if (!socketManager.isConnected()) {
                socketManager.connect()
            }
        } catch (e: Exception) {
            Snackbar.make(
                binding.root, "Failed to connect to socket", Snackbar.LENGTH_SHORT
            ).show()
        }
//            } else {
//                Snackbar.make(
//                    binding.root,
//                    "Failed to connect to server, check internet ",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
//        }, 0, 5, TimeUnit.SECONDS)

        socketManager.onMessageReceived { value, code, json ->

            if(value == 0){
                Log.d("testing", "Message received: $json")

                val jsonString = json.toString()

                adapter.removePendingMessage()

                val gson = Gson()
                val message = gson.fromJson(jsonString, Message::class.java)

                if (message.chatId != "" || message.chatId != null) {
                    chatCurrentPreferenceManager.setCurrentChatId(message.chatId)
                    currentChatId = message.chatId
                }

                CoroutineScope(Dispatchers.Main).launch {
                    messageManager.handleResponse(code, message)
                }
            }

            if(value == 1){
                Log.d("testing", "All messages received: $json")

                val jsonString = json.toString()

                val gson = Gson()
                val messages = gson.fromJson(jsonString, Array<Message>::class.java)

                CoroutineScope(Dispatchers.Main).launch {
                    messageManager.handleAllMessages(code, messages)
                }
            }
        }

//       -----------------------------------
//       -------------------------------------------------------
        //Testing Completed

        rvMessages = binding.recyclerViewMessages
        rvMessages.setHasFixedSize(true)

        adapter = MessageAdapter(messageList)
        rvMessages.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rvMessages.adapter = adapter

        val rvChatHistory =
            binding.navigationView.getHeaderView(0)
                .findViewById<RecyclerView>(R.id.recycler_view_chat_history)
        rvChatHistory.layoutManager = LinearLayoutManager(requireContext())
        rvChatHistory.adapter = ChatHistoryAdapter(requireContext(), this)

        followupAdapter = FollowupAdapter()
        rvFollowups = binding.followupRv
        rvFollowups.layoutManager = LinearLayoutManager(requireContext())
        rvFollowups.adapter = followupAdapter


        val retrofit = commonFunctionManager.retrofitBuilder()

        val apiService = retrofit.create(MessageApiService::class.java)
        val repository = MessageRepository(apiService)
        messageviewModel = ViewModelProvider(
            this, MessageViewModelFactory(repository)
        ).get(MessageViewModel::class.java)

        val chatApiService = retrofit.create(ChatApiService::class.java)
        val chatrepository = ChatRespository(chatApiService)
        chatViewModel = ViewModelProvider(
            this, ChatViewModelFactory(chatrepository)
        ).get(ChatViewModel::class.java)

        messageManager = MessageManager(
            messageviewModel,
            adapter,
            rvMessages,
            root,
            messageList,
            binding.followupLayout
        )
        chatManager = ChatManager(chatViewModel, binding.root, userGoogleDetailsManager)
        gmailAuthenticationClass = GmailAuthenticationClass(
            requireContext(), requireActivity(), binding.root
        )

        chatManager.fetchAllChats(userGoogleDetailsManager.getUID(), rvChatHistory)

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_mobile_navigation_to_settingFragment)
        }

        binding.buttonSend.setOnClickListener {
            try {
                messageManager.sendMessage(
                    binding.editTextMessage.text.toString(),
                    chatCurrentPreferenceManager.getCurrentChatId(),
                    requireContext(),
                    socketManager
                )
                binding.editTextMessage.text.clear()
            } catch (e: Exception) {
                Log.e("testing", "Failed to send message", e)
                Snackbar.make(
                    binding.root,
                    "Failed to send message. Please check your connection.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonOptions.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_ui_display)

            val window = dialog.window
            window?.setGravity(Gravity.BOTTOM or Gravity.START)
            window?.attributes?.verticalMargin = 0.1f // Adjust vertical margin as needed
            dialog.show()

            val switch_personal_mode = dialog.findViewById<Switch>(R.id.switch_personal_mode)

            switch_personal_mode.isChecked = chatCurrentPreferenceManager.getCurrentValue()

            if (chatCurrentPreferenceManager.getCurrentValue() == false) {
                switch_personal_mode.isChecked = false
            } else {
                switch_personal_mode.isChecked = true
            }

            switch_personal_mode.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chatCurrentPreferenceManager.setCurrentValue(true)
                    CoroutineScope(Dispatchers.IO).launch {
                        PreferenceDatabaseManager(requireContext()).addPreference(
                            chatCurrentPreferenceManager.PREFS_NAME,
                            chatCurrentPreferenceManager.CURRENT_VALUE,
                            chatCurrentPreferenceManager.getCurrentValue().toString()
                        )
                    }
                    currentChatId = ""
                    chatCurrentPreferenceManager.setCurrentChatId("")
                    messageList.clear()
                    adapter.notifyDataSetChanged()
                    binding.followupLayout.visibility = View.VISIBLE

                } else {
                    chatCurrentPreferenceManager.setCurrentValue(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        PreferenceDatabaseManager(requireContext()).addPreference(
                            chatCurrentPreferenceManager.PREFS_NAME,
                            chatCurrentPreferenceManager.CURRENT_VALUE,
                            chatCurrentPreferenceManager.getCurrentValue().toString()
                        )
                    }
                    currentChatId = ""
                    chatCurrentPreferenceManager.setCurrentChatId("")

//                    CoroutineScope(Dispatchers.IO).launch {
//                        currentChatId = chatManager.createNewChat(false).await()
//                    }
                    messageList.clear()
                    adapter.notifyDataSetChanged()
                    binding.followupLayout.visibility = View.VISIBLE

                }
            }
        }

        binding.floatingButton.setOnClickListener {
            val drawer = binding.drawerLayout
            drawer.openDrawer(GravityCompat.START)
        }

        getFollowups()

        return root
    }

    fun getFollowups() {
        val retrofit = commonFunctionManager.retrofitBuilder()
        val followupApiService = retrofit.create(FollowupApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = followupApiService.getFollowups(userGoogleDetailsManager.getUID())
            if (response.isSuccessful) {
                val followups = response.body()
                withContext(Dispatchers.Main) {
                    followupAdapter.submitList(followups)
                }
            } else {
                // Handle error
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

class FollowupAdapter :
    RecyclerView.Adapter<FollowupAdapter.FollowupViewHolder>() {
    private var followups = emptyList<FollowupsModel>()

    class FollowupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_chat_name)
        val tvReferenceMail: TextView = itemView.findViewById(R.id.tv_reference_email)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_chat, parent, false)
        return FollowupViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowupViewHolder, position: Int) {
        val followup = followups[position]
        holder.tvTitle.text = followup.title

        val referenceMailsText = StringBuilder()
        for (referenceMail in followup.referenceMail) {
            val date = Date(referenceMail.date.toLong())
            val format = SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault())
            val time = format.format(date)
            referenceMailsText.append("From: ${referenceMail.from} \nDate: $time \nSubject: ${referenceMail.subject}\n\n")
        }
        holder.tvReferenceMail.text = referenceMailsText.toString()
    }

    override fun getItemCount() = followups.size

    fun submitList(list: List<FollowupsModel>?) {
        followups = list ?: emptyList()
        notifyDataSetChanged()
    }
}

class ChatHistoryAdapter(val context: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {

    var chatList = emptyList<Chat>()
    lateinit var chatCurrentPreferenceManager: ChatCurrentPreferenceManager
    lateinit var chatManager: ChatManager

    interface OnItemClickListener {
        fun onItemClick(chat: Chat)
    }

    class ChatHistoryViewHolder(
        itemView: View,
        private val chatList: List<Chat>
    ) :
        RecyclerView.ViewHolder(itemView) {
        val tvTimestamp: TextView = itemView.findViewById(R.id.text_view_message)

//            init {
//                itemView.setOnClickListener {
//                    val position = adapterPosition
//                }
//            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        chatCurrentPreferenceManager = ChatCurrentPreferenceManager(context)
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_history, parent, false)
        return ChatHistoryViewHolder(view, chatList)
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        val chat = chatList[position]
        var chattitle = "Greetings"
        if (chat.chatTitle != "") {
            chattitle = chat.chatTitle
        }
        holder.tvTimestamp.text = chattitle
        holder.itemView.setOnClickListener { listener.onItemClick(chat) }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun submitList(list: List<Chat>?) {
        chatList = list ?: emptyList()
        notifyDataSetChanged()
    }
}
