package com.example.drunkenautopilot

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.preference.PreferenceManager
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.viewModels.EpisodeViewModel

class SplashScreen : AppCompatActivity(), ViewModelStoreOwner {

    var activeEpisode: Episode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        println("Started")
        super.onStart()
    }

    override fun onResume() {

        val animation = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate)
        findViewById<ImageView>(R.id.iv_loadingIcon).startAnimation(animation)

        val settings: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val lat = settings.getFloat(resources.getString(R.string.address_latitude_key), 900f)
        val long = settings.getFloat(resources.getString(R.string.address_longitude_key), 900f)
        val name = settings.getString(resources.getString(R.string.address_name_key), null)

        val hasSettings = lat <= 90 && long <= 180 && name != null

        val episodeViewModel = ViewModelProvider(this).get(EpisodeViewModel::class.java)
        episodeViewModel.activeEpisode.observe(this, Observer { episode ->
            activeEpisode = episode
        })


        Handler().postDelayed({
            if (!hasSettings) {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
//            } else if (activeEpisode == null){
            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
//            } else {
//                val intent = Intent(applicationContext, EpisodeMainScreenActivity::class.java)
//                startActivity(intent)
            }
        }, 2000)
        super.onResume()
    }
}
