package com.example.simplesudokuapp.presistence

import androidx.datastore.core.DataStore
import com.example.simplesudokuapp.GameSettings
import com.example.simplesudokuapp.domain.Difficulty
import com.example.simplesudokuapp.domain.ISettingsStorage
import com.example.simplesudokuapp.domain.Settings
import com.example.simplesudokuapp.domain.SettingsStorageResult


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

class LocalSettingsStorageImpl(
    private val dataStore: DataStore<GameSettings>,
) : ISettingsStorage {
    override suspend fun getSettings(): SettingsStorageResult = withContext(Dispatchers.IO) {
        try {
            val gameSettings = dataStore.data.first()
            SettingsStorageResult.OnSuccess(gameSettings.toSettings)
        } catch (e: Exception) {
            SettingsStorageResult.OnError(e)
        }
    }

    override suspend fun updateSettings(settings: Settings): SettingsStorageResult = withContext(Dispatchers.IO){
        try {
            dataStore.updateData {
                it.toBuilder()
                    .setDiff(settings.difficulty.toProto)
                    .setBoundary(settings.boundary)
                    .build()
            }
            SettingsStorageResult.OnComplete
        }catch (e : Exception){
            SettingsStorageResult.OnError(e)
        }
    }

    private val GameSettings.toSettings: Settings
        get() = Settings(
            this.diff.toDomain,
            this.boundary.verify()
        )

    private val GameSettings.ProtoDiff.toDomain: Difficulty
        get() = when (this.number) {
            1 -> Difficulty.EASY
            2 -> Difficulty.MEDIUM
            3 -> Difficulty.HARD
            else -> Difficulty.MEDIUM
        }

    private val Difficulty.toProto: GameSettings.ProtoDiff
        get() = when (this) {
            Difficulty.EASY -> GameSettings.ProtoDiff.Easy
            Difficulty.MEDIUM -> GameSettings.ProtoDiff.Medium
            Difficulty.HARD -> GameSettings.ProtoDiff.Hard
        }


    private fun Int.verify(): Int {
        return when (this) {
            4 -> this
            9 -> this
            16 -> this
            else -> 4
        }
    }
}