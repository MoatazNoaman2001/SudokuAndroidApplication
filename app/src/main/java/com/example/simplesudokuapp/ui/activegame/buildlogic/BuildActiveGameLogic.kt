package com.example.simplesudokuapp.ui.activegame.buildlogic

import android.content.Context
import com.example.simplesudokuapp.persistance.settingsDataStore
import com.example.simplesudokuapp.persistance.statsDataStore
import com.example.simplesudokuapp.common.ProductionDispatcherProvider
import com.example.simplesudokuapp.presistence.GameRepositoryImpl
import com.example.simplesudokuapp.presistence.LocalGameStorageImpl
import com.example.simplesudokuapp.presistence.LocalSettingsStorageImpl
import com.example.simplesudokuapp.presistence.LocalStatisticsStorageImpl
import com.example.simplesudokuapp.ui.activegame.ActiveGameContainer
import com.example.simplesudokuapp.ui.activegame.ActiveGameLogic
import com.example.simplesudokuapp.ui.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic{
    return ActiveGameLogic(
        container ,
        viewModel ,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(context.statsDataStore),
        ProductionDispatcherProvider
    )
}