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
import android.widget.*
import butterknife.bindView
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
import java.util.*

class ClassHistoryAdapter(context: Context, attendees: ArrayList<Attendance>):
        ArrayAdapter<Attendance>(context, 0, attendees) {

    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        var checkedin:Boolean = false
        // Get the data item for this position
        val attendance = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false)
        }

        val dateTextView: TextView = view!!.findViewById(R.id.first_text) as TextView
        val locationTextView: TextView = view.findViewById(R.id.second_text) as TextView
        val checkedInTextView:TextView = view.findViewById(R.id.third_text) as TextView

        locationTextView.text = attendance.location
        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        dateTextView.text = sdf.format(attendance.date)

        val cal:Calendar = Calendar.getInstance(); // creates calendar
        cal.time = Date(); // sets calendar time/date
        cal.add(Calendar.HOUR, -2);

        if (attendance.date.before(cal.time)){
            checkedInTextView.text = "Complete"
            checkedInTextView.setTextColor(Color.LTGRAY)
        }
        else if (attendance.checkedin == true){
            checkedInTextView.text = "CHECKED IN"
            checkedInTextView.setTextColor(Color.BLUE)
        }
        else {
            checkedInTextView.text = "Registered"
            checkedInTextView.setTextColor(Color.DKGRAY)
        }


        return view
    }
}