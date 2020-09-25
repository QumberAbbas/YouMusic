package com.test.youtubeplayer.network

import androidx.lifecycle.LiveData

object NetworkEvents : LiveData<Event>(Event.ConnectivityLost) {
    internal fun notify(event: Event) {
        postValue(event)
    }
}