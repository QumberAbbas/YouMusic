package com.test.youtubeplayer.network

sealed class Event {
    object ConnectivityLost : Event()
    object ConnectivityAvailable : Event()
}