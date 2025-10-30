package com.example.chat_app.ui.show_image

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.ortiz.touchview.TouchImageView

@BindingAdapter("bind_rotate")
fun bindRotate(imageView: TouchImageView, targetRotation: Float?) {
    targetRotation ?: return

    val delta = if(targetRotation == 0f) 0f else 90f

    imageView.animate()
        .rotationBy(delta) // ← XOAY TƯƠNG ĐỐI
        .setDuration(300)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            imageView.post { // ← CHỜ LAYOUT XONG
                applyFitCenterAfterRotation(imageView, targetRotation)

            }
        }
        .start()
}
private fun applyFitCenterAfterRotation1(imageView: TouchImageView, rotate: Float) {
    Log.d("FitCenterDebug", "=== BẮT ĐẦU applyFitCenterAfterRotation ===")
    Log.d("FitCenterDebug", "Góc xoay: $rotate")

    val drawable = imageView.drawable as? BitmapDrawable ?: run {
        Log.e("FitCenterDebug", "Drawable không phải BitmapDrawable")
        return
    }
    val bitmap = drawable.bitmap ?: run {
        Log.e("FitCenterDebug", "Bitmap null")
        return
    }

    val viewWidth = imageView.width.toFloat()
    val viewHeight = imageView.height.toFloat()
    if (viewWidth <= 0 || viewHeight <= 0) {
        Log.e("FitCenterDebug", "ImageView chưa layout: $viewWidth x $viewHeight")
        return
    }

    // 1. Kích thước sau xoay
    val (rotatedWidth, rotatedHeight) = when (rotate.toInt() % 360) {
        0, 180 -> bitmap.width.toFloat() to bitmap.height.toFloat()
        90, 270 -> bitmap.height.toFloat() to bitmap.width.toFloat()
        else -> return
    }

    // 2. Tính scale
    val scale = minOf(viewWidth / rotatedWidth, viewHeight / rotatedHeight)
    val scaledWidth = rotatedWidth * scale
    val scaledHeight = rotatedHeight * scale

    // 3. Tạo Matrix: XOAY + SCALE + CĂN GIỮA
    val matrix = Matrix().apply {
        postTranslate(
            (viewWidth - bitmap.width.toFloat()) / 2f,
            (viewHeight - bitmap.height.toFloat()) / 2f
        )
        postRotate(rotate, viewWidth / 2f, viewHeight / 2f)     // 2. Xoay quanh tâm ảnh
        postScale(scale, scale,viewWidth / 2f, viewHeight / 2f)                                     // 1. Scale trước
                                         // ← CĂN GIỮA
    }

    // 4. Áp dụng
    imageView.apply {
        scaleType = ImageView.ScaleType.MATRIX
        imageMatrix = matrix
        rotation = 0f  // ← RESET rotation của View
    }

    Log.d("FitCenterDebug", "HOÀN TẤT: scale=$scale, dx=${(viewWidth - scaledWidth)/2f}, dy=${(viewHeight - scaledHeight)/2f}")
}
private fun applyFitCenterAfterRotation(imageView: TouchImageView, rotate: Float) {
    val drawable = imageView.drawable as? BitmapDrawable ?: return
    val bitmap = drawable.bitmap ?: return

    val rotationMatrix = Matrix().apply {
        if (rotate == 0f) {
            postRotate(0f)
        }
        else{
            postRotate(90f)
        }
    }

    val rotatedBitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true
    )

    imageView.setImageBitmap(rotatedBitmap)
    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    imageView.rotation = 0f  // Reset rotation View
}