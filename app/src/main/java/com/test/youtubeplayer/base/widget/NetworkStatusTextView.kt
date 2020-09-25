package com.test.youtubeplayer.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.textview.MaterialTextView
import com.test.youtubeplayer.R
import com.test.youtubeplayer.network.Event
import com.test.youtubeplayer.network.NetworkEvents
import dagger.hilt.android.internal.managers.ViewComponentManager

class NetworkStatusTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {

    init {
        visibility = View.GONE
        text = context.getString(R.string.offline_status)
        updateView(NetworkEvents.value)
        val lifeCycleOwners = if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
        }
        NetworkEvents.observe(lifeCycleOwners as LifecycleOwner, Observer {
            updateView(it)
        })
    }

    private fun updateView(it: Event?) {
        if (it is Event.ConnectivityAvailable) {
            visibility = View.GONE
        } else if (it is Event.ConnectivityLost) {
            visibility = View.VISIBLE
        }
    }

}