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
import com.fighttactix.model.Meeting
import java.text.SimpleDateFormat
import java.util.*

class AdminMeetingAdapter(context: Context, meetings:ArrayList<Meeting>):
        ArrayAdapter<Meeting>(context, 0, meetings) {


    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

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

        if (meeting.date.before(cal.time)){
            dateTextView.setTextColor(Color.LTGRAY)
            timeTextView.setTextColor(Color.LTGRAY)
        }
        else {
            dateTextView.setTextColor(Color.BLACK)
            timeTextView.setTextColor(Color.BLACK)
        }


        if (CloudQueries.currentEnrolled != null)
            for(session in CloudQueries.currentEnrolled!!)
                if (meeting.objectId == session.meetingId){
                    checkedInTextView.text = session.attendance?.size.toString()
                    if(session.attendance != null && CloudQueries.maxClassSize != null)
                        if(session.attendance!!.size >= CloudQueries.maxClassSize!!)
                            checkedInTextView.setTextColor(Color.RED)
                        else checkedInTextView.setTextColor(Color.BLUE)
                }



        return view
    }
}