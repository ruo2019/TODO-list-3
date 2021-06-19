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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDatabaseDao{

    @Insert
    suspend fun insert(night:TodoItem)

    @Update
    suspend fun update(night: TodoItem)

    @Query("SELECT * from all_tasks_table WHERE taskId = :key")
    suspend fun get(key: Long): TodoItem?

    @Query("DELETE FROM all_tasks_table")
    suspend fun clear()

    @Query("SELECT * FROM all_tasks_table ORDER BY taskId DESC LIMIT 1")
    suspend fun getTonight(): TodoItem?

    // no need for suspend keyword because Room already uses a background thread for
    // that specific @Query which returns LiveData.
    @Query("SELECT * FROM all_tasks_table ORDER BY taskId DESC")
    fun getAllNights(): LiveData<List<TodoItem>>
}
