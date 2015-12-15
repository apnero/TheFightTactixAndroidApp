package com.fighttactix.model

import com.fighttactix.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

class ConnectionsAdapter(context: Context, cards:ArrayList<Location>):
        ArrayAdapter<Location>(context, 0, cards) {



    override fun getView(position:Int, convertView: View?, parent: ViewGroup):View {

        // Get the data item for this position
        val location = getItem(position)
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.connections_item, parent, false)
        }

        val nameTextView: TextView = view!!.findViewById(R.id.first_text) as TextView
        val addressTextView:TextView = view.findViewById(R.id.second_text) as TextView
        val linkTextView:TextView = view.findViewById(R.id.third_text) as TextView

        nameTextView.text = location.name
        addressTextView.text = location.address
        linkTextView.tag = location.link

        return view
    }
}