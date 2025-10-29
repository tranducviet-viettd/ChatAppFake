package com.example.chat_app.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.databinding.ListItemMessageReceivedBinding
import com.example.chat_app.databinding.ListItemMessageSentBinding

class MessagesListAdapter internal constructor(private val viewModel: ChatViewModel,private val userID:String): ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallBack()){

    private val holderTypeMessageReceived =1
    private val holderTypeMessageSent = 2

    class ReceivedViewHolder(private val binding: ListItemMessageReceivedBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: ChatViewModel,message: Message){
            binding.viewmodel=viewModel
            binding.message=message
            binding.executePendingBindings()
        }
    }

    class SentViewHolder(private val binding: ListItemMessageSentBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: ChatViewModel,message: Message){
            binding.viewmodel=viewModel
            binding.message=message
            Log.d("ViewHolder", "Binding message: $message")
            binding.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderID != userID){
            holderTypeMessageReceived
        }
        else{
            holderTypeMessageSent
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val layoutInflater= LayoutInflater.from(parent.context)

        return when(viewType){
            holderTypeMessageReceived -> {
                val binding = ListItemMessageReceivedBinding.inflate(layoutInflater,parent,false)
                ReceivedViewHolder(binding)
            }

            holderTypeMessageSent  -> {
                val binding = ListItemMessageSentBinding.inflate(layoutInflater,parent,false)
                SentViewHolder(binding)
            }

            else ->{
                throw Exception("Error reading holder type")
            }

        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when(holder.itemViewType){
            holderTypeMessageReceived -> {
                (holder as ReceivedViewHolder).bind(viewModel,getItem(position))
            }
            holderTypeMessageSent -> {
                (holder as SentViewHolder).bind(viewModel,getItem(position))
            }
        }
    }





    }
class MessageDiffCallBack : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        val result = oldItem.id == newItem.id
        Log.d("DiffUtil", "areItemsTheSame: $result, oldItem.id=${oldItem.id}, newItem.id=${newItem.id}")
        return result
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        val result = oldItem.text == newItem.text &&
                oldItem.epochTimeMs == newItem.epochTimeMs &&
                oldItem.senderID == newItem.senderID &&
                oldItem.imageUrl == newItem.imageUrl
        Log.d("DiffUtil", "areContentsTheSame: $result, oldItem=$oldItem, newItem=$newItem")
        return result
    }
}