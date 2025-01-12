package com.foke.together.external.camera.internal

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.camera2.Camera2Config
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InternalCameraModule {
    private val TAG = InternalCameraModule::class.java.simpleName
    private var analyzeExecutorService : ExecutorService? = null
    private var captureExecutorService : ExecutorService? = null

    // CameraX ImageAnalyzer용 백그라운드 서비스
    @Provides
    @Singleton
    fun clearAnalyzeExecutor(): Boolean{
        analyzeExecutorService?.shutdown()
        analyzeExecutorService?.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        analyzeExecutorService = null
        return analyzeExecutorService == null
    }

    @Provides
    @Singleton
    fun provideCameraSelector(cameraIdx: Int): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(cameraIdx)
            .build()
    }

    @Provides
    @Singleton
    fun provideCameraProvider(
        context: Context,
    ): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(context).get()
    }

    @OptIn(ExperimentalCamera2Interop::class)
    @Provides
    @Singleton
    fun provideImageAnalysis(
        context: Context
    ): ImageAnalysis {
        val iaBuilder = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetRotation(context.display.rotation)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        return iaBuilder.build()
    }

    @Provides
    @Singleton
    fun provideAnalyzeExecutor(): ExecutorService {
        if(analyzeExecutorService == null){
            analyzeExecutorService = Executors.newSingleThreadExecutor()
        }
        return analyzeExecutorService!!
    }

    fun shutdown(){
        if(analyzeExecutorService != null) {
            analyzeExecutorService?.shutdown()
            analyzeExecutorService?.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            analyzeExecutorService = null
        }
    }

    @Provides
    @Singleton
    fun provideCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder
            .fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR)
            .build()
    }

    @Provides
    @Singleton
    fun provideCameraStatus(): Flow<Boolean> = flow {
        emit(analyzeExecutorService != null)
    }

    @Provides
    @Singleton
    fun provideCameraPreview(
        previewView: PreviewView
    ): Preview {
        return Preview.Builder().build().also{
            it.surfaceProvider = previewView.surfaceProvider
        }
    }

    @Provides
    @Singleton
    fun provideImageCapture(
        context: Context
    ): ImageCapture {
        return ImageCapture.Builder()
            .setTargetRotation(context.display.rotation)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
    }
    // 미디어 파이프 제공하기 위함
//    @Provides
//    @Singleton
//    fun provideMPImageAnalyzer(
//        handLandMarker: HandLandmarker,
//        setCameraFrame: (Bitmap) -> Unit,
//        cameraCropScale: Float = DEFAULT_CROP_SCALE,
//        drawCameraInIndicator: Boolean = false
//    ) = ImageAnalysis.Analyzer{ imageProxy ->
//        val frameTime = SystemClock.uptimeMillis()
//        // Copy out RGB bits from the frame to a bitmap buffer
//        val bitmapBuffer =
//            Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//        imageProxy.close()
//        val newWidth = imageProxy.width / cameraCropScale
//        val newHeight = imageProxy.height / cameraCropScale
//        val newX = ( imageProxy.width - newWidth ) / 2
//        val newY = ( imageProxy.height - newHeight ) / 2
//        // if crop scale = 1.0f  ,   not use new bitmap
//        var crop = bitmapBuffer
//        if ( cameraCropScale > 1.5f ) {
//            crop = Bitmap.createBitmap( bitmapBuffer, newX.toInt(), newY.toInt(), newWidth.toInt(), newHeight.toInt()  )
//        }
//
//        // Convert the input Bitmap object to an MPImage object to run inference
//        val mpImage = BitmapImageBuilder(crop).build()
//        handLandMarker.detectAsync(mpImage, frameTime)
//    }
}