package com.example.chat_app.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.R
import com.example.chat_app.databinding.FragmentChatBinding
import com.example.chat_app.databinding.ToolbarAddonChatBinding
import com.example.chat_app.ui.chats.ChatsViewModel
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.chat_app.data.EventObserver
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.databinding.BottomCustomMessageSentBinding
import com.example.chat_app.ui.show_image.ShowImageFragment


class ChatFragment: Fragment() {
    companion object {
        const val ARGS_KEY_USER_ID = "bundle_user_id"
        const val ARGS_KEY_OTHER_USER_ID = "bundle_other_user_id"
        const val ARGS_KEY_CHAT_ID = "bundle_other_chat_id"
    }

    private val viewModel : ChatViewModel by viewModels {
        ChatViewModelFactory(
            requireArguments().getString(ARGS_KEY_USER_ID)!!,
            requireArguments().getString(ARGS_KEY_OTHER_USER_ID)!!,
            requireArguments().getString(ARGS_KEY_CHAT_ID)!!,
            requireContext()
        )
    }

    private lateinit var viewDataBinding : FragmentChatBinding
    private lateinit var listAdapter: MessagesListAdapter
    private lateinit var listAdapterObserver: RecyclerView.AdapterDataObserver
    private lateinit var toolbarAddonChatBinding: ToolbarAddonChatBinding

    private lateinit var bottomCustomMessageSentBinding: BottomCustomMessageSentBinding

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.sendImage(it) }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "ChatFragment: onCreate called - Fragment CREATED")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "ChatFragment: onCreateView called - Fragment creating View")
        viewDataBinding = FragmentChatBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        toolbarAddonChatBinding= ToolbarAddonChatBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        toolbarAddonChatBinding.lifecycleOwner=this.viewLifecycleOwner
        bottomCustomMessageSentBinding= BottomCustomMessageSentBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        bottomCustomMessageSentBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "ChatFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "ChatFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "ChatFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "ChatFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "ChatFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "ChatFragment: onDestroyView called - Fragment View DESTROYED")
        listAdapter.unregisterAdapterDataObserver(listAdapterObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "ChatFragment: onDestroy called - Fragment DESTROYED")
        removeCustomToolbar()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("CheckLifecycle", "ChatFragment: onActivityCreated called - Fragment initializing after Activity creation")
        setupObserver()
        setupCustomToolbar()
        setupListAdapter()
        setupImagePicker()
    }

    private fun setupObserver(){
        viewModel.imagePressEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToShowImage(it)
        })
        viewModel.copyMessageEvent.observe(viewLifecycleOwner, EventObserver{
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // Tạo ClipData để lưu text
            val clip = ClipData.newPlainText("Copied Text", it.text)
            // Đặt dữ liệu vào clipboard
            clipboard.setPrimaryClip(clip)

            // Hiển thị thông báo
            Toast.makeText(requireContext(), "Đã copy", Toast.LENGTH_SHORT).show()
        })
    }

    private fun navigateToShowImage(message: Message){
        val bundle= bundleOf(
            ShowImageFragment.ARGS_KEY_URL_IMAGE to message.imageUrl
        )
        findNavController().navigate(R.id.action_chatFragment_to_showImageFragmet,bundle)
    }
    private fun setupImagePicker() {
        viewDataBinding.selectImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupCustomToolbar(){
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar.setDisplayShowTitleEnabled(false)
        supportActionBar.customView = toolbarAddonChatBinding.root
    }

    private fun setupListAdapter(){
        val viewModel = viewDataBinding.viewmodel
        if(viewModel!=null){
            listAdapterObserver= (object : RecyclerView.AdapterDataObserver(){
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    viewDataBinding.messagesRecycleView.scrollToPosition(positionStart)
                }
            })
            listAdapter = MessagesListAdapter(viewModel,requireArguments().getString(ARGS_KEY_USER_ID)!!)
            listAdapter.registerAdapterDataObserver(listAdapterObserver)
            viewDataBinding.messagesRecycleView.adapter=listAdapter
        }
        else{
            throw Exception("")
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home-> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun removeCustomToolbar(){
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar.setDisplayShowTitleEnabled(true)
        supportActionBar.customView = null
    }


}