package com.example.todo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.TakeNote
import com.amplifyframework.datastore.generated.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TodoListActivity : AppCompatActivity() {
    private var TAG = "TodoListActivity-Dota"
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var todoListRecyclerViewAdapter: TodoListRecyclerViewAdapter
    private lateinit var fabIcon: FloatingActionButton
    private lateinit var profileIcon: FloatingActionButton
    private lateinit var logoutIcon: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureRecyclerView()
        queryTodoList()
        syncCloud()
        profileIcon = findViewById(R.id.id_profile)
        profileIcon.setOnClickListener {

        }

        logoutIcon = findViewById(R.id.id_log_out)
        logoutIcon.setOnClickListener {
            Amplify.Auth.signOut(
                {
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                },
                {
                    Toast.makeText(applicationContext, "Logout failed", Toast.LENGTH_SHORT).show()
                } )
        }
    }

    private fun syncCloud() {
        Amplify.DataStore.observe(
            TakeNote::class.java,
            {   //queryTodoList()
                Log.i(TAG, "Observation began.")
            },
            {
                queryTodoList()
                Log.i(TAG, "syncCloud-> " + it.item().toString())
            },
            { Log.e(TAG, "Observation failed.", it) },
            {
                Log.i(TAG, "Observation complete.")
            }
        )
    }

    private fun configureRecyclerView() {
        val todoList: ArrayList<TakeNote> = ArrayList()
        fabIcon = findViewById(R.id.id_fab)
        recyclerView = findViewById(R.id.id_recycler_view)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        todoListRecyclerViewAdapter = TodoListRecyclerViewAdapter(this, todoList)
        recyclerView.adapter = todoListRecyclerViewAdapter

        fabIcon.setOnClickListener {
            showTodoForm(this)
        }
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
                //queryTodoList()
                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
    }

    private fun queryTodoList() {
        val currUser = Amplify.Auth.currentUser
        val todoList: ArrayList<TakeNote> = ArrayList()
        Amplify.DataStore.query(
            TakeNote::class.java, Where.matches(TakeNote.USER.eq(currUser.userId)),
            { todos ->
                Log.i(TAG, "todos.size(): " + todos.hasNext())
                while (todos.hasNext()) {
                    val todo: TakeNote = todos.next()
                    todoList.add(todo)
                    Log.i(TAG, "queryTodoList ==== Todo ====")
                    Log.i(TAG, "queryTodoList Name: ${todo.name}")
                    Log.i(TAG, "queryTodoList Priority: ${todo.priority}")
                    Log.i(TAG, "queryTodoList Description: ${todo.description}")
                }
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
        val currUser = Amplify.Auth.currentUser

        val takeNoteItem =
            TakeNote.Builder()
                .name(todoName).description(todoDesc).priority(priority)
                .user(User.justId(currUser.userId)).build()

        storeData(takeNoteItem)
    }

    private fun storeData(takeNoteItem: TakeNote) {
        Amplify.DataStore.save(takeNoteItem,
            { Log.i(TAG, "storeData-> Saved item: ${takeNoteItem.name}") },
            { Log.e(TAG, "storeData-> Could not save item to DataStore", it) }
        )
    }
}