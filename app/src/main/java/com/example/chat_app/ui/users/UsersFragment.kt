package com.example.chat_app.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.node.ViewAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.chat_app.App
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentUsersBinding
import com.example.chat_app.R
import com.example.chat_app.ui.profile.ProfileFragment
class UsersFragment: Fragment() {

    private val viewModel : UsersViewModel by viewModels { UsersViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding : FragmentUsersBinding
    private lateinit var viewAdapter: UsersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "UserFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentUsersBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
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
        if(viewModel!=null) {  viewAdapter= UsersListAdapter(viewModel)
            viewDataBinding.usersRecyclerView.adapter=viewAdapter }

    }
    private fun setupObserver(){
        viewModel.selectUser.observe(viewLifecycleOwner, EventObserver{
            nagivateToProfile(it.info.id)
        })
    }
    private fun nagivateToProfile(userID:String){
        val bundle= bundleOf(ProfileFragment.ARGS_KEY_USER_ID to userID)
        findNavController().navigate(R.id.action_navigation_users_to_profileFragment,bundle)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}