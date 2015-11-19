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

class AdminCheckInAdapter(context: Context, attendees: ArrayList<Attendance>):
        ArrayAdapter<Attendance>(context, 0, attendees) {

    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val attendee = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_checkin_item, parent, false)
        }

        val textView: TextView = view!!.findViewById(R.id.admin_name_text) as TextView
        val checkedInTextView:TextView = view!!.findViewById(R.id.admin_checkin_text) as TextView

        textView.setText(attendee.username)
        if(attendee.checkedin == true) {
            checkedInTextView.setText("CHECKED IN")
            checkedInTextView.setTextColor(Color.BLUE)
        }
        else {
            checkedInTextView.setText("Not Checked In")
            checkedInTextView.setTextColor(Color.DKGRAY)
        }

        return view
    }
}