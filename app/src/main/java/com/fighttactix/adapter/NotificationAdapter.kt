package com.fighttactix.adapter

import com.fighttactix.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fighttactix.model.Notifications
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(context: Context, cards:ArrayList<Notifications>):
        ArrayAdapter<Notifications>(context, 0, cards) {



    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val notification = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.notifications_item, parent, false)
        }

        val textView: TextView = view!!.findViewById(R.id.msg_text) as TextView
        val dateTextView:TextView = view.findViewById(R.id.date_text) as TextView

        textView.text = notification.text

        val sdf: SimpleDateFormat = SimpleDateFormat("EEE, MMM d hh:mm aaa")
        dateTextView.text = sdf.format(notification.createdAt)

        return view
    }
}