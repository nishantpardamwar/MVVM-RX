package com.nishantpardamwar.unnamed

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View?.keyboardVisibility(show: Boolean) {
    if (this != null) {
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
            if (show)
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            else
                imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }
}