package com.fighttactix

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.fighttactix.cloud.CloudQueries
import com.parse.*
import org.json.JSONException
import org.json.JSONObject
import java.util.Arrays


class LoginActivity:Activity() {


    override protected fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.
        val currentUser = ParseUser.getCurrentUser()
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser))
        {
            // Go to the user info activity
            startMainActivity()
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data)
    }

    fun onLoginClick(v:View) {
        val progressDialog:Dialog = ProgressDialog.show(this@LoginActivity, "", "Logging in...", true)
        val permissions = Arrays.asList<String>("public_profile", "email")


        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, object:LogInCallback {
            override fun done(user:ParseUser?, err:ParseException?) {
                progressDialog.dismiss()
                if (user == null)
                {
                    Log.d("mytagg", "Uh oh. The user cancelled the Facebook login.")
                }
                else if (user.isNew())
                {
                    Log.d("mytagg", "User signed up and logged in through Facebook!")
                    makeMeRequest()
                    startMainActivity()
                }
                else
                {
                    Log.d("mytagg", "User logged in through Facebook!")
                    startMainActivity()
                }
            }
        })
    }


    private fun makeMeRequest() {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                object: GraphRequest.GraphJSONObjectCallback {
                    override fun onCompleted(jsonObject: JSONObject?, graphResponse: GraphResponse) {
                        if (jsonObject != null) {
                            val userProfile = JSONObject()
                            try {
                                val currentUser = ParseUser.getCurrentUser()

                                userProfile.put("facebookId", jsonObject.getLong("id"))
                                userProfile.put("name", jsonObject.getString("name"))
                                currentUser.put("username", jsonObject.getString("name"))
                                if (jsonObject.has("gender")) userProfile.put("gender", jsonObject.getString("gender"))
                                if (jsonObject.has("email")) {
                                    userProfile.put("email", jsonObject.getString("email"))
                                    currentUser.put("email", jsonObject.getString("email"))
                                }
                                // Save the user profile info in a user property
                                currentUser.put("profile", userProfile)
                                currentUser.saveInBackground()

                            } catch (e: JSONException) {
                                Log.d("MyTagg",
                                        "Error parsing returned user data. " + e)
                            }
                        } else if (graphResponse.getError() != null) {
                            Log.d("My Tagg", "error: " + graphResponse.getError())

                        }
                    }
                })
        val parameters = Bundle()
        parameters.putString("fields", "id,email,gender,name")
        request.setParameters(parameters)
        request.executeAsync()
    }


    private fun startMainActivity() {
        CloudQueries.userAdministrator(this)

        var subscribedChannels:List<String>? = ParseInstallation.getCurrentInstallation().getList("channels")
        if(subscribedChannels?.contains("Tactix") == false){
            ParsePush.subscribeInBackground("Tactix")
        }
        val username: String = (ParseUser.getCurrentUser()).username.replace(" ", "")
        if(subscribedChannels?.contains(username) == false){
            ParsePush.subscribeInBackground(username)
        }
        if(subscribedChannels == null){
            ParsePush.subscribeInBackground("Tactix")
            ParsePush.subscribeInBackground(username)
        }

    }
}