package com.example.drunkenautopilot

import android.app.Activity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val animation = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate)
        findViewById<ImageView>(R.id.iv_loadingIcon).startAnimation(animation)
    }
}
