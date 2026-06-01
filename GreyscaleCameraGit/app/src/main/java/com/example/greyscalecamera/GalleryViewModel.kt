package com.example.greyscalecamera

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.greyscalecamera.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class GalleryViewModel : ViewModel() {

    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    fun loadImages(context: Context) {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name))
        }
        val targetDir = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        
        val files = targetDir.listFiles { file ->
            file.extension.lowercase() in listOf("jpg", "jpeg", "png")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()

        _images.value = files.map { Uri.fromFile(it) }
    }
}
