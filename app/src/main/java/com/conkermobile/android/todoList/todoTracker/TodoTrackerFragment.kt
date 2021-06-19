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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conkermobile.android.todoList.R
import com.conkermobile.android.todoList.database.TodoDatabase
import com.conkermobile.android.todoList.databinding.FragmentTodoTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class TodoTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentTodoTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_todo_tracker, container, false
        )

        // need a reference to the app that this fragment is attached to, to pass into the view-model factory provider.
        // The requireNotNull Kotlin function throws an IllegalArgumentException if the value is null.
        // val application = this.activity?.application
        val application = requireNotNull(this.activity).application

        // a reference to the data source via a reference to the DAO.
        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao

        // create an instance of the viewModelFactory. You need to pass it the dataSource and the application.
        val viewModelFactory = TodoTrackerViewModelFactory(dataSource, application)

        // Now that you have a factory, get a reference to the TodoTrackerViewModel.
        // The TodoTrackerViewModel::class.java parameter refers to the runtime Java class of this object.
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory).get(TodoTrackerViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, { night ->
            if (night != null) {
                this.findNavController().navigate(
                    TodoTrackerFragmentDirections.actionTodoTrackerFragmentToTodoTaskFragment(
                        night.taskId
                    )
                )
                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                activity?.let { activity ->
                    Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    sleepTrackerViewModel.doneShowingSnackbar()
                }
            }
        })
        return binding.root
    }
}