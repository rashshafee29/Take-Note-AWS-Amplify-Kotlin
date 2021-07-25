package com.example.todo

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.AmplifyModelProvider
import com.amplifyframework.datastore.generated.model.TakeNote
import com.amplifyframework.datastore.generated.model.Todo

class AmplifyConfigure: Application() {

    private var TAG = "TakeNote-AWS"
    override fun onCreate() {
        super.onCreate()
        try {
            val modelProvider = AmplifyModelProvider.getInstance()
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSDataStorePlugin(modelProvider))
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (failure: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", failure)
        }
    }


}