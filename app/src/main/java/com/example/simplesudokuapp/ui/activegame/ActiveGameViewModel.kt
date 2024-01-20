package com.example.simplesudokuapp.ui.activegame

import com.example.simplesudokuapp.domain.Difficulty
import com.example.simplesudokuapp.domain.SudokuPuzzle
import com.example.simplesudokuapp.domain.getHash
import org.w3c.dom.Node

class ActiveGameViewModel {
    internal var subBoarderState: ((HashMap<Int, SudokuTitle>) -> Unit)? = null
    internal var subContentState: ((ActiveGameScreenState) -> Unit)? = null
    internal var subTimerState: ((Long) -> Unit)? = null

    internal fun updateTimerState() {
        timerState++
        subTimerState?.invoke(timerState)
    }

    internal var subIsCompleteState: ((Boolean) -> Unit)? = null

    internal var timerState: Long = 0L
    internal var difficulty = Difficulty.MEDIUM
    internal var boundary = 9
    internal var boardState: HashMap<Int, SudokuTitle> = HashMap()

    internal var isCompletedState: Boolean = false
    internal var isNewRecordState: Boolean = false

    internal fun initializeGameState(
        puzzle: SudokuPuzzle,
        isComplete: Boolean,
    ) {
        puzzle.graph.forEach {
            val node = it.value[0]
            boardState[it.key] = SudokuTitle(
                node.x,
                node.y,
                node.color,
                hasFocus = false,
                node.readOnly
            )
        }

        val contentState: ActiveGameScreenState

        if (isComplete) {
            isCompletedState = true
            contentState = ActiveGameScreenState.COMPLETE
        } else {
            contentState = ActiveGameScreenState.ACTIVE
        }

        boundary = puzzle.boundary
        difficulty = puzzle.difficulty
        timerState = puzzle.elapsedTime

        subIsCompleteState?.invoke(isCompletedState)
        subContentState?.invoke(contentState)
        subBoarderState?.invoke(boardState)
    }

    internal fun updateBoardState(x: Int, y: Int, value: Int, hasFocus: Boolean) {
        boardState[getHash(x , y)]?.let {
            it.value = value
            it.hasFocus = hasFocus
        }

        subBoarderState?.invoke(boardState)
    }

    internal fun showLoadingState(){
        subContentState?.invoke(ActiveGameScreenState.LOADING)
    }

    internal fun updateFocusState(x:Int , y:Int){
        boardState.values.forEach {
            it.hasFocus = it.x == x && it.y == y
        }

        subBoarderState?.invoke(boardState)
    }

    fun updateCompleteState(){
        isCompletedState = true
        subContentState?.invoke(ActiveGameScreenState.COMPLETE)
    }
}

class SudokuTitle(
    val x: Int,
    val y: Int,
    var value: Int,
    var hasFocus: Boolean,
    val readOnly: Boolean,
)