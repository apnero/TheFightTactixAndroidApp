package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Cards")
class Cards():ParseObject() {

    var user: ParseObject?
        get() {
            return getParseObject("user")
        }
        set(user) {
            put("user", user)
        }

    var credits: Int
        get() {
            return getInt("credits")
        }
        set(credits) {
            put("credits", credits)
        }

    var date:Date?
        get() {
            return getDate("date")
        }
        set(date) {
            put("date", date)
        }

    var username: String
        get() {
            return getString("username")
        }
        set(username) {
            put("username", username)
        }

}
