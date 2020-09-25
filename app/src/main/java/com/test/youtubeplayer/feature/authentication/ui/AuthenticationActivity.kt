package com.test.youtubeplayer.feature.authentication.ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.coroutineScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.test.youtubeplayer.R
import com.test.youtubeplayer.base.activity.BaseActivity
import com.test.youtubeplayer.databinding.ActivityAuthenticationBinding
import com.test.youtubeplayer.feature.authentication.GoogleSingInResultContract
import com.test.youtubeplayer.feature.playlist.ui.PlayListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity<ActivityAuthenticationBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_authentication
    }

    override fun initialize() {
        binding.btnSignIn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signIn.launch(null)
        }
    }

    private val signIn = registerForActivityResult(GoogleSingInResultContract()) { token ->
        token?.let {
            lifecycle.coroutineScope.launch {
                val resultSuccess = signInWithToken(token, this@AuthenticationActivity)
                binding.progressBar.visibility = View.GONE
                if (resultSuccess) {
                    val intent = PlayListActivity.create(this@AuthenticationActivity)
                    this@AuthenticationActivity.startActivity(intent)
                } else {
                    showSignInFailed()
                }

            }
        } ?: apply {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSignInFailed() {
        Snackbar.make(binding.root, R.string.signin_failed, Snackbar.LENGTH_SHORT)
            .show()
    }

    private suspend fun signInWithToken(idToken: String, activity: Activity): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCoroutine { cont ->
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        cont.resume(true)
                    } else {
                        cont.resume(false)
                    }
                }
        }

    }
}

