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
                    CloudQueries.userClassHistory()
                }
                else
                {
                    Log.v("Cloud Queries adminCheckInSave Tag", e.toString() )
                }
            }
        })


    }


    public fun saveNewCard(cardSaveInfo:HashMap<String, String>){

        ParseCloud.callFunctionInBackground("saveNewCard", cardSaveInfo, object: FunctionCallback<String> {
            override fun done(response:String?, e: ParseException?) {
                if (e == null)
                {
                    Log.v("Cloud Queries saveNewCard", response )
                    CloudQueries.allUserCards()
                    CloudQueries.userPunchCards()
                }
                else
                {
                    Log.v("Cloud Queries saveNewCard Tag", e.toString() )
                }
            }
        })


    }

    public fun push(pushInfo:HashMap<String, String>){
        ParseCloud.callFunctionInBackground("push", pushInfo, object: FunctionCallback<String> {
            override fun done(response:String?, e: ParseException?) {
                if (e == null)
                {
                    Log.v("Cloud Queries push", response )
                    //CloudQueries.allUserCards()
                }
                else
                {
                    Log.v("Cloud Queries push Tag", e.toString() )
                }
            }
        })

    }


    public fun registerForClass(registrationInfo:HashMap<String, Date>){

        ParseCloud.callFunctionInBackground("registerForClass", registrationInfo, object: FunctionCallback<String> {
            override fun done(response:String?, e: ParseException?) {
                if (e == null)
                {
                    Log.v("Cloud Queries registerForClass", response )
                    CloudQueries.userClassHistory()
                    CloudQueries.registeredNextClass()
                }
                else
                {
                    Log.v("Cloud Queries registerForClass Tag", e.toString() )
                }
            }
        })
    }


    public fun unRegisterForClass(registrationInfo:HashMap<String, Date>){

        ParseCloud.callFunctionInBackground("unRegisterForClass", registrationInfo, object: FunctionCallback<String> {
            override fun done(response:String?, e: ParseException?) {
                if (e == null)
                {
                    Log.v("Cloud Queries unRegisterForClass", response )
                    CloudQueries.userClassHistory()
                    CloudQueries.registeredNextClass()
                }
                else
                {
                    Log.v("Cloud Queries unRegisterForClass Tag", e.toString() )
                }
            }
        })
    }



}