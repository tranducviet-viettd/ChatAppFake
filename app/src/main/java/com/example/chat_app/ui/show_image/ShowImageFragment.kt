package com.example.chat_app.ui.show_image

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chat_app.data.EventObserver
import com.example.chat_app.databinding.FragmentShowImageBinding
import com.example.chat_app.databinding.ToolbarCustomShowImageBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

class ShowImageFragment : Fragment() {

    companion object {
        const val ARGS_KEY_URL_IMAGE = "imageUrl"
        private const val TAG = "ShowImageFragment"
    }

    private val viewModel: ShowImageViewModel by viewModels {
        ShowImageViewModelFactory(
            requireArguments().getString(ARGS_KEY_URL_IMAGE)!!
        )
    }

    private lateinit var viewDataBinding: FragmentShowImageBinding
    private lateinit var toolbarCustomShowImageBinding: ToolbarCustomShowImageBinding

    // 🔥 Thêm permission launcher
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "storagePermissionLauncher: Permission result, isGranted=$isGranted")
        if (isGranted) {
            viewModel.downBtnPressed()
        } else {
            Log.d(TAG, "storagePermissionLauncher: Permission denied")
            Toast.makeText(context, "Cần quyền để lưu ảnh!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: Fragment created, imageUrl=${viewModel._imageUrl}")
        viewDataBinding = FragmentShowImageBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = this@ShowImageFragment.viewLifecycleOwner
        }
        toolbarCustomShowImageBinding = ToolbarCustomShowImageBinding.inflate(inflater,container,false).apply {
            viewmodel=viewModel
            lifecycleOwner= this@ShowImageFragment.viewLifecycleOwner
        }
        Log.d(TAG, "onCreateView: DataBinding set")
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: Setting up observers")
        setupObserver()
        setupCustomToolbar()
    }

    private fun setupCustomToolbar(){
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // Nếu cần nút back
            // Đặt custom view
            customView = toolbarCustomShowImageBinding.root
            // Đảm bảo custom view lấp đầy toolbar và căn phải
            customView.layoutParams = Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.MATCH_PARENT
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home-> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupObserver() {
        viewModel.downBtnEvent.observe(viewLifecycleOwner, EventObserver { imageUrl ->
            Log.d(TAG, "downBtnEvent: Trigger download for URL=$imageUrl")
            checkAndRequestStoragePermission(imageUrl)
        })
    }

    private fun checkAndRequestStoragePermission(imageUrl: String) {
        Log.d(TAG, "checkAndRequestStoragePermission: Checking permissions for API=${Build.VERSION.SDK_INT}")
        when {
            // Android 13+: READ_MEDIA_IMAGES
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "checkAndRequestStoragePermission: READ_MEDIA_IMAGES granted")
                    downloadImageWithPicasso(imageUrl)
                } else {
                    Log.d(TAG, "checkAndRequestStoragePermission: Requesting READ_MEDIA_IMAGES")
                    storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            // Android 6-9: WRITE_EXTERNAL_STORAGE
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "checkAndRequestStoragePermission: WRITE_EXTERNAL_STORAGE granted")
                    downloadImageWithPicasso(imageUrl)
                } else {
                    Log.d(TAG, "checkAndRequestStoragePermission: Requesting WRITE_EXTERNAL_STORAGE")
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            // Android 10-12: Không cần quyền
            else -> {
                Log.d(TAG, "checkAndRequestStoragePermission: No permission needed (Android 10-12)")
                downloadImageWithPicasso(imageUrl)
            }
        }
    }

    private fun downloadImageWithPicasso(imageUrl: String) {
        Log.d(TAG, "downloadImageWithPicasso: Starting download for URL=$imageUrl")
        Picasso.get()
            .load(imageUrl)
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                    Log.d(TAG, "onBitmapLoaded: Image loaded successfully, from=$from")
                    saveToGallery(bitmap, imageUrl)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.e(TAG, "onBitmapFailed: Error loading image, message=${e?.message}", e)
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "Lỗi tải ảnh: ${e?.message ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Log.d(TAG, "onPrepareLoad: Preparing to load image")
                }
            })
    }

    private fun saveToGallery(bitmap: Bitmap, originalUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "saveToGallery: Starting save process for URL=$originalUrl")
                if (!isAdded || activity?.isFinishing == true || activity?.isDestroyed == true) {
                    Log.w(TAG, "saveToGallery: Fragment not attached or Activity finishing, aborting")
                    return@launch
                }

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
                val fileName = "Cloudinary_$timeStamp.jpg"
                Log.d(TAG, "saveToGallery: Generated filename=$fileName")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d(TAG, "saveToGallery: Using MediaStore for Android 10+")
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ChatApp")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val uri = requireContext().contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: throw Exception("Không thể tạo URI")
                    Log.d(TAG, "saveToGallery: Created URI=$uri")

                    requireContext().contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        Log.d(TAG, "saveToGallery: Image compressed and saved to URI")
                    }

                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    requireContext().contentResolver.update(uri, contentValues, null, null)
                    Log.d(TAG, "saveToGallery: MediaStore updated")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Đã lưu vào Thư viện!", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "saveToGallery: Save completed, notified user")
                    }
                } else {
                    Log.d(TAG, "saveToGallery: Using File for Android 9-")
                    val picturesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES + "/ChatApp"
                    )
                    if (!picturesDir.exists()) {
                        picturesDir.mkdirs()
                        Log.d(TAG, "saveToGallery: Created directory $picturesDir")
                    }

                    val file = File(picturesDir, fileName)
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        Log.d(TAG, "saveToGallery: Image saved to file=$file")
                    }

                    MediaScannerConnection.scanFile(
                        requireContext(),
                        arrayOf(file.absolutePath),
                        arrayOf("image/jpeg"),
                        null
                    )
                    Log.d(TAG, "saveToGallery: MediaScanner triggered for file=$file")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Đã lưu vào ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "saveToGallery: Save completed, notified user")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveToGallery: Error saving image, message=${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi lưu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}