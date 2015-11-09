package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Attendance")
class Attendance():ParseObject() {

    var user: ParseObject
        get() {
            return getParseObject("user")
        }
        set(name) {
            put("user", user)
        }

    var meeting: ParseObject
        get() {
            return getParseObject("meeting")
        }
        set(name) {
            put("meeting", user)
        }

}
