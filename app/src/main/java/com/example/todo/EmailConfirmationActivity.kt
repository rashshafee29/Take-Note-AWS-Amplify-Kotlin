package com.example.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.amplifyframework.datastore.DataStoreException

import android.content.Intent

import com.amplifyframework.datastore.DataStoreItemChange

import com.amplifyframework.core.Amplify

import com.amplifyframework.auth.result.AuthSignInResult

import com.amplifyframework.auth.result.AuthSignUpResult

import android.widget.EditText

import android.view.View
import android.view.Window
import android.view.WindowManager
import com.amplifyframework.auth.AuthException
import com.amplifyframework.core.Consumer
import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.generated.model.User


class EmailConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_confirmation)
        supportActionBar!!.hide()
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    fun onConfirmButtonPressed(view: View?) {
        // 1. Confirm the code
        // 2. Re-login
        // 3. Save the user details such names in Data-store
        val txtConfirmationCode = findViewById<EditText>(R.id.txtConfirmationCode)
        Amplify.Auth.confirmSignUp(
            getEmail()!!,
            txtConfirmationCode.text.toString(),
            { authSignUpResult: AuthSignUpResult ->
                this.onSuccess(
                    authSignUpResult
                )
            },
            this::onError
        )
    }

    private fun onError(e: AuthException) {
        runOnUiThread {
            Toast
                .makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun onSuccess(authSignUpResult: AuthSignUpResult) {
        reLogin()
    }

    private fun reLogin() {
        val username = getEmail()
        val password = getPassword()
        Amplify.Auth.signIn(
            username,
            password,
            { authSignInResult: AuthSignInResult ->
                onLoginSuccess(
                    authSignInResult
                )
            },
            this::onError
        )
    }

    private fun onLoginSuccess(authSignInResult: AuthSignInResult) {
        val userId = Amplify.Auth.currentUser.userId
        val name = getName()
        Amplify.DataStore.save(
            User.builder().id(userId).name(name).build(),
        this::onSavedSuccess,
        this::onError)
    }

    private fun <T : Model?> onSavedSuccess(tDataStoreItemChange: DataStoreItemChange<T>) {
        val intent = Intent(this, TodoListActivity::class.java)
        startActivity(intent)
    }

    private fun onError(e: DataStoreException) {
        runOnUiThread {
            Toast
                .makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getName(): String? {
        return intent.getStringExtra("name")
    }

    private fun getPassword(): String? {
        return intent.getStringExtra("password")
    }

    private fun getEmail(): String? {
        return intent.getStringExtra("email")
    }
}