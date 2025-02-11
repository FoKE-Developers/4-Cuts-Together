package com.foke.together.presenter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foke.together.domain.interactor.GetCameraSourceTypeUseCase
import com.foke.together.domain.interactor.GetExternalCameraIPUseCase
import com.foke.together.domain.interactor.SetCameraSourceTypeUseCase
import com.foke.together.domain.interactor.SetExternalCameraIPUseCase
import com.foke.together.domain.interactor.entity.CameraSourceType
import com.foke.together.domain.interactor.entity.ExternalCameraIP
import com.foke.together.util.AppLog
import com.foke.together.util.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    getCameraSourceTypeUseCase: GetCameraSourceTypeUseCase,
    private val setCameraSourceTypeUseCase: SetCameraSourceTypeUseCase,
    getExternalCameraIPUseCase: GetExternalCameraIPUseCase,
    private val setExternalCameraIPUseCase: SetExternalCameraIPUseCase
): ViewModel() {
    val cameraSourceType = getCameraSourceTypeUseCase().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    // TODO: check to remove
    val cameraIPAddress = getExternalCameraIPUseCase().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    var cameraIPAddressState by mutableStateOf("")

    init {
        viewModelScope.launch(ioDispatcher) {
            cameraIPAddress.collectLatest {
                cameraIPAddressState = it.address
            }
        }
    }

    fun setCameraSourceType(index: Int){
        setCameraSourceType(CameraSourceType.entries[index])
    }

    fun setCameraSourceType(type: CameraSourceType){
        viewModelScope.launch {
            AppLog.e(TAG, "setCameraSourceType", "type: $type")
            setCameraSourceTypeUseCase(type)
        }
    }

    fun setCameraIPAddress(address: String){
        viewModelScope.launch {
            setExternalCameraIPUseCase(ExternalCameraIP(address))
        }
    }

    companion object {
        private val TAG = SettingViewModel::class.java.simpleName
    }
}