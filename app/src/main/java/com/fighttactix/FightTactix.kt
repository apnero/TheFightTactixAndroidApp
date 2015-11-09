package com.fighttactix

import android.app.Application
import com.fighttactix.model.Attendance
import com.fighttactix.model.Cards
//import com.facebook.FacebookSdk
import com.fighttactix.model.Meeting
import com.parse.Parse
import com.parse.ParseFacebookUtils
import com.parse.ParseInstallation
import com.parse.ParseObject

/**
 * Created by Suleiman on 24-06-2015.
 */
class FightTactix:Application() {
    override fun onCreate() {
        super.onCreate()


        Parse.enableLocalDatastore(this)
        ParseObject.registerSubclass(Meeting::class.java)
        ParseObject.registerSubclass(Attendance::class.java)
        ParseObject.registerSubclass(Cards::class.java)
        Parse.initialize(this, "A7hkeyC96XycUj3dPqplGO5ltPYyu1PXT39O663R", "ZsKBS95aegUHNght50r8INIiAro0Siyw6diLDXba")
        ParseInstallation.getCurrentInstallation().saveInBackground();

        //FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
    }
}