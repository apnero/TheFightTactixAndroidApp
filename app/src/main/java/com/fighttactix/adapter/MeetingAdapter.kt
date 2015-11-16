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
import com.parse.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
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

        //val meetingName: TextView = view!!.findViewById(R.id.name) as TextView
        val meetingLocation: TextView = view!!.findViewById(R.id.location) as TextView
        val meetingDate: TextView = view.findViewById(R.id.date) as TextView
        var meetingCheckinButton: TextView = view.findViewById(R.id.checkin_button) as TextView
        val registerCardView: CardView = view.findViewById(R.id.register_view) as CardView
        var checkinRowColor: LinearLayout = view.findViewById(R.id.checkin_row_color) as LinearLayout
        meetingCheckinButton.tag = position
        //meetingName.text = meeting.name
        meetingLocation.text = meeting.location

        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        meetingDate.text = sdf.format(meeting.date)

        if (meeting.date.before(Calendar.getInstance().getTime())){
            meetingCheckinButton.setText("Class Over")
            checkinRowColor.setBackgroundColor(Color.MAGENTA)
            meetingCheckinButton.setTextColor(Color.BLACK)
            registerCardView.setEnabled(false)
        }
        else if (meetHistory.contains(meeting)) {
            meetingCheckinButton.setText("Registered")
            checkinRowColor.setBackgroundColor(Color.GRAY)
            meetingCheckinButton.setTextColor(Color.BLUE)
            registerCardView.setEnabled(false)
        }
        else if (meeting?.open == false){
            meetingCheckinButton.setText("Not Open For Registration")
            checkinRowColor.setBackgroundColor(Color.GREEN)
            meetingCheckinButton.setTextColor(Color.RED)
            registerCardView.setEnabled(false)
        }
        else {
            meetingCheckinButton.setText("Register")
            checkinRowColor.setBackgroundColor(Color.BLACK)
            meetingCheckinButton.setTextColor(Color.WHITE)
            registerCardView.setEnabled(true)
        }

        return view
    }
}