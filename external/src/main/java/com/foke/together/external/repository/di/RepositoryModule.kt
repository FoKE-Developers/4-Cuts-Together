package com.foke.together.external.repository.di

import com.foke.together.domain.output.ExternalCameraRepositoryInterface
import com.foke.together.domain.output.ImageRepositoryInterface
import com.foke.together.domain.output.InternalCameraRepositoryInterface
import com.foke.together.domain.output.QRCodeRepositoryInterface
import com.foke.together.domain.output.SessionRepositoryInterface
import com.foke.together.external.repository.ExternalCameraRepository
import com.foke.together.external.repository.ImageRepository
import com.foke.together.external.repository.InternalCameraRepository
import com.foke.together.external.repository.QRCodeRepository
import com.foke.together.external.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindExternalCameraRepository(
        externalCameraRepository: ExternalCameraRepository
    ): ExternalCameraRepositoryInterface

    @Singleton
    @Binds
    abstract fun bindInternalCameraRepository(
        internalCameraRepository: InternalCameraRepository
    ): InternalCameraRepositoryInterface

    @Singleton
    @Binds
    abstract fun bindImageRepository(
        imageRepository: ImageRepository
    ): ImageRepositoryInterface

    @Singleton
    @Binds
    abstract fun bindQRCodeRepository(
        qrCodeRepository: QRCodeRepository
    ): QRCodeRepositoryInterface

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        sessionRepository: SessionRepository
    ): SessionRepositoryInterface
}