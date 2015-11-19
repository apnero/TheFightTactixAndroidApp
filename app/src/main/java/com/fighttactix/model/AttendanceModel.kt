package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Attendance")
class Attendance():ParseObject() {

    var username: String
        get() {
            return getString("username")
        }
        set(userName) {
            put("username", username)
        }

    var location:String?
        get() {
            return getString("location")
        }
        set(location) {
            put("location", location)
        }


    //    var meeting: ParseObject
//        get() {
//            return getParseObject("meeting")
//        }
//        set(name) {
//            put("meeting", user)
//        }

    var checkedin:Boolean?
        get() {
            return getBoolean("checkedin")
        }
        set(checkedin) {
            put("checkedin", checkedin)
        }

    var date:Date
        get() {
            return getDate("date")
        }
        set(date) {
            put("date", date)
        }
}
