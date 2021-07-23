package com.example.todo

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import android.R.attr.value

import android.R.attr.text

class MainActivity : AppCompatActivity() {
    private var TAG = "TODO-AWS"
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var todoListRecyclerViewAdapter: TodoListRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val todoList: ArrayList<Todo> = ArrayList()
        recyclerView = findViewById(R.id.id_recycler_view)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        todoListRecyclerViewAdapter = TodoListRecyclerViewAdapter(applicationContext, todoList)
        recyclerView.adapter = todoListRecyclerViewAdapter
        configureAmplify()
//        createTodoItem()
        queryTodoList()
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
            Amplify.DataStore.query(Todo::class.java,
            { todos ->
                while (todos.hasNext()) {
                    val todo: Todo = todos.next()
                    runOnUiThread{
                        todoListRecyclerViewAdapter.updateData(todo)
                    }
                    Log.i(TAG, "==== Todo ====")
                    Log.i(TAG, "Name: ${todo.name}")
                    Log.i(TAG, "Priority: ${todo.priority}")
                    Log.i(TAG, "Description: ${todo.description}")
                }
            },
            { Log.e(TAG, "Could not query DataStore", it)  }
        )
    }

    private fun createTodoItem() {
//        val todoItem = Todo.builder()
//            .name("First todo")
//            .description("First khela")
//            .build()
        val todoItem = Todo.builder()
            .name("Second todo")
            .priority(Priority.LOW)
            .description("Second khela")
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