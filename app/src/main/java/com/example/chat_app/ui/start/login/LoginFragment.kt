package com.example.chat_app.ui.start.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chat_app.ui.main.MainActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentLoginBinding
import com.example.chat_app.util.SharedPreferencesUtil
import com.example.chat_app.util.forceHideKeyboard
import com.example.chat_app.util.showSnackBar
import com.example.chat_app.R

class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var viewDataBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "LoginFragment: onCreateView called - Fragment creating View")

        viewDataBinding=FragmentLoginBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObservers()
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

    private fun setUpObservers(){
        viewModel.dataLoading.observe(viewLifecycleOwner,EventObserver{(activity as MainActivity).showGlobalProgressBar(it)})
        viewModel.snackBarText.observe(viewLifecycleOwner,EventObserver{ text ->
            view?.showSnackBar(text)
            view?.forceHideKeyboard()

        })
        viewModel.switchEvent.observe(viewLifecycleOwner, EventObserver{
            if(viewDataBinding.switchButton.isChecked ){
                viewModel.removeHistory()
            }
        })

        viewModel.isLoggedInEvent.observe(viewLifecycleOwner,EventObserver{
            SharedPreferencesUtil.saveUserID(requireContext(),it.uid)
            navigateToChats()
        })


    }

    private fun navigateToChats(){
        findNavController().navigate(R.id.action_loginFragment_to_navigation_chats)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "LoginFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "LoginFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "LoginFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "LoginFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "LoginFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "LoginFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "LoginFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "LoginFragment: onDestroy called - Fragment DESTROYED")

    }
}

