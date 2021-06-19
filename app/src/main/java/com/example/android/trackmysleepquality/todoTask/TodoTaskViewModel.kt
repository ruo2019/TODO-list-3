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

package com.example.android.trackmysleepquality.todoTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conkermobileX.getlivedata.GetLiveData
import com.example.android.trackmysleepquality.database.TodoDatabaseDao
import com.example.android.trackmysleepquality.database.TodoItem
import kotlinx.coroutines.launch

class TodoTaskViewModel(
    private val sleepNightKey: Long = 0L,
    val database: TodoDatabaseDao
) :
    ViewModel() {

    private val _navigateToSleepTracker = GetLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker



//    fun onStopTracking() {
//        viewModelScope.launch {
//            // In Kotlin, the return@label syntax specifies the function from which
//            // this statement returns, among several nested functions.
//            val oldNight = tonight.value ?: return@launch
//            oldNight.endTimeMilli = System.currentTimeMillis()
//            update(oldNight)
//
//            _navigateToSleepTracker.value = oldNight
//        }
//
//    }

    private suspend fun update(night: TodoItem) {
        database.update(night)
    }

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    fun onSetTodoTask(task: String) {
        viewModelScope.launch {
            val tonight = database.get(sleepNightKey) ?: return@launch
            tonight.taskDetails = task
            database.update(tonight)


            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }

}