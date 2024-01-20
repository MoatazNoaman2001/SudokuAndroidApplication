package com.example.simplesudokuapp.ui.activegame

import android.util.Log
import com.example.simplesudokuapp.common.BaseLogic
import com.example.simplesudokuapp.common.DispatcherProvider
import com.example.simplesudokuapp.domain.IGameRepository
import com.example.simplesudokuapp.domain.IStatisticsRepository
import com.example.simplesudokuapp.domain.SudokuPuzzle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects
import kotlin.coroutines.CoroutineContext

private const val TAG = "ActiveGameLogic"

class ActiveGameLogic(
    private val container: ActiveGameContainer,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val staticRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider,
) : BaseLogic<ActiveGameEvent>(), CoroutineScope {

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit,
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    private var timerTracker: Job? = null


    private val Long.timeOffset: Long
        get() {
            return if (this <= 0) 0
            else this - 1
        }

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(event.input, viewModel.timerState)
            is ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            is ActiveGameEvent.OnStart -> onStart()
            is ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTitleFocused -> onTileFocused(event.x, event.y)
        }
    }

    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker


    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    private fun onStop() {
        if (!viewModel.isCompletedState) {
            launch {
                gameRepo.saveGame(
                    elapsedTime = viewModel.timerState.timeOffset,
                    {
                        cancleStuff()
                    },
                    {
                        container.showError()
                        cancleStuff()
                    }
                )
            }
        } else {
            cancleStuff()
        }
    }

    private fun onStart() = launch {
        Log.d(TAG, "onStart: onStart called inside active game logic")

        Log.d(TAG, "onNewGameClicked: game repo: ${Objects.isNull(gameRepo)}")
        Log.d(TAG, "onNewGameClicked: viewModel: ${Objects.isNull(viewModel)}")
        Log.d(TAG, "onNewGameClicked: timerTracker: ${Objects.isNull(timerTracker)}")

        gameRepo.getCurrentGame(
            { puzzle, isCompleted ->
                viewModel.initializeGameState(puzzle, isCompleted)
                Log.d(TAG, "onNewGameClicked: isCompleted: ${!isCompleted}")
                Log.d(TAG, "onStart: new game initialized")
                if (!isCompleted) {
                    timerTracker = startCoroutineTimer {
                        viewModel.updateTimerState()
                    }
                }
            },
            {
                Log.d(TAG, "onStart: error triggered")
                container.onNewGameClicked()
            }
        )
    }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        if (!viewModel.isCompletedState) {
            gameRepo.getCurrentGame(
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                {
                    container.showError()
                }
            )
        } else {
            NavigateToNewGame()
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            puzzle.copy(elapsedTime = viewModel.timerState.timeOffset),
            { NavigateToNewGame() }, { container.showError(); NavigateToNewGame() }
        )
    }

    private fun NavigateToNewGame() {
        cancleStuff()
        container.onNewGameClicked()
    }

    private fun cancleStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTitle: SudokuTitle? = null

        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTitle = it
        }
        if (focusedTitle != null) {
            gameRepo.updateNode(
                focusedTitle!!.x,
                focusedTitle!!.y,
                input,
                elapsedTime,

                //success
                {
                    focusedTitle?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }

                    if (it) {
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                //Error
                {
                    container.showError()
                }
            )
        }
    }

    private fun checkIfNewRecord() = launch {
        staticRepo.updateStatistic(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            {
                container.showError()
                viewModel.updateCompleteState()
            }
        )
    }


}