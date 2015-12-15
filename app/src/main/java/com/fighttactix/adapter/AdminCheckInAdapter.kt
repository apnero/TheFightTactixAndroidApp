package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class AdminCheckInAdapter(context: Context, attendees: ArrayList<Attendance>?):
        ArrayAdapter<Attendance>(context, 0, attendees) {

    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val attendee = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.admin_checkin_item, parent, false)
        }

        val textView: TextView = view!!.findViewById(R.id.admin_name_text) as TextView
        val checkedInTextView:TextView = view.findViewById(R.id.admin_checkin_text) as TextView

        textView.text = attendee.username
        if(attendee.checkedin == true) {
            checkedInTextView.text = "CHECKED IN"
            checkedInTextView.setTextColor(Color.BLUE)
        }
        else {
            checkedInTextView.text = "Not Checked In"
            checkedInTextView.setTextColor(Color.DKGRAY)
        }

        return view
    }
}