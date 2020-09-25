package com.test.youtubeplayer.feature.splash.router

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.test.youtubeplayer.feature.authentication.ui.AuthenticationActivity
import com.test.youtubeplayer.feature.playlist.ui.PlayListActivity

object SplashRouter {
    fun routeToNextScreen(context: Context) {
        if (GoogleSignIn.getLastSignedInAccount(context) != null) {
            routeToPlayListScreen(context)
        } else {
            routeToAuthScreen(context)
        }
    }

    private fun routeToAuthScreen(context: Context) {
        context.startActivity(Intent(context, AuthenticationActivity::class.java))
    }

    private fun routeToPlayListScreen(context: Context) {
        val intent = PlayListActivity.create(context)
        context.startActivity(intent)
    }
}