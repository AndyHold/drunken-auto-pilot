package com.example.drunkenautopilot.audioRecordingsRecyclerView

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.R

class AudioRecordingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val btnPlayButton: ImageButton = view.findViewById(R.id.btn_play_audio)
    val tvDateTaken: TextView = view.findViewById(R.id.tv_date_taken)

    var isActive: Boolean = false
        set(value) {
            field = value
            itemView.setBackgroundColor(if (field) Color.LTGRAY else Color.TRANSPARENT)
        }
}