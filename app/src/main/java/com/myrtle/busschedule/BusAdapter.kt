package com.myrtle.busschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BusAdapter(val data: List<Bus>) : RecyclerView.Adapter<BusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(R.layout.bus_item, parent, false)

        return BusViewHolder(layout)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}