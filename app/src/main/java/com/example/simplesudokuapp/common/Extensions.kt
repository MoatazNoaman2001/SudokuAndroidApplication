package com.example.simplesudokuapp.common

import android.app.Activity
import android.widget.Toast
import com.example.simplesudokuapp.R
import com.example.simplesudokuapp.domain.Difficulty

internal fun Activity.makeToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

internal fun Long.toTime(): String {
    if (this > 3600) return "+59:59"

    var minutes = ((this % 3600) / 60).toString()
    if (minutes.length == 1) minutes = "0$minutes"
    var seconds = (this % 60).toString()
    if (seconds.length == 1) seconds = "0$seconds"

    return String.format("$minutes:$seconds")
}

internal val Difficulty.toLocalizedResource: Int
    get() {
        return when(this){
            Difficulty.EASY -> R.string.Easy
            Difficulty.MEDIUM -> R.string.Medium
            Difficulty.HARD -> R.string.Hard
        }
    }