package com.ivy.backupdata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivy.legacy.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BackupViewModel @Inject constructor() : ViewModel() {

    private val _backupStep = MutableLiveData<BackupStep>()
    val backupStep = _backupStep.asLiveData()

}
