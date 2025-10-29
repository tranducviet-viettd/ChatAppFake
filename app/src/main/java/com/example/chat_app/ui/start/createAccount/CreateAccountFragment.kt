package com.example.chat_app.ui.start.createAccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.R
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentCreateAccountBinding
import com.example.chat_app.ui.main.MainActivity
import com.example.chat_app.util.SharedPreferencesUtil
import com.example.chat_app.util.forceHideKeyboard
import com.example.chat_app.util.showSnackBar

class CreateAccountFragment:Fragment() {

    private val viewModel by viewModels<CreateAccountViewModel>()
    private lateinit var viewDataBinding: FragmentCreateAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "CreateFragment: onCreateView called - Fragment creating View")

        viewDataBinding=FragmentCreateAccountBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
    }

    private fun setupObserver(){
        viewModel.dataLoading.observe(viewLifecycleOwner,EventObserver {
            (activity as MainActivity).showGlobalProgressBar(it)
        })
        viewModel.snackBarText.observe(viewLifecycleOwner,EventObserver{
            view?.showSnackBar(it)
            view?.forceHideKeyboard()
        })
        viewModel.isCreateEvent.observe(viewLifecycleOwner,EventObserver{
            SharedPreferencesUtil.saveUserID(requireContext(),it.uid)
            navigateToChats()
        })
    }
    private fun navigateToChats() {
        findNavController().navigate(R.id.action_createAccountFragment_to_navigation_chats)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "CreateFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "CreateFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "CreateFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "CreateFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "CreateFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "CreateFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "CreateFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "CreateFragment: onDestroy called - Fragment DESTROYED")

    }


}