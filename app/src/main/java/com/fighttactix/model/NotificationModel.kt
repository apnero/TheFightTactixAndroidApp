package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Notifications")
class Notifications():ParseObject() {

    var text: String?
        get() {
            return getString("text")
        }
        set(name) {
            put("name", name)
        }


}
