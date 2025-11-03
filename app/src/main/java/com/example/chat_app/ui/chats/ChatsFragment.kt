package com.example.chat_app.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.chat_app.data.EventObserver
import com.example.chat_app.data.model.ChatWithUserInfo
import com.example.chat_app.databinding.FragmentChatsBinding
import com.example.chat_app.ui.chat.ChatFragment
import com.example.chat_app.util.convertTwoUserIDs
import com.example.chat_app.App
import com.example.chat_app.R
import com.example.chat_app.databinding.ToolbarCustomChatsBinding
import com.example.chat_app.databinding.ToolbarCustomShowImageBinding

class ChatsFragment: Fragment() {

    private val viewModel: ChatsViewModel by viewModels{ ChatsViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding: FragmentChatsBinding
    private lateinit var toolbarCustomChatsBinding: ToolbarCustomChatsBinding
    private lateinit var listAdapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "ChatsFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentChatsBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner

        toolbarCustomChatsBinding = ToolbarCustomChatsBinding.inflate(inflater,container,false).apply {
            viewmodel=viewModel
            lifecycleOwner= this@ChatsFragment.viewLifecycleOwner
        }

        return viewDataBinding.root
    }

    private fun setupCustomToolbar(){
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false) // Nếu cần nút back
            // Đặt custom view
            customView = toolbarCustomChatsBinding.root
            // Đảm bảo custom view lấp đầy toolbar và căn phải
            customView.layoutParams = Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.MATCH_PARENT
            )
        }
    }
    private fun removeCustomToolbar() {
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar.setDisplayShowTitleEnabled(true)
        supportActionBar.customView = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d("CheckLifecycle", "ChatsFragment: onActivityCreated called - Fragment initializing after Activity creation")

        super.onActivityCreated(savedInstanceState)
        setupListAdapter()

        setupObserver()

    }

    private fun setupListAdapter(){
        val viewModel=viewDataBinding.viewmodel
        if(viewModel!=null){
            listAdapter= ChatsListAdapter(viewModel,lifecycleOwner = viewLifecycleOwner)
            viewDataBinding.chatsRecyclerView.adapter=listAdapter
        }
        else{
            throw Exception("viewmodel loi")
        }
    }

    private fun setupObserver(){
        viewModel.selectChat.observe(viewLifecycleOwner, EventObserver{
            navigateToChat(it)
        })

    }

    private fun navigateToChat(chatWithUserInfo: ChatWithUserInfo){
        val bundle= bundleOf(
            ChatFragment.ARGS_KEY_USER_ID to App.myUserID,
            ChatFragment.ARGS_KEY_CHAT_ID to convertTwoUserIDs(App.myUserID,chatWithUserInfo.mUserInfo.id),
            ChatFragment.ARGS_KEY_OTHER_USER_ID to chatWithUserInfo.mUserInfo.id
        )
        findNavController().navigate(R.id.action_navigation_chats_to_chatFragment,bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "ChatsFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "ChatsFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "ChatsFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        setupCustomToolbar()
        Log.d("CheckLifecycle", "ChatsFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "ChatsFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        removeCustomToolbar()
        Log.d("CheckLifecycle", "ChatsFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "ChatsFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "ChatsFragment: onDestroy called - Fragment DESTROYED")

    }



}