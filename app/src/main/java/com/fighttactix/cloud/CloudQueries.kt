package com.fighttactix.cloud

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.fighttactix.LoginActivity
import com.fighttactix.MainActivity
import com.fighttactix.model.*
import com.parse.*
import org.json.JSONObject
import java.util.*

/**
 * Created by Andrew on 11/14/2015.
 */
object CloudQueries {

    var maxClassSize:Int? = 99
    var numOfClassesUserAttended:Int = 0
    var numOfPunchCardCredits:Int = 0
//    var nextClass: Meeting? = Meeting()
//    var checkinClass: Meeting? = Meeting()
//    var registeredNextClass: ArrayList<Attendance>? = ArrayList<Attendance>()
    var currentSchedule: ArrayList<Meeting>? = ArrayList<Meeting>()
    var recentSchedule: ArrayList<Meeting>? = ArrayList<Meeting>()
    var userClassHistory: ArrayList<Attendance>? = ArrayList<Attendance>()
    var userAttendedHistory: ArrayList<Attendance> = ArrayList<Attendance>()
    var userPunchCards: ArrayList<Cards>? = ArrayList<Cards>()
    //var allUserCards: ArrayList<Cards>? = ArrayList<Cards>()
    //var allUserAttendance: ArrayList<Attendance>? = ArrayList<Attendance>()
    var allUsers: ArrayList<ParseUser>? = ArrayList<ParseUser>()
    //var userAttendanceCount: ArrayList<UserAttendanceCount>? = ArrayList<UserAttendanceCount>()
    //var userCardSum: ArrayList<UserCardSum>? = ArrayList<UserCardSum>()
    var userCounts: ArrayList<UserCounts> = ArrayList<UserCounts>()

    var userAdministrator: Boolean = false
    var locations: ArrayList<Location>? = ArrayList<Location>()
    var currentEnrolled: ArrayList<CurrentAttendance>? = ArrayList<CurrentAttendance>()
    var notifications: ArrayList<Notifications>? = ArrayList<Notifications>()

    public fun userPunchCards(){

        ParseCloud.callFunctionInBackground("userPunchCards", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Cards>> { cards, e ->
            if (e == null) {
                numOfPunchCardCredits = 0
                userPunchCards = cards
                if (cards != null)
                    for (card in cards)
                        numOfPunchCardCredits += card.credits

                //Log.v("Cloud Queries userPunchCards", userPunchCards.toString() )
            } else {
                //Log.v("Cloud Queries userPunchCards Tag", e.toString() )
            }
        })
    }

    public fun userAdministrator(view: View){

        ParseCloud.callFunctionInBackground("userAdministrator", HashMap<String, Unit>(), FunctionCallback<kotlin.Boolean> { role, e ->
            if (e == null) {
                if (role != null) userAdministrator = role
                if (userAdministrator == true){
                    MainActivity.setupAdmin(view)
                    //CloudQueries.nextClass()
                    //CloudQueries.registeredNextClass()
                    //CloudQueries.allUsers()
                    //CloudQueries.allUserAttendance()
                    //CloudQueries.allUserCards()
                }
                //Log.v("Cloud Queries userAdministrator", userAdministrator.toString() )
            } else {
                //Log.v("Cloud Queries userAdministrator Tag", e.toString() )
            }
        })
    }

//    public fun nextClass(){
//
//        ParseCloud.callFunctionInBackground("nextClass", HashMap<String, Unit>(), FunctionCallback<com.fighttactix.model.Meeting> { meetings, e ->
//            if (e == null) {
//                nextClass = meetings
//                //Log.v("Cloud Queries nextClass", nextClass.toString() )
//            } else {
//                //Log.v("Cloud Queries nextClass Tag", e.toString() )
//            }
//        })
//    }

//    public fun checkinClass(){
//
//        ParseCloud.callFunctionInBackground("checkinClass", HashMap<String, Unit>(), FunctionCallback<com.fighttactix.model.Meeting> { meetings, e ->
//            if (e == null) {
//                checkinClass = meetings
//                //Log.v("Cloud Queries checkinClass", checkinClass.toString() )
//            } else {
//                //Log.v("Cloud Queries checkinClass Tag", e.toString() )
//            }
//        })
//    }



//    public fun registeredNextClass(){
//
//        ParseCloud.callFunctionInBackground("registeredNextClass", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Attendance>> { users, e ->
//            if (e == null) {
//                registeredNextClass = users
//                //Log.v("Cloud Queries registeredNextClass", registeredNextClass.toString() )
//            } else {
//                //Log.v("Cloud Queries registeredNextClass Tag", e.toString() )
//            }
//        })
//    }

    public fun currentSchedule(){

        ParseCloud.callFunctionInBackground("currentSchedule", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Meeting>> { meetings, e ->
            if (e == null) {
                currentSchedule = meetings


                //Log.v("Cloud Queries currentSchedule", currentSchedule.toString() )
            } else {
                //Log.v("Cloud Queries currentSchedule Tag", e.toString() )
            }
        })
    }


    public fun recentSchedule(){

        ParseCloud.callFunctionInBackground("recentSchedule", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Meeting>> { meetings, e ->
            if (e == null) {
                recentSchedule = meetings

                currentEnrolled?.clear()
                if (meetings != null)
                    for (meeting in meetings)
                        currentEnrolled(meeting.objectId)


                //Log.v("Cloud Queries currentSchedule", currentSchedule.toString() )
            } else {
                //Log.v("Cloud Queries recentSchedule Tag", e.toString() )
            }
        })
    }

    public fun currentEnrolled(meetingId:String) {

        var hmap = HashMap<String, String>()
        hmap.put("objectId", meetingId)

//        ParseCloud.callFunctionInBackground("currentEnrolled", hmap, FunctionCallback<kotlin.Int> { num, e ->
//            if (e == null) {
//                currentEnrolled?.add(CurrentAttendance(meetingId, num))
//                //Log.v("Cloud Queries currentEnrolled", currentEnrolled.toString() )
//            } else {
//                //Log.v("Cloud Queries currentEnrolled Tag", e.toString() )
//            }
//        })


        ParseCloud.callFunctionInBackground("currentEnrolledNames", hmap, FunctionCallback<java.util.ArrayList<com.fighttactix.model.Attendance>> { attendance, e ->
            if (e == null) {
                if (attendance != null) {
                    currentEnrolled?.add(CurrentAttendance(meetingId, attendance))
                }
            } else {
                Log.v("Cloud Queries currentEnrolled Tag", e.toString() )
            }
        })

    }


    public fun maxClassSize() {
        ParseCloud.callFunctionInBackground("maxClassSize", HashMap<String, Unit>(), FunctionCallback<kotlin.Int> { max, e ->
            if (e == null) {
                maxClassSize = max
                //Log.v("Cloud Queries maxClassSize", maxClassSize.toString() )
            } else {
                //Log.v("Cloud Queries maxClassSize Tag", e.toString() )
            }
        })

    }

    public fun userClassHistory(){


        ParseCloud.callFunctionInBackground("userClassHistory", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Attendance>> { attendance, e ->
            if (e == null) {
                userClassHistory = attendance
                userAttendedHistory.clear()
                numOfClassesUserAttended = 0
                if (attendance != null) {
                    for (attend in attendance) {
                      if (attend.checkedin == true) {
                        userAttendedHistory.add(attend)
                          numOfClassesUserAttended += 1
                      }
                    }
                }

                //Log.v("Cloud Queries userClassHistory", userClassHistory.toString() )
            } else {
                //Log.v("Cloud Queries currentSchedule Tag", e.toString() )
            }
        })
    }


//    public fun allUserCards(){
//
//        ParseCloud.callFunctionInBackground("allUserCards", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Cards>> { cardInfo, e ->
//            if (e == null) {
//                allUserCards = cardInfo
//                //Log.v("Cloud Queries allUserCards", cardInfo.toString() )
//            } else {
//                //Log.v("Cloud Queries allUserCards Tag", e.toString() )
//            }
//        })
//    }
//
//
//    public fun allUserAttendance(){
//
//        ParseCloud.callFunctionInBackground("allUserAttendance", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Attendance>> { attendanceInfo, e ->
//            if (e == null) {
//                allUserAttendance = attendanceInfo
//                //Log.v("Cloud Queries allUserAttendance", attendanceInfo.toString() )
//            } else {
//                //Log.v("Cloud Queries allUserAttendance Tag", e.toString() )
//            }
//        })
//    }

    public fun allUsers(){

        userCounts.clear()
        ParseCloud.callFunctionInBackground("allUsers", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.parse.ParseUser>> { users, e ->
            if (e == null) {
                allUsers = users
                for (user in users){
                    getUserNumbers(user)
                }
                //Log.v("Cloud Queries allUsers", allUsers.toString() )
            } else {
                //Log.v("Cloud Queries allUsers Tag", e.toString() )
            }
        })
    }


    public fun getUserNumbers(user: ParseUser){



        var hmap = HashMap<String, String>()
        hmap.put("userId", user.objectId)

        //var name:String = user.get("name") as String

        //userAttendanceCount?.clear()
        //userCardSum?.clear()

//        ParseCloud.callFunctionInBackground("countUserAttendance", hmap, FunctionCallback<kotlin.Int> { count, e ->
//            if (e == null) {
//                userAttendanceCount?.add(UserAttendanceCount(name, count))
//            } else {
//                Log.v("Cloud Queries countUserAttendance Tag", e.toString() )
//            }
//        })
//
//        ParseCloud.callFunctionInBackground("getUserCardSum", hmap, FunctionCallback<kotlin.Int> { sum, e ->
//            if (e == null) {
//                userCardSum?.add(UserCardSum(name, sum))
//            } else {
//                Log.v("Cloud Queries getUserCardSum Tag", e.toString() )
//            }
//        })
        //var a = java.util.ArrayList<E>

        ParseCloud.callFunctionInBackground("getUserCounts", hmap, FunctionCallback<java.util.ArrayList<Any>> { counts, e ->
            if (e == null) {
                //var name = counts[0] as String
                //var attend = counts[1] as Int
                //var sum = counts[2] as Int
                userCounts.add(UserCounts(counts[0] as String, counts[1] as Int, counts[2] as Int))
                //Log.v("Cloud Queries getUserCardSum", counts.toString()  )
            } else {
                Log.v("Cloud Queries getUserCardSum Tag", e.toString() )
            }
        })
    }



    public fun findBalance():ArrayList<AdminCard>{

        var adminCardList: ArrayList<AdminCard> = ArrayList<AdminCard>()

            //adminCardList.clear()
//            for (user in userAttendanceCount!!){
//                //var adminCard:AdminCard = AdminCard()
//
//                //var sum = 0
//
//                //adminCard.username = user.name
//
//                if (userCardSum != null)
//                    for (userSum in userCardSum!!)
//                        if (userSum.name == user.name){
//                            adminCardList.add(AdminCard(user.name, userSum.cardSum - user.attendanceCount))
//                            break
//                        }
//
//            }

            for (user in userCounts){
               adminCardList.add(AdminCard(user.name, user.cardSum - user.attendanceCount))

            }
        Collections.sort(adminCardList)
        return adminCardList
    }

    public fun locations(){

        ParseCloud.callFunctionInBackground("locations", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Location>> { locationInfo, e ->
            if (e == null) {
                locations = locationInfo
                //Log.v("Cloud Queries allUserCards", locations.toString() )
            } else {
                //Log.v("Cloud Queries allUserCards Tag", e.toString() )
            }
        })
    }


    public fun notifications(){

        ParseCloud.callFunctionInBackground("notifications", HashMap<String, Unit>(), FunctionCallback<java.util.ArrayList<com.fighttactix.model.Notifications>> { notificationInfo, e ->
            if (e == null) {
                notifications = notificationInfo
                //Log.v("Cloud Queries notifications", locations.toString() )
            } else {
                //Log.v("Cloud Queries notifications Tag", e.toString() )
            }
        })
    }


}