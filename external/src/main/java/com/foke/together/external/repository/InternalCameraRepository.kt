package com.foke.together.external.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.foke.together.domain.output.InternalCameraRepositoryInterface
import com.foke.together.util.AppLog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InternalCameraRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cameraProvider: ProcessCameraProvider,
    private val selector : CameraSelector,
    private val preview: Preview,
    private val imageAnalysis: ImageAnalysis,
    private val imageCapture: ImageCapture

): InternalCameraRepositoryInterface{

    override suspend fun capture(context: Context): Result<Bitmap> {
        var imageBitmap : Bitmap? = null
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    imageBitmap = imageProxy.toBitmap()
                }

                override fun onError(exception: ImageCaptureException) {
                    imageBitmap = null
                }
            }
        )
        return if(imageBitmap != null){
            Result.success(imageBitmap!!)
        } else{
            Result.failure(Exception("Unknown error"))
        }
    }

    override suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageAnalysis,
                imageCapture
            )
        }
        catch (e: Exception){
            AppLog.e(TAG,"showCameraPreview", e.message!!)
        }
    }

    companion object {
        private val TAG = InternalCameraRepository::class.java.simpleName
    }
}