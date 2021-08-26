package com.example.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

import android.view.View

import com.amplifyframework.auth.result.AuthSignInResult

import android.widget.Toast

import com.amplifyframework.core.Amplify


import android.widget.EditText
import com.amplifyframework.auth.AuthException
import android.view.WindowManager

import android.os.Build
import android.view.Window


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    fun onPressLogin(view: View?) {
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        Amplify.Auth.signIn(
            txtEmail.text.toString(),
            txtPassword.text.toString(),
            { authSignInResult: AuthSignInResult ->
                onLoginSuccess(
                    authSignInResult
                )
            }
        ) { e: AuthException ->
            onLoginError(
                e
            )
        }
    }

    private fun onLoginError(e: AuthException) {
        runOnUiThread {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun onLoginSuccess(authSignInResult: AuthSignInResult) {
        val intent = Intent(this, TodoListActivity::class.java)
        startActivity(intent)
    }

    fun onJoinPressed(view: View?) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}