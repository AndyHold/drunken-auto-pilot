package com.example.drunkenautopilot

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class EpisodeMainScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.episode_main_screen)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.openOptionsMenu()
        val startAudioButton: ImageButton = findViewById(R.id.btn_start_audio)
        val startVideoButton: ImageButton = findViewById(R.id.btn_start_video)

        startAudioButton.setOnClickListener {
            // TODO: Start an Audio Recording
        }

        startVideoButton.setOnClickListener {
            // TODO: Start a Video Recording
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}