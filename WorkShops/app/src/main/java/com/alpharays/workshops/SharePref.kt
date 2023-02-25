package com.alpharays.workshops

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun SharedPreferences.getBooleanLiveData(
    key: String,
    defaultValue: Boolean
): LiveData<Boolean> {
    return object : MutableLiveData<Boolean>(getBoolean(key, defaultValue)) {
        private val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == key) {
                    value = getBoolean(key, defaultValue)
                }
            }

        override fun onActive() {
            super.onActive()
            registerOnSharedPreferenceChangeListener(listener)
        }

        override fun onInactive() {
            super.onInactive()
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}
