package com.example.simplesudokuapp.presistence

import com.example.simplesudokuapp.computationlogic.puzzleIsComplete
import com.example.simplesudokuapp.domain.GameStorageResult
import com.example.simplesudokuapp.domain.IGameDataStorage
import com.example.simplesudokuapp.domain.IGameRepository
import com.example.simplesudokuapp.domain.ISettingsStorage
import com.example.simplesudokuapp.domain.Settings
import com.example.simplesudokuapp.domain.SettingsStorageResult
import com.example.simplesudokuapp.domain.SudokuPuzzle

class GameRepositoryImpl(
    val gameStorage: IGameDataStorage,
    val settingsStorage: ISettingsStorage,
) : IGameRepository {
    override suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        when (val getCurrentGameResult = gameStorage.getCurrentGame()) {
            is GameStorageResult.OnError -> {
                onError(getCurrentGameResult.exception)
            }

            is GameStorageResult.OnSuccess -> {
                updateGame(getCurrentGameResult.currentGame.copy(elapsedTime = elapsedTime),
                    {
                        onSuccess(Unit)
                    }, {
                        onError(it)
                    })

            }
        }
    }

    override suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        when (val updateGameResult = gameStorage.updateGame(game)) {
            is GameStorageResult.OnError -> onError(updateGameResult.exception)
            is GameStorageResult.OnSuccess -> onSuccess(Unit)
        }
    }

    override suspend fun CreateNewGame(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit,
    ) {

        when (val updateSettingResult = settingsStorage.updateSettings(settings)) {
            is SettingsStorageResult.OnSuccess -> {
                when (val newGame = createAndWriteNewGame(settings)) {
                    is GameStorageResult.OnSuccess -> onSuccess(Unit)
                    is GameStorageResult.OnError -> onError(newGame.exception)
                }
            }

            is SettingsStorageResult.OnError -> onError(updateSettingResult.exception)
            is SettingsStorageResult.OnComplete -> {
                onSuccess(Unit)
            }
        }
    }

    private suspend fun createAndWriteNewGame(settings: Settings): GameStorageResult {
        return gameStorage.updateGame(
            SudokuPuzzle(settings.boundary, settings.difficulty)
        )
    }

    override suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isCompleted: Boolean) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        when (val getCurrentGame = gameStorage.getCurrentGame()) {
            is GameStorageResult.OnError -> {
                when (val getSettingResult = settingsStorage.getSettings()) {
                    is SettingsStorageResult.OnSuccess -> {
                        when (val updateGameResult = createAndWriteNewGame(getSettingResult.settings)) {
                            is GameStorageResult.OnSuccess -> onSuccess(
                                updateGameResult.currentGame,
                                puzzleIsComplete(updateGameResult.currentGame)
                            )

                            is GameStorageResult.OnError -> onError(updateGameResult.exception)
                        }
                    }

                    is SettingsStorageResult.OnError -> {
                        onError(getSettingResult.exception)
                    }

                    SettingsStorageResult.OnComplete -> {

                    }
                }
            }

            is GameStorageResult.OnSuccess -> {
                puzzleIsComplete(getCurrentGame.currentGame)
            }
        }
    }


    override suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit) {

        when (val getSetting = settingsStorage.getSettings()) {
            is SettingsStorageResult.OnSuccess -> onSuccess(getSetting.settings)
            is SettingsStorageResult.OnError -> onError(getSetting.exception)
            SettingsStorageResult.OnComplete -> Unit
        }
    }

    override suspend fun updateSetting(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        when (val updateSetting = settingsStorage.updateSettings(settings)) {
            is SettingsStorageResult.OnError -> onError(updateSetting.exception)
            is SettingsStorageResult.OnSuccess -> onSuccess(Unit)
            SettingsStorageResult.OnComplete -> Unit
        }
    }

    override suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        when (val result = gameStorage.updateNode(x, y, color, elapsedTime)) {
            is GameStorageResult.OnSuccess -> onSuccess(
                puzzleIsComplete(result.currentGame)
            )

            is GameStorageResult.OnError -> onError(
                result.exception
            )
        }
    }
}