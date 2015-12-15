package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

class AdminCardAdapter(context: Context, cards:ArrayList<AdminCard>?):
        ArrayAdapter<AdminCard>(context, 0, cards) {



    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val adminCard = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_card_item, parent, false)
        }

        val userNameTextView: TextView = view!!.findViewById(R.id.first_text) as TextView
        val creditsTextView:TextView = view.findViewById(R.id.second_text) as TextView
        val addCreditsTextView:TextView = view.findViewById(R.id.third_text) as TextView

        userNameTextView.text = adminCard.username
        creditsTextView.text = adminCard.credits.toString()
        addCreditsTextView.text = "Add Credits"
        addCreditsTextView.setTextColor(Color.BLUE)

        return view
    }
}