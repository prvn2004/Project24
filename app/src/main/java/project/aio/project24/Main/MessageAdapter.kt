package project.aio.project24.Main

import UserGoogleDetailsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import project.aio.project24.Api.MessagesApi.Message
import project.aio.project24.R
import project.aio.project24.databinding.ItemMessageBotBinding
import project.aio.project24.databinding.ItemMessageUserBinding

//
//class MessageAdapter(private val messageList: List<Message>) :
//    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
//
//    companion object {
//        private const val VIEW_TYPE_USER = 1
//        private const val VIEW_TYPE_BOT = 2
//    }
//
//
//    override fun getItemViewType(position: Int): Int {
//        return if (messageList[position].isBot) {
//            VIEW_TYPE_BOT
//        } else {
//            VIEW_TYPE_USER
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        val layoutId = if (viewType == VIEW_TYPE_USER) {
//            R.layout.item_message_user
//        } else {
//            R.layout.item_message_bot
//        }
//        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
//        return MessageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        val message = messageList[position]
//        holder.bind(message)
//    }
//
//    override fun getItemCount(): Int {
//        return messageList.size
//    }
//
//    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
//        private val tvSender: TextView = itemView.findViewById(R.id.tvSender)
//        private val ivProfile: ImageView = itemView.findViewById(R.id.floating_button)
//
//        val userGoogleDetailsManager = UserGoogleDetailsManager(itemView.context)
//
//        init {
//            // Add a global layout listener to adjust the height after layout
//            itemView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    // Remove the listener to prevent multiple calls
//                    itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                    // Adjusting the height of the TextView based on its content
//                    val layoutParams = tvContent.layoutParams
//                    layoutParams.height = tvContent.height
//                    tvContent.layoutParams = layoutParams
//                }
//            })
//        }
//
//        fun bind(message: Message) {
//            tvContent.text = message.content
//            tvSender.text = if (message.isBot) "Dasa" else "User"
//
//            if (!message.isBot) {
//                val photoUrl = userGoogleDetailsManager.getProfilePhotoUrl()
//                if (photoUrl != null) {
//                    Glide.with(itemView.context)
//                        .load(photoUrl)
//                        .into(ivProfile)
//                }
//            }
//        }
//    }
//
//}

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LEFT_VIEW = 1
        private const val RIGHT_VIEW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_VIEW -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageBotBinding.inflate(inflater, parent, false)
                LeftViewHolder(binding)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageUserBinding.inflate(inflater, parent, false)
                RightViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = messageList[position]
        when (holder) {
            is LeftViewHolder -> holder.bind(chat)
            is RightViewHolder -> holder.bind(chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isBot) LEFT_VIEW else RIGHT_VIEW
    }

    inner class LeftViewHolder(private val binding: ItemMessageBotBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Message) {
            binding.apply {
                chat.also {
                    binding.tvSender.text = "Dasa"
                    binding.tvContent.text = it.content
                }
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemMessageUserBinding) : RecyclerView.ViewHolder(binding.root) {
        private val userGoogleDetailsManager = UserGoogleDetailsManager(binding.root.context)

        fun bind(chat: Message) {
            binding.apply {
                chat.also {
                    binding.tvSender.text = userGoogleDetailsManager.getDisplayName()
                    binding.tvContent.text = it.content
                    val photoUrl = userGoogleDetailsManager.getProfilePhotoUrl()
                if (photoUrl != null) {
                    Glide.with(itemView.context)
                        .load(photoUrl)
                        .into(binding.floatingButton)
                }
                }
            }
        }
    }
}
