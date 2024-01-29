package com.example.filmer

import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

object AnimationHelper {
    private const val menuItems = 4

    fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {

        Executors.newSingleThreadExecutor().execute {

            while (true) {

                if (rootView.isAttachedToWindow) {

                    activity.runOnUiThread {

                        val x: Int =
                            (rootView.width / (menuItems * 2)) + (rootView.width / menuItems) * (position - 1)
                        val y: Int = rootView.y.roundToInt() + rootView.height

                        val startRadius = 0
                        val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())

                        ViewAnimationUtils.createCircularReveal(
                            rootView,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        ).apply {

                            duration = 400

                            interpolator = AccelerateDecelerateInterpolator()

                            start()
                        }

                        rootView.visibility = View.VISIBLE
                    }
                    return@execute
                }
            }
        }
    }
}