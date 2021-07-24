package com.example.todo

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var TAG = "TODO-AWS"
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var todoListRecyclerViewAdapter: TodoListRecyclerViewAdapter
    private lateinit var fabIcon: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val todoList: ArrayList<Todo> = ArrayList()
        fabIcon = findViewById(R.id.id_fab)
        recyclerView = findViewById(R.id.id_recycler_view)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        todoListRecyclerViewAdapter = TodoListRecyclerViewAdapter(this, todoList)
        recyclerView.adapter = todoListRecyclerViewAdapter

        fabIcon.setOnClickListener {
            showTodoForm(this)
        }
        configureAmplify()
        queryTodoList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.todo_menu, menu)
        val searchItem: MenuItem = menu!!.findItem(R.id.id_search_button)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoListRecyclerViewAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    private fun showTodoForm(context: Context) {
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_todo_form)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val todoTitle: EditText = dialog.findViewById(R.id.id_task_name)
        val todoDesc: EditText = dialog.findViewById(R.id.id_task_description)
        val todoPriority: Spinner = dialog.findViewById(R.id.id_pr_spinner)
        val errorText: TextView = dialog.findViewById(R.id.id_error_text)
        errorText.visibility = View.GONE

        val okayBtn = dialog.findViewById<Button>(R.id.id_dialog_btn_okay)
        val cancelBtn = dialog.findViewById<Button>(R.id.id_dialog_btn_cancel)
        okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
        cancelBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.high_pr))

        okayBtn.setOnClickListener {
            if (todoTitle.text.toString().isEmpty() || todoDesc.text.toString().isEmpty()) {
                errorText.visibility = View.VISIBLE
            } else {
                errorText.visibility = View.GONE
                createTodoItem(
                    todoTitle.text.toString(),
                    todoDesc.text.toString(),
                    todoPriority.selectedItem.toString()
                )
                dialog.dismiss()
                queryTodoList()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
    }

    private fun configureAmplify() {

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (failure: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", failure)
        }

        Amplify.DataStore.observe(Todo::class.java,
            { Log.i(TAG, "Observation began.") },
            { Log.i(TAG, it.item().toString()) },
            { Log.e(TAG, "Observation failed.", it) },
            { Log.i(TAG, "Observation complete.") }
        )
    }

    private fun queryTodoList() {
        val todoList: ArrayList<Todo> = ArrayList()
        Amplify.DataStore.query(Todo::class.java,
            { todos ->
                while (todos.hasNext()) {
                    val todo: Todo = todos.next()
                    todoList.add(todo)
                    Log.i(TAG, "==== Todo ====")
                    Log.i(TAG, "Name: ${todo.name}")
                    Log.i(TAG, "Priority: ${todo.priority}")
                    Log.i(TAG, "Description: ${todo.description}")
                }
                todoList.reverse()
                runOnUiThread {
                    todoListRecyclerViewAdapter.setData(todoList)
                }
            },
            { Log.e(TAG, "Could not query DataStore", it) }
        )
    }

    private fun createTodoItem(todoName: String, todoDesc: String, todoPr: String) {
        val priority: Priority? = when (todoPr) {
            "LOW" -> Priority.LOW
            "NORMAL" -> Priority.NORMAL
            "HIGH" -> Priority.HIGH
            else -> null
        }
        val todoItem = Todo.builder()
            .name(todoName)
            .priority(priority)
            .description(todoDesc)
            .build()

        storeData(todoItem)
    }

    private fun storeData(todoItem: Todo) {
        Amplify.DataStore.save(todoItem,
            { Log.i(TAG, "Saved item: ${todoItem.name}") },
            { Log.e(TAG, "Could not save item to DataStore", it) }
        )
    }
}