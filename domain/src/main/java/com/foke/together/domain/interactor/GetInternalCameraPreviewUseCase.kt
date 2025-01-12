package com.foke.together.domain.interactor

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.foke.together.domain.output.ExternalCameraRepositoryInterface
import com.foke.together.domain.output.InternalCameraRepositoryInterface
import javax.inject.Inject

class GetInternalCameraPreviewUseCase @Inject constructor(
    private val internalCameraRepository: InternalCameraRepositoryInterface
) {
    suspend operator fun invoke(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) = internalCameraRepository.showCameraPreview(
        previewView,
        lifecycleOwner
    )
}