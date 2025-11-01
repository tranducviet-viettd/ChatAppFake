package com.example.chat_app.ui.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.R
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.ui.main.MainActivity
import com.squareup.picasso.Picasso
import kotlin.math.abs
import com.squareup.picasso.Target
import com.squareup.picasso.Callback
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest

@BindingAdapter("bind_messages_list", "bind_should_scroll")
fun bindMessageList(listView: RecyclerView,items: List<Message>?,shouldScroll: Boolean?){
    items?.let{
        Log.d("BindingAdapter", "Submitting list: $it, hashCode: ${it.hashCode()}")
        (listView.adapter as MessagesListAdapter).submitList(it.toList())
        if (shouldScroll == true ) {
            listView.post {
                listView.scrollToPosition(items.size - 1)
                (listView.context as? ChatViewModel)?.clearScrollFlag()
            }
        }
    }
}
@BindingAdapter("picassoImageUrl")
fun bindPicassoImageUrl(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        imageView.setImageResource(R.drawable.ic_baseline_error_24)
        imageView.visibility = View.GONE
        return
    }

    val context = imageView.context
    val squareSize = dpToPx(200, context)
    val landscapeSize = Pair(dpToPx(240, context), dpToPx(160, context))
    val portraitSize = Pair(dpToPx(160, context), dpToPx(240, context))



    val fileName = url.toMD5() + ".jpg"
    val localFile = File(MainActivity.imagesCacheDir, fileName)
    val thumbFile = File(MainActivity.imagesCacheDir, "thumb_" + fileName)

    if (thumbFile.exists()) {
        loadThumbnail(imageView, thumbFile, squareSize, landscapeSize, portraitSize)
        Log.d("ImageCache", "Loaded thumbnail: ${thumbFile.absolutePath}")
        return
    } else if (localFile.exists()) {
        loadFromLocalFile(imageView, localFile, squareSize, landscapeSize, portraitSize)
        Log.d("ImageCache", "Loaded full image: ${localFile.absolutePath}")
        return
    }
    val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            bitmap?.let {
                val aspectRatio = it.width.toFloat() / it.height
                val layoutParams = imageView.layoutParams
                when {
                    abs(aspectRatio - 1.0f) < 0.1f -> {
                        layoutParams.width = squareSize
                        layoutParams.height = squareSize
                    }
                    aspectRatio > 1.0f -> {
                        layoutParams.width = landscapeSize.first
                        layoutParams.height = landscapeSize.second
                    }
                    else -> {
                        layoutParams.width = portraitSize.first
                        layoutParams.height = portraitSize.second
                    }
                }
                imageView.layoutParams = layoutParams

                try {
                    FileOutputStream(localFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    Log.d("ImageCache", "Saved full image: ${localFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e("ImageCache", "Error saving full image", e)
                }

                try {
                    val thumbBitmap = Bitmap.createScaledBitmap(bitmap, 240, (240 / aspectRatio).toInt(), true)
                    FileOutputStream(thumbFile).use { out ->
                        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    Log.d("ImageCache", "Saved thumbnail: ${thumbFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e("ImageCache", "Error saving thumbnail", e)
                }

                // Dùng Callback thay vì load lại URL
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE
            } ?: run {
                imageView.setImageResource(R.drawable.ic_baseline_error_24)
                imageView.visibility = View.GONE
            }
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            imageView.setImageResource(R.drawable.ic_baseline_error_24)
            imageView.visibility = View.GONE
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            imageView.setImageResource(R.drawable.picture_svgrepo_com__1_)
        }
    }

    Picasso.get().load(url).into(target)
}

private fun loadFromLocalFile(
    imageView: ImageView,
    localFile: File,
    squareSize: Int,
    landscapeSize: Pair<Int, Int>,
    portraitSize: Pair<Int, Int>
) {
    Picasso.get()
        .load(localFile)
        .placeholder(R.drawable.picture_svgrepo_com__1_)
        .error(R.drawable.ic_baseline_error_24)
        .into(imageView, object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    val aspectRatio = it.width.toFloat() / it.height
                    val layoutParams = imageView.layoutParams
                    when {
                        abs(aspectRatio - 1.0f) < 0.1f -> {
                            layoutParams.width = squareSize
                            layoutParams.height = squareSize
                        }
                        aspectRatio > 1.0f -> {
                            layoutParams.width = landscapeSize.first
                            layoutParams.height = landscapeSize.second
                        }
                        else -> {
                            layoutParams.width = portraitSize.first
                            layoutParams.height = portraitSize.second
                        }
                    }
                    imageView.layoutParams = layoutParams
                    imageView.visibility = View.VISIBLE
                } ?: run {
                    imageView.setImageResource(R.drawable.ic_baseline_error_24)
                    imageView.visibility = View.GONE
                }
            }

            override fun onError(e: Exception?) {
                imageView.setImageResource(R.drawable.ic_baseline_error_24)
                imageView.visibility = View.GONE
            }
        })
}

private fun loadThumbnail(
    imageView: ImageView,
    thumbFile: File,
    squareSize: Int,
    landscapeSize: Pair<Int, Int>,
    portraitSize: Pair<Int, Int>
) {
    Picasso.get()
        .load(thumbFile)
        .placeholder(R.drawable.picture_svgrepo_com__1_)
        .error(R.drawable.ic_baseline_error_24)
        .into(imageView, object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    val aspectRatio = it.width.toFloat() / it.height
                    val layoutParams = imageView.layoutParams
                    when {
                        abs(aspectRatio - 1.0f) < 0.1f -> {
                            layoutParams.width = squareSize
                            layoutParams.height = squareSize
                        }
                        aspectRatio > 1.0f -> {
                            layoutParams.width = landscapeSize.first
                            layoutParams.height = landscapeSize.second
                        }
                        else -> {
                            layoutParams.width = portraitSize.first
                            layoutParams.height = portraitSize.second
                        }
                    }
                    imageView.layoutParams = layoutParams
                    imageView.visibility = View.VISIBLE
                } ?: run {
                    imageView.setImageResource(R.drawable.ic_baseline_error_24)
                    imageView.visibility = View.GONE
                }
            }

            override fun onError(e: Exception?) {
                imageView.setImageResource(R.drawable.ic_baseline_error_24)
                imageView.visibility = View.GONE
            }
        })
}
// Hàm tính MD5 hash cho URL (để làm tên file)
fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}
// Hàm chuyển dp sang px
fun dpToPx(dp: Int, context: Context): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
@BindingAdapter("bind_message","bind_message_viewmodel")
fun View.bindShouldMessageShowTimeText(message: Message,viewModel: ChatViewModel){
    val fifteenMinute = 900000
    val index = viewModel.messageList.value!!.indexOf(message)

    if(index==0){
        this.visibility=View.VISIBLE
    }
    else{
        val beforeMessage=viewModel.messageList.value!![index-1]

        if(abs(beforeMessage.epochTimeMs - message.epochTimeMs) > fifteenMinute){
            this.visibility = View.VISIBLE
        }
        else{
            this.visibility = View.GONE
        }
    }

}