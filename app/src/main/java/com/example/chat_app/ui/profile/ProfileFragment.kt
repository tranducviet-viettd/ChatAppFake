package com.example.chat_app.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.chat_app.App
import com.example.chat_app.R
import com.example.chat_app.data.EventObserver
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.model.ChatWithUserInfo
import com.example.chat_app.databinding.FragmentProfileBinding
import com.example.chat_app.ui.chat.ChatFragment
import com.example.chat_app.ui.main.MainActivity
import com.example.chat_app.util.convertTwoUserIDs
import com.example.chat_app.util.forceHideKeyboard
import com.example.chat_app.util.showSnackBar

class ProfileFragment: Fragment() {
    companion object {
        const val ARGS_KEY_USER_ID = "bundle_user_id"
    }

    private val viewModel : ProfileViewModel by viewModels { ProfileViewModelFactory(App.myUserID,requireArguments().getString(ARGS_KEY_USER_ID)!!) }
    private lateinit var viewDataBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "ProfileFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentProfileBinding.inflate(inflater,container,false).apply{ viewmodel = viewModel}
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    private fun setupObservers(){
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })

        viewModel.snackBarText.observe(viewLifecycleOwner,
            EventObserver { text ->
                view?.showSnackBar(text)
                view?.forceHideKeyboard()
            })
        viewModel.selectChat.observe(viewLifecycleOwner, EventObserver{
            navigateToChat(it)
        })
    }
    private fun navigateToChat(userInfo: UserInfo){
        val bundle= bundleOf(
            ChatFragment.ARGS_KEY_USER_ID to App.myUserID,
            ChatFragment.ARGS_KEY_CHAT_ID to convertTwoUserIDs(App.myUserID,userInfo.id),
            ChatFragment.ARGS_KEY_OTHER_USER_ID to userInfo.id
        )
        findNavController().navigate(R.id.action_profileFragment_to_chatFragment,bundle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "ProfileFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "ProfileFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "ProfileFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "ProfileFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "ProfileFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "ProfileFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "ProfileFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "ProfileFragment: onDestroy called - Fragment DESTROYED")

    }




}