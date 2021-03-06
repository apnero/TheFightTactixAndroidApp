package com.fighttactix.adapter

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fighttactix.cloud.CloudQueries
import com.fighttactix.model.Attendance
import com.fighttactix.model.Meeting
import java.text.SimpleDateFormat
import java.util.*

class MeetingAdapter(context: Context, meetings:ArrayList<Meeting>, userClassHistory:ArrayList<Attendance>):
                                            ArrayAdapter<Meeting>(context, 0, meetings) {


    val classHistory:ArrayList<Attendance> = userClassHistory

    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        var registered:Boolean = false
        var checkedin:Boolean = false
        var soldOut:Boolean = false
        // Get the data item for this position
        val meeting = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false)
        }

        val dateTextView: TextView = view!!.findViewById(R.id.first_text) as TextView
        val timeTextView: TextView = view.findViewById(R.id.first2_text) as TextView
        val locationTextView: TextView = view.findViewById(R.id.second_text) as TextView
        val checkedInTextView:TextView = view.findViewById(R.id.third_text) as TextView

        locationTextView.text = meeting.location

        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d")
        val hourSdf:SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
        val twoHourClassCal:Calendar = Calendar.getInstance()
        twoHourClassCal.time = meeting.date
        twoHourClassCal.add(Calendar.HOUR, 2)
        dateTextView.text = sdf.format(meeting.date)
        timeTextView.text = hourSdf.format(meeting.date) + "-" + hourSdf.format(twoHourClassCal.time)

        val cal:Calendar = Calendar.getInstance() // creates calendar
        cal.time = Date()
        // sets calendar time/date
        cal.add(Calendar.HOUR, -2)

        for(item in classHistory) {
            if(meeting.date == item.date){
                registered = true
                if (item.checkedin == true){
                    checkedin = true
                }
                break
            }
        }


        if (CloudQueries.currentEnrolled != null && CloudQueries.maxClassSize != null)
            for(session in CloudQueries.currentEnrolled!!)
                if (meeting.objectId == session.meetingId)
                    if(session.attendance != null && session.attendance!!.size >= CloudQueries.maxClassSize!!)
                        soldOut = true



        if (meeting.date.before(cal.time)){
            checkedInTextView.text = "Complete"
            checkedInTextView.setTextColor(Color.LTGRAY)
        }
        else if (checkedin){
            checkedInTextView.text = "CHECKED IN"
            checkedInTextView.setTextColor(Color.BLUE)
        }
        else if (registered){
            cal.add(Calendar.HOUR, 3)//1 hour before
            if(cal.time.after(meeting.date)){
                checkedInTextView.text = "Registered (< 1 hour)"
                checkedInTextView.setTextColor(Color.BLACK)
            }
            else {
                checkedInTextView.text = "Registered"
                checkedInTextView.setTextColor(Color.DKGRAY)
            }
        }
        else if (soldOut){
            checkedInTextView.text = "MAX Registered"
            checkedInTextView.setTextColor(Color.BLACK)
        }
        else if (meeting.open == true){
            checkedInTextView.text = "Register"
            checkedInTextView.setTextColor(Color.GRAY)
        }
        else {
            checkedInTextView.text = "Registration Closed"
            checkedInTextView.setTextColor(Color.BLACK)
        }


        return view
    }
}