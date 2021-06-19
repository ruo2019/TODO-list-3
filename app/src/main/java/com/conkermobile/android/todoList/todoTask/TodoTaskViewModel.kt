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

package com.conkermobile.android.todoList.todoTask

import androidx.lifecycle.*
import com.conkermobileX.getlivedata.GetLiveData
import com.conkermobile.android.todoList.database.TodoDatabaseDao
import com.conkermobile.android.todoList.database.TodoItem
import kotlinx.coroutines.launch

class TodoTaskViewModel(
    private val sleepNightKey: Long = 0L,
    val database: TodoDatabaseDao
) :
    ViewModel() {

    private val _text =  GetLiveData<String>()
    val text = _text.liveData

    private val _important = MutableLiveData(false)
    val important : LiveData<Boolean> get() = _important

    private val _navigateToSleepTracker = GetLiveData<Boolean?>()

    val navigateToSleepTracker = _navigateToSleepTracker.liveData
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

    fun onDone() {
        viewModelScope.launch {
            val task = TodoItem(sleepNightKey, isImportant = _important.value!!, taskDetails = _text.value!!)
            database.insert(task)
            _navigateToSleepTracker.value = true
        }
    }

    private suspend fun update(night: TodoItem) {
        database.update(night)
    }

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

//    fun onSetTodoTask(task: String) {
//        viewModelScope.launch {
//            val tonight = database.get(sleepNightKey) ?: return@launch
//            tonight.taskDetails = task
//            database.update(tonight)
//
//
//            // Setting this state variable to true will alert the observer and trigger navigation.
//            _navigateToSleepTracker.value = true
//        }
//    }

    fun setText(text: String) {
        _text.value = text
    }

    fun setImportant(important: Boolean) {
        _important.value = important
    }

}