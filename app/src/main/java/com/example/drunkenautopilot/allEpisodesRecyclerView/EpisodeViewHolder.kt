package com.example.drunkenautopilot.allEpisodesRecyclerView

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.R

class EpisodeViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val tvDate: TextView = view.findViewById(R.id.tv_date)
    val tvSteps: TextView = view.findViewById(R.id.tv_steps)

    var isActive: Boolean = false
    set(value) {
        field = value
        itemView.setBackgroundColor(if (field) Color.LTGRAY else Color.TRANSPARENT)
    }
}