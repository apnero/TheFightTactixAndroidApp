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

class PunchCardAdapter(context: Context, cards:ArrayList<Cards>):
        ArrayAdapter<Cards>(context, 0, cards) {



    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val card = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.cardview_punchcard, parent, false)
        }

        val cardCredits: TextView = view!!.findViewById(R.id.credits) as TextView
        val cardDate: TextView = view.findViewById(R.id.date) as TextView

        cardDate.text = card.date.toString()
        cardCredits.text = card.credits.toString()

        return view
    }
}