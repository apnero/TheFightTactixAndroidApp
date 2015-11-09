package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Meeting")
class Meeting():ParseObject() {

//    var objectId: String
//        get() {
//            return getString("objectId")
//        }
//        set(objectId) {
//            put("objectId", objectId)
//        }

    var name: String?
        get() {
            return getString("name")
        }
        set(name) {
            put("name", name)
        }

    var instructor: String?
        get() {
            return getString("instructor")
        }
        set(instructor) {
            put("instructor", instructor)
        }

    var location:String?
        get() {
            return getString("location")
        }
        set(location) {
            put("location", location)
        }

    var date:Date
        get() {
            return getDate("date")
        }
        set(date) {
            put("date", date)
        }

    var active:Boolean?
        get() {
            return getBoolean("active")
        }
        set(active) {
            put("active", active)
        }

    var open:Boolean?
        get() {
            return getBoolean("open")
        }
        set(name) {
            put("open", open)
        }


}
