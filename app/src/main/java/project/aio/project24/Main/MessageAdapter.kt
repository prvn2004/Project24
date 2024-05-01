package project.aio.project24.Main

import UserGoogleDetailsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import project.aio.project24.Api.MessagesApi.Message
import project.aio.project24.R
import project.aio.project24.databinding.ItemMessageBotBinding
import project.aio.project24.databinding.ItemMessageUserBinding
import project.aio.project24.databinding.ItemPendingMessageLottieBinding

class MessageAdapter(private val messageList: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LEFT_VIEW = 1
        private const val RIGHT_VIEW = 2
        private const val PENDING_VIEW = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_VIEW -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageBotBinding.inflate(inflater, parent, false)
                LeftViewHolder(binding)
            }
            RIGHT_VIEW -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageUserBinding.inflate(inflater, parent, false)
                RightViewHolder(binding)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPendingMessageLottieBinding.inflate(inflater, parent, false)
                PendingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = messageList[position]
        when (holder) {
            is LeftViewHolder -> holder.bind(chat)
            is RightViewHolder -> holder.bind(chat)
            is PendingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isBot) LEFT_VIEW
        else if (messageList[position].isPending) PENDING_VIEW
        else RIGHT_VIEW
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    fun removePendingMessage() {
        for (i in messageList.indices.reversed()) {
            if (messageList[i].isPending) {
                messageList.removeAt(i)
                notifyItemRemoved(i)
                break
            }
        }
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

    inner class PendingViewHolder(private val binding: ItemPendingMessageLottieBinding) : RecyclerView.ViewHolder(binding.root) {
        val lottieAnimationView: LottieAnimationView = binding.animationView
        fun bind() {
            // Show Lottie animation
            lottieAnimationView.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()
        }

        public fun hideAnimation() {
            // Hide Lottie animation
            lottieAnimationView.visibility = View.GONE
            lottieAnimationView.cancelAnimation()
        }
    }
}
