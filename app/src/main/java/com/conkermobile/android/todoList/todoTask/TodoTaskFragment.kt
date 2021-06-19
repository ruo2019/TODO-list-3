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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conkermobile.android.todoList.R
import com.conkermobile.android.todoList.database.TodoDatabase
import com.conkermobile.android.todoList.databinding.FragmentTodoTaskBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class TodoTaskFragment : Fragment() {

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
        val binding: FragmentTodoTaskBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_todo_task, container, false
        )

        val application = requireNotNull(this.activity).application

        val arguments = TodoTaskFragmentArgs.fromBundle(requireArguments())
        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao
        val viewModelFactory = TodoTaskViewModelFactory(arguments.todoId, dataSource)
        val todoTaskViewModel =
            ViewModelProvider(this, viewModelFactory).get(TodoTaskViewModel::class.java)

        binding.todoTaskViewModel = todoTaskViewModel

        todoTaskViewModel.navigateToSleepTracker.observe(viewLifecycleOwner, { task ->
            if (task != null) {
                this.findNavController().navigate(
                    TodoTaskFragmentDirections.actionTodoTaskFragmentToTodoTrackerFragment(
                    )
                )
                todoTaskViewModel.doneNavigating()
            }
        })

        binding.editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            /**
             * This method is called to notify you that, somewhere within
             * `s`, the text has been changed.
             * It is legitimate to make further changes to `s` from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [Spannable.setSpan] in [.onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(the_text_in_the_edittext: Editable) {
                todoTaskViewModel.setText(the_text_in_the_edittext.toString())
                binding.doneButtonEnabled = the_text_in_the_edittext.isNotEmpty()
            }
        })
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            todoTaskViewModel.setImportant(isChecked)
        }

        return binding.root
    }
}
