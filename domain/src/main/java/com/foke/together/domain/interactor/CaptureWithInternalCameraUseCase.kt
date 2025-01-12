package com.foke.together.domain.interactor

import android.content.Context
import com.foke.together.domain.output.ImageRepositoryInterface
import com.foke.together.domain.output.InternalCameraRepositoryInterface
import com.foke.together.util.AppLog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CaptureWithInternalCameraUseCase @Inject constructor(
    private val internalCameraRepository: InternalCameraRepositoryInterface,
    private val imageRepository: ImageRepositoryInterface
) {
    suspend operator fun invoke(
        context: Context,
        fileName: String
    ): Result<Unit>{
        internalCameraRepository.capture(context)
            .onSuccess {
                AppLog.i(TAG, "capture", "success: $it")
                imageRepository.cachingImage(it, fileName)
                return Result.success(Unit)
            }
            .onFailure {
                AppLog.i(TAG, "capture", "failure: $it")
                return Result.failure(it)
            }
        return Result.failure(Exception("Unknown error"))
    }

    companion object {
        private val TAG = CaptureWithInternalCameraUseCase::class.java.simpleName
    }
}