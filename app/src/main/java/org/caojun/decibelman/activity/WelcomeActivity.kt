package org.caojun.decibelman.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*
import org.caojun.decibelman.R
import org.caojun.particle.ParticleView
import org.jetbrains.anko.startActivity

class WelcomeActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val mSharedPreferences = getSharedPreferences(BaseActivity.PREFER_NAME, Context.MODE_PRIVATE)
        val switchOn = mSharedPreferences.getBoolean(BaseActivity.Key_Switch_Welcome, true)
        if (!switchOn) {
            doGoNext()
            return
        }

        particleView.startAnim()
        particleView.setOnParticleAnimListener(object : ParticleView.ParticleAnimListener {
            override fun onAnimationEnd() {
                doGoNext()
            }
        })

        tvSkip.setOnClickListener {
            particleView.pauseAnim()
            mSharedPreferences.edit().putBoolean(BaseActivity.Key_Switch_Welcome, false).apply()
            doGoNext()
        }
    }

    private fun doGoNext() {
        startActivity<MainActivity>()
        finish()
    }
}