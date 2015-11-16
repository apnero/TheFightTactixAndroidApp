package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Attendance")
class Attendance():ParseObject() {

    var userName: String
        get() {
            return getString("userName")
        }
        set(userName) {
            put("userName", userName)
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

}
