package com.evw.aster


import androidx.lifecycle.Observer

class EventObserverop<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Eventop<T>> {
    override fun onChanged(value: Eventop<T>) {
        value.getContentIfNotHandled()?.let { it ->
            onEventUnhandledContent(it)
        }
    }
}