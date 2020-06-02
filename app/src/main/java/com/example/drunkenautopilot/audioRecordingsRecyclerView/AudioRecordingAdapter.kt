package com.example.drunkenautopilot.audioRecordingsRecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.R
import com.example.drunkenautopilot.models.AudioRecording
import java.text.SimpleDateFormat
import java.util.*

class AudioRecordingAdapter(
    val context: Context,
    val audioRecordings: List<AudioRecording>,
    val clickListener: (AudioRecording) -> Unit
) : RecyclerView.Adapter<AudioRecordingViewHolder>() {

    private var selectedIndex = RecyclerView.NO_POSITION

    override fun getItemCount(): Int = audioRecordings.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AudioRecordingViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.audio_recording_item, parent, false)
        val holder = AudioRecordingViewHolder(view)
        holder.btnPlayButton.setOnClickListener{
            val oldSelectedIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(selectedIndex)
            notifyItemChanged(oldSelectedIndex)
            clickListener(audioRecordings[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: AudioRecordingViewHolder, i: Int) {
        val date = Date()
        date.time = audioRecordings[i].timeTaken.time
        holder.tvDateTaken.text = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(date)
        holder.isActive = selectedIndex == i
    }
}