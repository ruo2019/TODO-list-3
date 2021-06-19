/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.conkermobile.android.todoList.todoTracker

import android.app.Application
import androidx.lifecycle.*
import com.conkermobileX.getlivedata.GetLiveData
import com.conkermobile.android.todoList.database.TodoDatabaseDao
import com.conkermobile.android.todoList.database.TodoItem
import com.conkermobile.android.todoList.formatNights
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for TodoTrackerFragment.
 */
class TodoTrackerViewModel(
    val database: TodoDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val nights = database.getAllNights()

    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    private val _navigateToSleepQuality = GetLiveData<TodoItem?>()
    val navigateToSleepQuality = _navigateToSleepQuality.liveData

    val clearButtonEnabled = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = GetLiveData<Boolean>()

    val showSnackBarEvent = _showSnackbarEvent.liveData

    fun onStartTracking() {
        Timber.i("stop tracking")
        viewModelScope.launch {
            Timber.i("launching")
            // In Kotlin, the return@label syntax specifies the function from which
            // this statement returns, among several nested functions.
            Timber.i("updating")
            Timber.i("navigating to todoTask")
            _navigateToSleepQuality.value = TodoItem()
        }
    }

    private suspend fun insert(night: TodoItem) {
        database.insert(night)
    }

//    fun onStopTracking() {
//        viewModelScope.launch {
//            // In Kotlin, the return@label syntax specifies the function from which
//            // this statement returns, among several nested functions.
//            val oldNight = tonight.value ?: return@launch
//            oldNight.endTimeMilli = System.currentTimeMillis()
//            update(oldNight)
//
//            _navigateToSleepQuality.value = oldNight
//        }
//
//    }

    private suspend fun update(night: TodoItem) {
        database.update(night)
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        database.clear()
        _showSnackbarEvent.value = true
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
}

