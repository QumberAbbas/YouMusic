package com.test.youtubeplayer.preferences

import android.content.Context
import android.content.SharedPreferences
import com.test.youtubeplayer.preferences.PreferenceKeys.KEY_IS_CHANNEL_AVAILABLE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_PREF_NAME = "YOU_MUSIC_PREF"

@Singleton
open class AppPrefrences @Inject constructor(@ApplicationContext val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(APP_PREF_NAME, 0);
    }

    open var isChannelAvailable: Boolean
        get() = prefs[KEY_IS_CHANNEL_AVAILABLE, false]
        set(value) {
            prefs[KEY_IS_CHANNEL_AVAILABLE] = value
        }
}

