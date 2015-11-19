package com.fighttactix.model

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*

@ParseClassName("Location")
class Location():ParseObject() {

    var name: String?
        get() {
            return getString("name")
        }
        set(name) {
            put("name", name)
        }

    var address: String?
        get() {
            return getString("address")
        }
        set(address) {
            put("address", address)
        }

    var link: String?
        get() {
            return getString("link")
        }
        set(link) {
            put("link", link)
        }
}
