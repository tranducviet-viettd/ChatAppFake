package com.example.chat_app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navGraphViewModels
import androidx.viewbinding.ViewBinding
import com.example.chat_app.App
import com.example.chat_app.R
import com.example.chat_app.databinding.FragmentNotificationsBinding

class NotificationsFragment: Fragment() {

    private val viewModel : NotificationsViewModel by navGraphViewModels(R.id.nav_graph) { NotificationsViewModelFactory(App.myUserID) }
    private lateinit var listAdapter: NotificationsListAdapter
    private lateinit var viewBinding: FragmentNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentNotificationsBinding.inflate(inflater,container,false).apply { viewmodel =viewModel }
        viewBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
    }

    private fun setupListAdapter(){
        val viewModel = viewBinding.viewmodel

        if(viewModel!=null){
            listAdapter = NotificationsListAdapter(viewModel)
            viewBinding.usersRecyclerView.adapter = listAdapter
        }else{
            throw Exception("")
        }

    }


}