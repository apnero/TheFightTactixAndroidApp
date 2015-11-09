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
import com.fighttactix.model.Attendance
import com.fighttactix.model.MeetingAdapter
import com.fighttactix.model.Meeting
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.DateFormat

import java.util.*


public class MeetingActivity: AppCompatActivity() {

    val meetingView: NonScrollableListView by bindView(R.id.cardList)
    val toolbar: Toolbar by bindView(R.id.toolbar)
    var meetingObjects = ArrayList<Meeting>()
    var meetingHistoryObjects = ArrayList<Meeting>()
    var title:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        title = intent.getStringExtra("title")
        initToolbar(title)
        getActiveMeetings()
    }

    private fun initToolbar(title:String) {

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.title = title
        actionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    public fun setMeetingViewAdapter(objects:ArrayList<Meeting>) {
        var adapter: ArrayAdapter<Meeting> =
                    MeetingAdapter(this, objects, meetingHistoryObjects)
        meetingView.adapter = adapter
    }


    fun showMultiChoice(view: View) {
        val meetingCheckinButton: FancyButton = view.findViewById(R.id.checkin_button) as FancyButton
            MaterialDialog.Builder(this).title(R.string.multiChoice).items(R.array.socialNetworks).itemsCallbackMultiChoice(arrayOf<Int>(0, 1), object : MaterialDialog.ListCallbackMultiChoice {
                override fun onSelection(dialog: MaterialDialog, which: Array<Int>, text: Array<CharSequence>): Boolean {
                    val str = StringBuilder()
                    for (i in which.indices) {
                        if (i == 1) addToCalendar(meetingCheckinButton.tag as Int)
                    }

                    return true // allow selection
                }
            }).onNeutral(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(@NonNull dialog: MaterialDialog, @NonNull which: DialogAction) {
                    dialog.clearSelectedIndices()
                }
            }).onPositive(object: MaterialDialog.SingleButtonCallback {
                override fun onClick(@NonNull dialog: MaterialDialog, @NonNull which: DialogAction) {
                    meetingCheckinButton.setBorderColor(Color.BLUE)
                    meetingCheckinButton.setTextColor(Color.BLUE)
                    meetingCheckinButton.setEnabled(false)
                    meetingCheckinButton.setText("Registered")
                    registerForMeeting(meetingCheckinButton.tag as Int)
                    dialog.dismiss()

                }
            }).alwaysCallMultiChoiceCallback()
                .positiveText(R.string.register)
                .autoDismiss(false)
                .neutralText(R.string.clear)
                .show()

    }


    fun registerForMeeting(index:Int){
        var attend:ParseObject = ParseObject.create("Attendance")
        //var meeting:ParseObject = ParseObject("Meeting")
        attend.put("user", ParseUser.getCurrentUser())
        attend.put("meeting", ParseObject.createWithoutData("Meeting", meetingObjects[index].objectId))
        attend.saveInBackground()

    }

    fun addToCalendar(index: Int){

        val calIntent = Intent(Intent.ACTION_INSERT)
        calIntent.setType("vnd.android.cursor.item/event")
        calIntent.putExtra(CalendarContract.Events.TITLE, meetingObjects[index].name)
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, meetingObjects[index].location)
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                meetingObjects[index].date.time + 5*60*60*1000)
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                meetingObjects[index].date.time + 5*60*60*1000 + 90*60*1000)
        startActivity(calIntent)
    }

    fun getActiveMeetings() {

        val query: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
        query.whereEqualTo("active", true)
        query.whereGreaterThanOrEqualTo("date", Date())
        query.orderByAscending("date")
        query.findInBackground(object: FindCallback<Meeting> {
            override fun done(objects:List<Meeting>, e: ParseException?) {
                if (e == null)
                {
                    Log.v("getActiveMeetings no exception", objects.toString())
                    meetingObjects = ArrayList(objects)
                    getUserAttendance()

                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }

    fun getUserAttendance() {

        val query: ParseQuery<Attendance> = ParseQuery.getQuery("Attendance")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
//        query.orderByAscending("date")
        query.findInBackground(object: FindCallback<Attendance> {
            override fun done(objects:List<Attendance>, e: ParseException?) {
                if (e == null)
                {
                    Log.v("getUserAttendance No exception", objects.toString())
                    getUserMeetings(objects)
                }
                else
                {
                    Log.v("exception", e.toString())
                }

            }
        })

    }

    fun getUserMeetings(attendanceList: List<Attendance>) {

        var queries: ArrayList<ParseQuery<Meeting>> = ArrayList<ParseQuery<Meeting>>()
        for (attend in attendanceList) {
            var query: ParseQuery<Meeting> = ParseQuery.getQuery("Meeting")
            query.whereEqualTo("objectId", attend.meeting.getObjectId())
            query.whereEqualTo("active", true)
            queries.add(query)
        }

        if(!queries.isEmpty()) {
            val mainQuery: ParseQuery<Meeting> = ParseQuery.or(queries)
            mainQuery.orderByAscending("date")
            mainQuery.findInBackground(object : FindCallback<Meeting> {
                override fun done(objects: List<Meeting>, e: ParseException?) {
                    if (e == null) {
                        Log.v("getUserMeetings no exception", objects.toString())
                        meetingHistoryObjects = ArrayList(objects)

                    }
                    else {
                        Log.v("exception", e.toString())
                    }
                    when(title){
                        "Schedule" -> setMeetingViewAdapter(meetingObjects)
                        "Class History" -> setMeetingViewAdapter(meetingHistoryObjects)
                    }
                }
            })
        }
        else{
            when(title){
                "Schedule" -> setMeetingViewAdapter(meetingObjects)
                "Class History" -> setMeetingViewAdapter(meetingHistoryObjects)
            }
        }

    }


}
