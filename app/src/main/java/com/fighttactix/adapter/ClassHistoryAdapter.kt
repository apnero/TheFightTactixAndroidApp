package com.fighttactix.adapter

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fighttactix.model.Attendance
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
            view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false)
        }

        val dateTextView: TextView = view!!.findViewById(R.id.first_text) as TextView
        val timeTextView: TextView = view.findViewById(R.id.first2_text) as TextView
        val checkedInTextView:TextView = view.findViewById(R.id.third_text) as TextView


        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d")
        val hourSdf:SimpleDateFormat = SimpleDateFormat("hh:mm aaa")
        val twoHourClassCal:Calendar = Calendar.getInstance()
        twoHourClassCal.time = attendance.date
        twoHourClassCal.add(Calendar.HOUR, 2)
        dateTextView.text = sdf.format(attendance.date)
        timeTextView.text = hourSdf.format(attendance.date) + "-" + hourSdf.format(twoHourClassCal.time)

        val cal:Calendar = Calendar.getInstance(); // creates calendar
        cal.time = Date(); // sets calendar time/date
        cal.add(Calendar.HOUR, -2);

        if (attendance.date.before(cal.time)){
            checkedInTextView.text = "Complete"
            checkedInTextView.setBackgroundColor(Color.BLACK)//checkedInTextView.setTextColor(Color.LTGRAY)
        }
        else {
            checkedInTextView.text = "CHECKED IN"
            checkedInTextView.setBackgroundColor(Color.RED)
            //checkedInTextView.setTextColor(Color.BLUE)
        }
//        else {
//            checkedInTextView.text = "Registered"
//            checkedInTextView.setBackgroundColor(Color.BLUE)
//            //checkedInTextView.setTextColor(Color.DKGRAY)
//        }


        return view
    }
}