package com.example.simplesudokuapp.ui.newgame

import com.example.simplesudokuapp.domain.Difficulty


sealed class NewGameEvent {
    object OnStart: NewGameEvent()
    data class OnSizeChanged(val boundary: Int): NewGameEvent()
    data class OnDifficultyChanged(val diff: Difficulty): NewGameEvent()
    object OnDonePressed: NewGameEvent()
}