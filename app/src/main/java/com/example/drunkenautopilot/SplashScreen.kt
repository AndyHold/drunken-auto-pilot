package com.example.drunkenautopilot

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.preference.PreferenceManager

class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val animation = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate)
        findViewById<ImageView>(R.id.iv_loadingIcon).startAnimation(animation)

        val settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val lat = settings.getFloat(resources.getString(R.string.address_latitude_key), 900f)
        val long = settings.getFloat(resources.getString(R.string.address_longitude_key), 900f)
        val name = settings.getString(resources.getString(R.string.address_name_key), null)

        if (lat <= 90 && long <= 180 && name != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            Handler().postDelayed({
                startActivity(intent)
            }, 2000)
        } else {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            Handler().postDelayed({
                startActivity(intent)
            }, 2000)
        }
    }
}
