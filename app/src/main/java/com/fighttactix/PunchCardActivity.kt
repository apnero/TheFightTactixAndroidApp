package com.fighttactix

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.support.annotation.NonNull
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import butterknife.bindView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.fighttactix.model.*
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.DateFormat

import java.util.*


public class PunchCardActivity: AppCompatActivity() {

    val punchCardView: NonScrollableListView by bindView(R.id.cardList)
    val toolbar: Toolbar by bindView(R.id.toolbar)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        initToolbar("Punch Card History")
        getUserPunchCards()
    }

    private fun initToolbar(title:String) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.title = title
        actionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    public fun setPunchCardViewAdapter(objects:ArrayList<Cards>) {
        var adapter: ArrayAdapter<Cards> =
                PunchCardAdapter(this, objects)
        punchCardView.adapter = adapter
    }


    fun getUserPunchCards() {
        val query: ParseQuery<Cards> = ParseQuery.getQuery("Cards")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.findInBackground(object: FindCallback<Cards> {
            override fun done(objects:List<Cards>, e: ParseException?) {
                if (e == null)
                {
                    Log.v("getUserPunchCards No exception", objects.toString())
                    setPunchCardViewAdapter(ArrayList(objects))
                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }


}
