package com.fighttactix.model

class AdminCard(var username:String = "", var credits:Int = 0):Comparable<Any>{

    override fun compareTo(other: Any): Int {

        val card = other as AdminCard

        if (this.credits == card.credits)
            return 0
        else
            return if (this.credits > card.credits) -1 else 1

    }

//    override fun compareTo(card:AdminCard):Int {
//        if (this.username == card.username)
//            return 0
//        else
//            return if (this.username > card.username) 1 else -1
//
//        throw UnsupportedOperationException()
//    }


}