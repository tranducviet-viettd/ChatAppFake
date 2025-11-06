package com.example.chat_app.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.App
import com.example.chat_app.R
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentFriendsBinding
import com.example.chat_app.ui.profile.ProfileFragment

import kotlin.getValue

class FriendsFragment: Fragment() {

    private val viewModel : FriendsViewModel by viewModels { FriendsViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding : FragmentFriendsBinding
    private lateinit var viewAdapter: FriendsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "UserFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentFriendsBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
        setupObserver()
    }

    private fun setupListAdapter(){
        val viewModel= viewDataBinding.viewmodel
        if(viewModel!=null) {  viewAdapter= FriendsListAdapter(viewModel)
            viewDataBinding.friendsRecycleView.adapter=viewAdapter }

    }

    private fun setupObserver(){
        viewModel.selectUser.observe(viewLifecycleOwner, EventObserver{
            nagivateToProfile(it.info.id)
        })
    }

    private fun nagivateToProfile(userID:String){
        val bundle= bundleOf(ProfileFragment.ARGS_KEY_USER_ID to userID)
        findNavController().navigate(R.id.action_navigation_friends_to_profileFragment,bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "UserFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "UserFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "UserFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "UserFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "UserFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "UserFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "UserFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "UserFragment: onDestroy called - Fragment DESTROYED")

    }




}

