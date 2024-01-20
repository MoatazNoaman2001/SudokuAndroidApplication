package com.example.simplesudokuapp.presistence

import com.example.simplesudokuapp.common.ProductionDispatcherProvider
import com.example.simplesudokuapp.domain.GameStorageResult
import com.example.simplesudokuapp.domain.IGameDataStorage
import com.example.simplesudokuapp.domain.SudokuPuzzle
import com.example.simplesudokuapp.domain.getHash
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


private const val FILE_NAME = "game_state.txt"

class LocalGameStorageImpl(
    fileToStorageDirectory: String,
    private val pathToStorageFile: File = File(fileToStorageDirectory, FILE_NAME),
) : IGameDataStorage {
    override suspend fun updateGame(game: SudokuPuzzle): GameStorageResult =
        withContext(ProductionDispatcherProvider.provideIoContext()) {
            try {
                updateGameData(game)
                GameStorageResult.OnSuccess(game)

            } catch (e: Exception) {
                GameStorageResult.OnError(e)
            }
        }

    private fun updateGameData(game: SudokuPuzzle) {
        ObjectOutputStream(FileOutputStream(pathToStorageFile)).apply {
            writeObject(game)
            close()
        }
    }

    override suspend fun updateNode(x: Int, y: Int,color :Int,  elapsedTime: Long): GameStorageResult =
        withContext(ProductionDispatcherProvider.provideIoContext()) {
            try {
                val game = getGame()
                game.graph[getHash(x, y)]!!.first.color = color
                game.elapsedTime = elapsedTime

                updateGameData(game)
                GameStorageResult.OnSuccess(game)
            } catch (e: Exception) {
                GameStorageResult.OnError(e)
            }
        }

    private fun getGame(): SudokuPuzzle {
        return ObjectInputStream(FileInputStream(pathToStorageFile)).let {
            it.readObject() as SudokuPuzzle
        }
    }

    override suspend fun getCurrentGame(): GameStorageResult = withContext(ProductionDispatcherProvider.provideIoContext()){
        try {
            GameStorageResult.OnSuccess(getGame())
        }catch (e:Exception){
            GameStorageResult.OnError(e)
        }
    }
}