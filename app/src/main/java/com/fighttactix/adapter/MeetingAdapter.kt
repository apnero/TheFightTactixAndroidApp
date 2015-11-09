package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.provider.ContactsContract
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import butterknife.bindView
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import java.util.*

class MeetingAdapter(context: Context, meetings:ArrayList<Meeting>, meetingsHistory:ArrayList<Meeting>):
                                            ArrayAdapter<Meeting>(context, 0, meetings) {


    val meetHistory:ArrayList<Meeting> = meetingsHistory

    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val meeting = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.cardview_schedule, parent, false)
        }

        val meetingName: TextView = view!!.findViewById(R.id.name) as TextView
        val meetingLocation: TextView = view.findViewById(R.id.location) as TextView
        val meetingDate: TextView = view.findViewById(R.id.date) as TextView
        var meetingCheckinButton: FancyButton = view.findViewById(R.id.checkin_button) as FancyButton

        meetingCheckinButton.tag = position
        meetingName.text = meeting.name
        meetingLocation.text = meeting.location
        meetingDate.text = meeting.date.toString()

        if (meeting.date.before(Calendar.getInstance().getTime())){
            meetingCheckinButton.setText("Class Over")
            meetingCheckinButton.setBorderColor(Color.GRAY)
            meetingCheckinButton.setTextColor(Color.GRAY)
            meetingCheckinButton.setEnabled(false)
        }
        else if (meetHistory.contains(meeting)) {
            meetingCheckinButton.setText("Registered")
            meetingCheckinButton.setBorderColor(Color.BLUE)
            meetingCheckinButton.setTextColor(Color.BLUE)
            meetingCheckinButton.setEnabled(false)
        }
        else if (meeting?.open == false){
                meetingCheckinButton.setText("Not Open For Registration")
                meetingCheckinButton.setBorderColor(Color.RED)
                meetingCheckinButton.setTextColor(Color.RED)
                meetingCheckinButton.setEnabled(false)
        }
        else {
            meetingCheckinButton.setText("Register")
            meetingCheckinButton.setBorderColor(Color.WHITE)
            meetingCheckinButton.setTextColor(Color.WHITE)
            meetingCheckinButton.setEnabled(true)
        }

        return view
    }
}