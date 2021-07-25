package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAuth()
    }

    private fun configureAuth() {
        val currentUser = Amplify.Auth.currentUser

        val intent: Intent = if (currentUser == null) {
            Intent(applicationContext, LoginActivity::class.java)
        } else {
            Intent(applicationContext, TodoListActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}