package com.example.chat_app.ui.preview_image

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app.databinding.FragmentPreviewImageBeforeSendBinding
import com.example.chat_app.ui.chat.ChatViewModel
import kotlin.getValue
import androidx.navigation.navGraphViewModels
import com.example.chat_app.R
import com.example.chat_app.data.EventObserver
import com.example.chat_app.ui.chat.ChatFragment
import com.example.chat_app.ui.chat.ChatViewModelFactory

class PreviewImageFragment: Fragment() {

    companion object {
        const val ARGS_KEY_URI = "uri"
        const val ARGS_KEY_USER_ID1 = "bundle_user_id"
        const val ARGS_KEY_OTHER_USER_ID1 = "bundle_other_user_id"
        const val ARGS_KEY_CHAT_ID1 = "bundle_other_chat_id"
    }

    private val viewModel: PreviewImageViewModel by viewModels {
        PreviewImageViewModelFactory(
            requireArguments().getParcelable<Uri>(ARGS_KEY_URI)!!,
            requireArguments().getString(ARGS_KEY_USER_ID1)!!,
            requireArguments().getString(ARGS_KEY_OTHER_USER_ID1)!!,
            requireArguments().getString(ARGS_KEY_CHAT_ID1)!!,
            requireContext()
        )
    }
    private lateinit var viewDataBinding: FragmentPreviewImageBeforeSendBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentPreviewImageBeforeSendBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = this@PreviewImageFragment.viewLifecycleOwner
        }
       return viewDataBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBtn()
    }
    private fun setupBtn(){
        viewDataBinding.sendPreviewBtn.setOnClickListener {
            viewModel.sendImage()
            findNavController().popBackStack()
        }
        viewDataBinding.cancelPreviewBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun navigateToChatFragment(){
        findNavController().navigate(R.id.action_previewImageFragment_to_chatFragment)
    }

}