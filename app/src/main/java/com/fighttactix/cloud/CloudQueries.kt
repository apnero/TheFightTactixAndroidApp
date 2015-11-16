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
object CloudQueries {

    var numOfClassesUserAttended:Int = 0
    var numOfPunchCardCredits:Int = 0
    var nextClass: Meeting = Meeting()
    var registeredNextClass: ArrayList<Attendance> = ArrayList<Attendance>()


    public fun numOfClassesUserAttended(){

        ParseCloud.callFunctionInBackground("numOfClassesUserAttended", HashMap<String, Unit>(), object: FunctionCallback<Int> {
            override fun done(number:Int?, e: ParseException?) {
                if (e == null)
                {
                     numOfClassesUserAttended = number!!
                    Log.v("Cloud Queries numOfClassesUserAttended", number.toString() )
                }
                else
                {
                    Log.v("Cloud Queries numOfClassesUserAttended Tag", e.toString() )
                }
            }
        })
    }


    public fun numOfPunchCardCredits(){

        ParseCloud.callFunctionInBackground("numOfPunchCardCredits", HashMap<String, Unit>(), object: FunctionCallback<Int> {
            override fun done(number:Int?, e: ParseException?) {
                if (e == null)
                {
                    numOfPunchCardCredits = number!!
                    Log.v("Cloud Queries numOfPunchCardCredits", number.toString() )
                }
                else
                {
                    Log.v("Cloud Queries numOfPunchCardCredits Tag", e.toString() )
                }
            }
        })
    }

    public fun nextClass(){

        ParseCloud.callFunctionInBackground("nextClass", HashMap<String, Unit>(), object: FunctionCallback<Meeting> {
            override fun done(meetings: Meeting?, e: ParseException?) {
                if (e == null)
                {
                    nextClass = meetings!!
                    Log.v("Cloud Queries nextClass", meetings.toString() )
                }
                else
                {
                    Log.v("Cloud Queries nextClass Tag", e.toString() )
                }
            }
        })
    }

    public fun registeredNextClass(){

        ParseCloud.callFunctionInBackground("registeredNextClass", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Attendance>> {
            override fun done(users: ArrayList<Attendance>?, e: ParseException?) {
                if (e == null)
                {
                    registeredNextClass = users!!
                    Log.v("Cloud Queries registeredNextClass", users.toString() )
                }
                else
                {
                    Log.v("Cloud Queries registeredNextClass Tag", e.toString() )
                }
            }
        })
    }

}