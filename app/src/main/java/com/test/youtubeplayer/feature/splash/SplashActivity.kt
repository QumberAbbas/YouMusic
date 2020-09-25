package com.test.youtubeplayer.feature.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.youtubeplayer.R
import com.test.youtubeplayer.extensions.setOnTransitionCompleted
import com.test.youtubeplayer.feature.splash.router.SplashRouter
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setMotionLayoutTransitions()
    }

    private fun setMotionLayoutTransitions() {
        motionLayout.setOnTransitionCompleted {
            SplashRouter.routeToNextScreen(this)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    override fun onResume() {
        super.onResume()
        motionLayout.startLayoutAnimation()
    }
}