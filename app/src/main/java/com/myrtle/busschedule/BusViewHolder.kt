package com.myrtle.busschedule

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val time: TextView

    init {
        time = itemView.findViewById(R.id.time)
    }

    fun bind(bus: Bus) {
        time.text = bus.time
    }
}

