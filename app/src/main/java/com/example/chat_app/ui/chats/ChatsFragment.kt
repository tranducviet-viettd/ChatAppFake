package com.example.chat_app.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class ChatsFragment: Fragment() {

    private val viewModel: ChatsViewModel by viewModels{ ChatsViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding: FragmentChatsBinding
    private lateinit var listAdapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "ChatsFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentChatsBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewDataBinding.root
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
            listAdapter= ChatsListAdapter(viewModel)
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
        Log.d("CheckLifecycle", "ChatsFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "ChatsFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
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