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

        ParseCloud.callFunctionInBackground("adminCheckInSave", userCheckInStatus, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries adminCheckInSave", response.toString() )
                //CloudQueries.allUserAttendance()//CloudQueries.registeredNextClass()
                //CloudQueries.userClassHistory()
            } else {
                //Log.v("Cloud Queries adminCheckInSave Tag", e.toString() )
            }
        })


    }


    public fun saveNewCard(cardSaveInfo:HashMap<String, String>){

        ParseCloud.callFunctionInBackground("saveNewCard", cardSaveInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries saveNewCard", response )
                //CloudQueries.allUserCards()
                //CloudQueries.userPunchCards()
            } else {
                //Log.v("Cloud Queries saveNewCard Tag", e.toString() )
            }
        })


    }

    public fun push(pushInfo:HashMap<String, String>){
        ParseCloud.callFunctionInBackground("push", pushInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries push", response )
                //CloudQueries.allUserCards()
            } else {
                //Log.v("Cloud Queries push Tag", e.toString() )
            }
        })

    }


    public fun registerForClass(registrationInfo:HashMap<String, Date>){

        ParseCloud.callFunctionInBackground("registerForClass", registrationInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries registerForClass", response )
                CloudQueries.userClassHistory()
                //CloudQueries.registeredNextClass()
            } else {
                //Log.v("Cloud Queries registerForClass Tag", e.toString() )
            }
        })
    }


    public fun unRegisterForClass(registrationInfo:HashMap<String, Date>){

        ParseCloud.callFunctionInBackground("unRegisterForClass", registrationInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries unRegisterForClass", response )
                CloudQueries.userClassHistory()
                //CloudQueries.registeredNextClass()
            } else {
                //Log.v("Cloud Queries unRegisterForClass Tag", e.toString() )
            }
        })
    }


    public fun adminDeleteMeeting(meetingId: HashMap<String, String>, attendanceList: ArrayList<Attendance>){


        for (attendance in attendanceList){
            var hmap = HashMap<String, String>()
            hmap.put("objectId", attendance.objectId)
            adminDeleteAttendance(hmap)
        }


        ParseCloud.callFunctionInBackground("adminDeleteMeeting", meetingId, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                Log.v("Cloud Queries adminDeleteMeeting", response )
                CloudQueries.currentSchedule()
                //CloudQueries.nextClass()
                //CloudQueries.registeredNextClass()
                CloudQueries.recentSchedule()
                //CloudQueries.allUserAttendance()
                //CloudQueries.allUserCards()
            } else {
                //Log.v("Cloud Queries adminDeleteMeeting Tag", e.toString() )
            }
        })
    }


    public fun adminDeleteAttendance(AttendanceId: HashMap<String, String>){

        ParseCloud.callFunctionInBackground("adminDeleteAttendance", AttendanceId, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries adminDeleteAttendance", response )
            } else {
                //Log.v("Cloud Queries adminDeleteAttendance Tag", e.toString() )
            }
        })
    }

    public fun adminAddMeeting(meetingInfo: HashMap<String, String>){

        ParseCloud.callFunctionInBackground("adminAddMeeting", meetingInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries adminAddMeeting", response )
                //CloudQueries.nextClass()
                CloudQueries.currentSchedule()
                //CloudQueries.registeredNextClass()
            } else {
                //Log.v("Cloud Queries adminAddMeeting Tag", e.toString() )
            }
        })
    }

    public fun adminModifyMeeting(meetingInfo: HashMap<String, String>){

        ParseCloud.callFunctionInBackground("adminModifyMeeting", meetingInfo, FunctionCallback<kotlin.String> { response, e ->
            if (e == null) {
                //Log.v("Cloud Queries adminModifyMeeting", response )
                //CloudQueries.nextClass()
                CloudQueries.currentSchedule()
                //CloudQueries.registeredNextClass()
            } else {
                //Log.v("Cloud Queries adminModifyMeeting Tag", e.toString() )
            }
        })
    }

//
//    public fun saveNotification(notificationInfo: HashMap<String, String>){
//
//        ParseCloud.callFunctionInBackground("saveNotification", notificationInfo, FunctionCallback<kotlin.String> { response, e ->
//            if (e == null) {
//                //Log.v("Cloud Queries adminAddMeeting", response )
//
//            } else {
//                //Log.v("Cloud Queries adminAddMeeting Tag", e.toString() )
//            }
//        })
//    }


}