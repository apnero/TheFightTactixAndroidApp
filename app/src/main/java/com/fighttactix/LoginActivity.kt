package com.fighttactix


import com.parse.ui.ParseLoginDispatchActivity

class LoginActivity:ParseLoginDispatchActivity() {

    override fun getTargetClass(): Class<*>? {
        return MainActivity::class.java
    }

}