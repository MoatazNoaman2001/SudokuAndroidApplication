package com.example.simplesudokuapp.ui.newgame

import android.content.Context
import com.example.simplesudokuapp.common.ProductionDispatcherProvider
import com.example.simplesudokuapp.persistance.settingsDataStore
import com.example.simplesudokuapp.persistance.statsDataStore
import com.example.simplesudokuapp.presistence.GameRepositoryImpl
import com.example.simplesudokuapp.presistence.LocalGameStorageImpl
import com.example.simplesudokuapp.presistence.LocalSettingsStorageImpl
import com.example.simplesudokuapp.presistence.LocalStatisticsStorageImpl


internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}