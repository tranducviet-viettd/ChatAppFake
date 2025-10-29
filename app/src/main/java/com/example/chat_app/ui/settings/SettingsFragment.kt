package com.example.chat_app.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.App
import com.example.chat_app.data.Event
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentSettingsBinding
import com.example.chat_app.util.SharedPreferencesUtil
import com.example.chat_app.R
import com.example.chat_app.util.convertFileToByteArray
import com.squareup.picasso.Picasso

class SettingsFragment: Fragment() {

    private val viewModel : SettingsViewModel by viewModels { SettingsViewModelFactory(App.myUserID) }

    private lateinit var viewDataBinding: FragmentSettingsBinding
    private val intentRequestCode = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "SettingFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentSettingsBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == intentRequestCode){
            data?.data?.let{uri ->
                convertFileToByteArray(requireContext(),uri).let { viewModel.changeUserImage(it) }
            }
        }
    }

    private fun setupObserver(){
        viewModel.editImageEvent.observe(viewLifecycleOwner, EventObserver{
            startSelectImageIntent()
        })

        viewModel.editStatusEvent.observe(viewLifecycleOwner, EventObserver{
            showEditStatusDialog()
        })
        viewModel.profileImageEvent.observe(viewLifecycleOwner, EventObserver { imageUrl ->
            showImageDialog(imageUrl)
        })
        viewModel.logoutEvent.observe(viewLifecycleOwner, EventObserver{
            SharedPreferencesUtil.removeUserID(requireContext())

            navigateToStart()
        })

        viewModel.editKeyEvent.observe(viewLifecycleOwner, EventObserver{
            showEditKeyDialog()
        })
    }
    private fun showEditStatusDialog(){
        val input = EditText(requireActivity() as Context)
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Status:")
            setView(input)
            setPositiveButton("Ok"){ _, _ ->
                val textInput= input.text.toString()
                if(!textInput.isBlank() && textInput.length <= 40){
                    viewModel.changeUserStatus(textInput)
                }

            }
            setNegativeButton("Cancel"){ _, _ ->}
            show()
        }
    }
    private fun showImageDialog(imageUrl: String) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val imageView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        // Tải hình ảnh bằng Picasso
        Picasso.get()
            .load(imageUrl)
            .error(R.drawable.ic_baseline_error_24) // Hình ảnh hiển thị nếu tải thất bại
            .into(imageView)

        dialog.setView(imageView)
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close") { _, _ -> dialog.dismiss() }
        dialog.show()

        // Tùy chỉnh kích thước của dialog (tùy chọn)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showEditKeyDialog(){
        val input = EditText(requireActivity() as Context)
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Key:")
            setView(input)
            setPositiveButton("Ok"){ _, _ ->
                val textInput= input.text.toString()
                if(!textInput.isBlank() && textInput.length <= 40){
                    viewModel.changeUserKey(textInput)
                }

            }
            setNegativeButton("Cancel"){ _, _ ->}
            show()
        }
    }
    private fun startSelectImageIntent(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent,intentRequestCode)
    }
    private fun navigateToStart(){

        findNavController().navigate(R.id.action_navigation_settings_to_startFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckLifecycle", "SettingFragment: onCreate called - Fragment CREATED")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CheckLifecycle", "SettingFragment: onViewCreated called - View created")
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckLifecycle", "SettingFragment: onStart called - Fragment STARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckLifecycle", "SettingFragment: onResume called - Fragment RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckLifecycle", "SettingFragment: onPause called - Fragment PAUSED")
    }
    override fun onStop() {
        super.onStop()
        Log.d("CheckLifecycle", "SettingFragment: onStop called - Fragment STOPPED")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckLifecycle", "SettingFragment: onDestroyView called - Fragment View DESTROYED")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckLifecycle", "SettingFragment: onDestroy called - Fragment DESTROYED")

    }

}