package com.fighttactix.cloud

import android.util.Log
import com.fighttactix.model.Attendance
import com.fighttactix.model.Meeting
import com.parse.FunctionCallback
import com.parse.ParseCloud
import com.parse.ParseException
import com.parse.ParseObject
import java.util.*

/**
 * Created by Andrew on 11/14/2015.
 */
object CloudCalls {

    public fun adminCheckInSave(userCheckInStatus:HashMap<String, String>){

        ParseCloud.callFunctionInBackground("adminCheckInSave", userCheckInStatus, object: FunctionCallback<String> {
            override fun done(response:String?, e: ParseException?) {
                if (e == null)
                {
                    Log.v("Cloud Queries adminCheckInSave", response.toString() )
                    CloudQueries.registeredNextClass()
                }
                else
                {
                    Log.v("Cloud Queries adminCheckInSave Tag", e.toString() )
                }
            }
        })
    }



}