package com.foke.together.domain.output

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

interface InternalCameraRepositoryInterface {
    suspend fun capture(context: Context): Result<Bitmap>
    suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    )
}