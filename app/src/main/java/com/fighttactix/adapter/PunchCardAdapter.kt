package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class PunchCardAdapter(context: Context, cards:ArrayList<Cards>):
        ArrayAdapter<Cards>(context, 0, cards) {



    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val card = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_checkin_item, parent, false)
        }

        val dateTextView: TextView = view!!.findViewById(R.id.admin_name_text) as TextView
        val creditsTextView:TextView = view.findViewById(R.id.admin_checkin_text) as TextView

        val sdf:SimpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        dateTextView.text = sdf.format(card.date)

        creditsTextView.text = card.credits.toString()


        return view
    }
}