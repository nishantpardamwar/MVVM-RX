package com.nishantpardamwar.unnamed

import android.content.res.Resources

object Utils {
    fun dp2px(dpVal: Int): Int {
        return (dpVal * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }
}