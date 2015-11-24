package com.fighttactix.cloud

import android.content.Intent
import android.util.Log
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
    var nextClass: Meeting? = Meeting()
    var checkinClass: Meeting? = Meeting()
    var registeredNextClass: ArrayList<Attendance>? = ArrayList<Attendance>()
    var currentSchedule: ArrayList<Meeting>? = ArrayList<Meeting>()
    var userClassHistory: ArrayList<Attendance>? = ArrayList<Attendance>()
    var userPunchCards: ArrayList<Cards>? = ArrayList<Cards>()
    var allUserCards: ArrayList<Cards>? = ArrayList<Cards>()
    var allUserAttendance: ArrayList<Attendance>? = ArrayList<Attendance>()
    var allUsers: ArrayList<ParseUser>? = ArrayList<ParseUser>()
    var userAdministrator: Boolean = false
    var locations: ArrayList<Location>? = ArrayList<Location>()
    var currentEnrolled: ArrayList<CurrentAttendance>? = ArrayList<CurrentAttendance>()


    public fun userPunchCards(){

        ParseCloud.callFunctionInBackground("userPunchCards", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Cards>> {
            override fun done(cards: ArrayList<Cards>?, e: ParseException?) {
                if (e == null)
                {
                    numOfPunchCardCredits = 0
                    userPunchCards = cards
                    if (cards != null)
                        for (card in cards)
                            numOfPunchCardCredits += card.credits

                    Log.v("Cloud Queries userPunchCards", userPunchCards.toString() )
                }
                else
                {
                    Log.v("Cloud Queries userPunchCards Tag", e.toString() )
                }
            }
        })
    }

    public fun userAdministrator(login: LoginActivity){

        ParseCloud.callFunctionInBackground("userAdministrator", HashMap<String, Unit>(), object: FunctionCallback<Boolean> {
            override fun done(role:Boolean?, e: ParseException?) {
                if (e == null)
                {
                    if (role != null) userAdministrator = role
                    val intent = Intent(login, MainActivity::class.java)
                    login.startActivity(intent)
                    if (userAdministrator == true){

                        CloudQueries.nextClass()
                        CloudQueries.registeredNextClass()
                        CloudQueries.allUsers()
                        CloudQueries.allUserAttendance()
                        CloudQueries.allUserCards()
                    }
                    Log.v("Cloud Queries userAdministrator", userAdministrator.toString() )
                }
                else
                {
                    Log.v("Cloud Queries userAdministrator Tag", e.toString() )
                }
            }
        })
    }

    public fun nextClass(){

        ParseCloud.callFunctionInBackground("nextClass", HashMap<String, Unit>(), object: FunctionCallback<Meeting> {
            override fun done(meetings: Meeting?, e: ParseException?) {
                if (e == null)
                {
                    nextClass = meetings
                    Log.v("Cloud Queries nextClass", nextClass.toString() )
                }
                else
                {
                    Log.v("Cloud Queries nextClass Tag", e.toString() )
                }
            }
        })
    }

    public fun checkinClass(){

        ParseCloud.callFunctionInBackground("checkinClass", HashMap<String, Unit>(), object: FunctionCallback<Meeting> {
            override fun done(meetings: Meeting?, e: ParseException?) {
                if (e == null)
                {
                    checkinClass = meetings
                    Log.v("Cloud Queries checkinClass", checkinClass.toString() )
                }
                else
                {
                    Log.v("Cloud Queries checkinClass Tag", e.toString() )
                }
            }
        })
    }



    public fun registeredNextClass(){

        ParseCloud.callFunctionInBackground("registeredNextClass", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Attendance>> {
            override fun done(users: ArrayList<Attendance>?, e: ParseException?) {
                if (e == null)
                {
                    registeredNextClass = users
                    Log.v("Cloud Queries registeredNextClass", registeredNextClass.toString() )
                }
                else
                {
                    Log.v("Cloud Queries registeredNextClass Tag", e.toString() )
                }
            }
        })
    }

    public fun currentSchedule(){

        ParseCloud.callFunctionInBackground("currentSchedule", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Meeting>> {
            override fun done(meetings: ArrayList<Meeting>?, e: ParseException?) {
                if (e == null)
                {
                    currentSchedule = meetings

                    if (meetings != null)
                        for (meeting in meetings)
                            currentEnrolled(meeting.objectId)
                    Log.v("Cloud Queries currentSchedule", currentSchedule.toString() )
                }
                else
                {
                    Log.v("Cloud Queries currentSchedule Tag", e.toString() )
                }
            }
        })
    }

    public fun maxClassSize() {
        ParseCloud.callFunctionInBackground("maxClassSize", HashMap<String, Unit>(), object: FunctionCallback<Int> {
            override fun done(max: Int?, e: ParseException?) {
                if (e == null)
                {
                    maxClassSize = max
                    Log.v("Cloud Queries maxClassSize", maxClassSize.toString() )
                }
                else
                {
                    Log.v("Cloud Queries maxClassSize Tag", e.toString() )
                }
            }
        })

    }


    public fun currentEnrolled(meetingId:String) {

        var hmap = HashMap<String, String>()
        hmap.put("objectId", meetingId)

        ParseCloud.callFunctionInBackground("currentEnrolled", hmap, object: FunctionCallback<Int> {
            override fun done(num: Int?, e: ParseException?) {
                if (e == null)
                {
                    currentEnrolled?.add(CurrentAttendance(meetingId, num))
                    Log.v("Cloud Queries currentEnrolled", currentEnrolled.toString() )
                }
                else
                {
                    Log.v("Cloud Queries currentEnrolled Tag", e.toString() )
                }
            }
        })

    }

    public fun userClassHistory(){


        ParseCloud.callFunctionInBackground("userClassHistory", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Attendance>> {
            override fun done(attendance: ArrayList<Attendance>?, e: ParseException?) {
                if (e == null)
                {
                    userClassHistory = attendance
                    if (attendance != null)
                        numOfClassesUserAttended = attendance.size
                    Log.v("Cloud Queries userClassHistory", userClassHistory.toString() )
                }
                else
                {
                    Log.v("Cloud Queries currentSchedule Tag", e.toString() )
                }
            }
        })
    }


    public fun allUserCards(){

        ParseCloud.callFunctionInBackground("allUserCards", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Cards>> {
            override fun done(cardInfo: ArrayList<Cards>?, e: ParseException?) {
                if (e == null)
                {
                    allUserCards = cardInfo
                    Log.v("Cloud Queries allUserCards", cardInfo.toString() )
                }
                else
                {
                    Log.v("Cloud Queries allUserCards Tag", e.toString() )
                }
            }
        })
    }


    public fun allUserAttendance(){

        ParseCloud.callFunctionInBackground("allUserAttendance", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Attendance>> {
            override fun done(attendanceInfo: ArrayList<Attendance>?, e: ParseException?) {
                if (e == null)
                {
                    allUserAttendance = attendanceInfo
                    Log.v("Cloud Queries allUserAttendance", attendanceInfo.toString() )
                }
                else
                {
                    Log.v("Cloud Queries allUserAttendance Tag", e.toString() )
                }
            }
        })
    }

    public fun allUsers(){

        ParseCloud.callFunctionInBackground("allUsers", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<ParseUser>> {
            override fun done(users: ArrayList<ParseUser>?, e: ParseException?) {
                if (e == null)
                {
                    allUsers = users
                    Log.v("Cloud Queries allUsers", allUsers.toString() )
                }
                else
                {
                    Log.v("Cloud Queries allUsers Tag", e.toString() )
                }
            }
        })
    }

    public fun findBalance():ArrayList<AdminCard>{

        var adminCardList: ArrayList<AdminCard> = ArrayList<AdminCard>()

        if (allUsers != null)
            for (user in allUsers!!){
                var adminCard:AdminCard = AdminCard()
                var sum = 0
                adminCard.username = user.username

                if (allUserCards != null)
                    for (card in allUserCards!!){
                        if (user.username == card.username) sum += card.credits
                    }
                if (allUserAttendance != null)
                    for (attendance in allUserAttendance!!){
                        if (user.username == attendance.username) sum -= 1
                    }

                adminCard.credits = sum
                adminCardList.add(adminCard)
            }
        return adminCardList
    }

    public fun locations(){

        ParseCloud.callFunctionInBackground("locations", HashMap<String, Unit>(), object: FunctionCallback<ArrayList<Location>> {
            override fun done(locationInfo: ArrayList<Location>?, e: ParseException?) {
                if (e == null)
                {
                    locations = locationInfo
                    Log.v("Cloud Queries allUserCards", locations.toString() )
                }
                else
                {
                    Log.v("Cloud Queries allUserCards Tag", e.toString() )
                }
            }
        })
    }

}