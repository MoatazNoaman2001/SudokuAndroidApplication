package com.example.simplesudokuapp.ui.activegame

interface ActiveGameContainer {
    fun showError()
    fun onNewGameClicked()
}

sealed class ActiveGameEvent {
    data class OnInput(val input: Int) : ActiveGameEvent()
    data class OnTitleFocused(val x:Int , val y:Int) : ActiveGameEvent()
    object OnNewGameClicked:  ActiveGameEvent()
    object OnStart : ActiveGameEvent()
    object OnStop:ActiveGameEvent()
}