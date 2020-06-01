package com.example.drunkenautopilot.allEpisodesRecyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.R
import com.example.drunkenautopilot.models.Episode
import java.time.format.DateTimeFormatter

class EpisodeAdapter (
    val context: Context,
    val episodes: List<Episode>,
    val clickListener: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeViewHolder>() {

    private var selectedIndex = RecyclerView.NO_POSITION
    @SuppressLint("NewApi")
    private var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun getItemCount(): Int = episodes.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EpisodeViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.episode_item, parent, false)
        val holder = EpisodeViewHolder(view)

        view.setOnClickListener {
            val oldSelectedIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(selectedIndex)
            notifyItemChanged(oldSelectedIndex)
            clickListener(episodes[holder.adapterPosition])
        }

        return holder
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: EpisodeViewHolder, i: Int) {
        holder.tvDate.text = formatter.format(episodes[i].date.toInstant())
        holder.tvSteps.text = episodes[i].steps.toString()
        holder.isActive = selectedIndex == i
    }
}