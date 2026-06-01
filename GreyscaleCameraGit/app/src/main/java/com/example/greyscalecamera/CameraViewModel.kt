package com.example.greyscalecamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.greyscalecamera.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

class CameraViewModel : ViewModel() {

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector: StateFlow<CameraSelector> = _cameraSelector

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private var activeRecording: Recording? = null

    fun toggleCamera() {
        _cameraSelector.value = if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    fun takePhoto(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor,
        onImageCaptured: (Uri, Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val outputDirectory = getOutputDirectory(context)
        
        val photoFile = File(outputDirectory, "IMG_$timestamp.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    
                    // Create greyscale version
                    val greyscaleFile = File(outputDirectory, "IMG_${timestamp}_BW.jpg")
                    val originalBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val greyscaleBitmap = toGreyscale(originalBitmap)
                    
                    try {
                        val out = FileOutputStream(greyscaleFile)
                        greyscaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                        onImageCaptured(savedUri, Uri.fromFile(greyscaleFile))
                    } catch (e: Exception) {
                        Log.e("CameraViewModel", "Error saving greyscale image", e)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    fun recordVideo(
        context: Context,
        videoCapture: VideoCapture<Recorder>,
        executor: Executor,
        onVideoRecorded: (Uri) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val recording = activeRecording
        if (recording != null) {
            recording.stop()
            activeRecording = null
            _isRecording.value = false
            return
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val outputDirectory = getOutputDirectory(context)
        val videoFile = File(outputDirectory, "VID_$timestamp.mp4")

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        activeRecording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(executor) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        _isRecording.value = true
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!event.hasError()) {
                            onVideoRecorded(Uri.fromFile(videoFile))
                        } else {
                            activeRecording?.stop()
                            activeRecording = null
                            _isRecording.value = false
                            onError(RuntimeException("Video capture failed: ${event.error}"))
                        }
                        _isRecording.value = false
                    }
                }
            }
    }

    private fun getOutputDirectory(context: Context): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }

    private fun toGreyscale(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(src, 0f, 0f, paint)
        return dest
    }
}
