package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.provider.ContactsContract
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.bindView
import com.fighttactix.cloud.CloudQueries
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
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
        val timeTextView: TextView = view!!.findViewById(R.id.first2_text) as TextView
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

        if (CloudQueries.currentEnrolled != null)
            for(session in CloudQueries.currentEnrolled!!)
                if (meeting.objectId == session.meetingId){
                    checkedInTextView.text = session.number.toString()
                    if(session.number!! >= CloudQueries.maxClassSize!!)
                        checkedInTextView.setTextColor(Color.RED)
                    else checkedInTextView.setTextColor(Color.BLUE)
                }



        return view
    }
}