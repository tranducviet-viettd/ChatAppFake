package com.example.chat_app.data.db.repository

import android.net.Uri
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.chat_app.data.db.remote.FirebaseStorageSource
import com.example.chat_app.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.HashMap
import androidx.core.net.toUri
import com.cloudinary.android.MediaManager

class StorageRepository {

    fun uploadUserImage(userID: String, byteArray: ByteArray, b: (Result<Uri>) -> Unit) {
        b.invoke(Result.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MediaManager.get().upload(byteArray)
                    .option("public_id", "user_photos/$userID/profile_image") // Đường dẫn tùy chỉnh
                    .option("overwrite", true) // Ghi đè nếu tồn tại
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            // Có thể bỏ qua
                        }
                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            // Có thể cập nhật tiến trình nếu cần
                        }
                        override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                            val url = resultData["secure_url"] as? String
                            if (url != null) {
                                b.invoke(Result.Success(url.toUri()))
                            } else {
                                b.invoke(Result.Error("Không lấy được URL từ Cloudinary"))
                            }
                        }
                        override fun onError(requestId: String, error: ErrorInfo) {
                            b.invoke(Result.Error(error.description ?: "Lỗi tải lên Cloudinary"))
                        }
                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            // Có thể bỏ qua hoặc xử lý thử lại
                        }
                    })
                    .dispatch()
            } catch (e: Exception) {
                b.invoke(Result.Error(e.message ?: "Lỗi không xác định khi tải lên Cloudinary"))
            }
        }
    }
}