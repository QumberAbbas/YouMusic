package com.test.youtubeplayer.extensions

import androidx.constraintlayout.motion.widget.MotionLayout

fun MotionLayout.setOnTransitionCompleted(action: () -> Unit) {
    setTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            action()
        }

        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
    })
}