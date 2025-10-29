package com.example.chat_app.ui.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.R
import com.example.chat_app.data.EventObserver
import com.example.chat_app.data.Result
import com.example.chat_app.databinding.FragmentKeyBinding
import com.example.chat_app.ui.main.MainActivity

class KeyFragment: Fragment() {

    private val viewModel by viewModels<KeyViewModel>()

    private lateinit var viewDataBinding: FragmentKeyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding= FragmentKeyBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(false)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
        viewModel.checkActiveKey { result ->
            if(result is Result.Success){
                result.data?.let {
                    if (!it){
                        navigationToStart()
                    }
                }
            }
        }

    }

    private fun setupObservers(){
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })

        viewModel.passKeyEvent.observe(viewLifecycleOwner, EventObserver{
            navigationToStart()
        })

        viewModel.failKeyEvent.observe(viewLifecycleOwner, EventObserver{
            navigationToLogin()
        })
        viewModel.sendEvent.observe(viewLifecycleOwner, EventObserver{
            viewModel.checkKey()
        })

    }
    private fun navigationToStart(){
        findNavController().navigate(R.id.action_keyFragment_to_StartFragment)
    }

    private fun navigationToLogin(){
        findNavController().navigate(R.id.action_keyFragment_to_LoginFragment)
    }
}