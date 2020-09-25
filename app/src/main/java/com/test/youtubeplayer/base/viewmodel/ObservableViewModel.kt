package com.test.youtubeplayer.base.viewmodel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel(), Observable {

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        removeOnPropertyChangedCallback(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        addOnPropertyChangedCallback(callback)
    }
}