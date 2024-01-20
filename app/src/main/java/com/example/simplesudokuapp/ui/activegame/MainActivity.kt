package com.example.simplesudokuapp.ui.activegame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.simplesudokuapp.common.makeToast
import com.example.simplesudokuapp.ui.newgame.NewGameActivity
import com.example.simplesudokuapp.ui.theme.SimpleSudokuAppTheme
import com.example.simplesudokuapp.ui.activegame.ActiveGameContainer
import com.example.simplesudokuapp.ui.activegame.ActiveGameEvent
import com.example.simplesudokuapp.ui.activegame.ActiveGameLogic
import com.example.simplesudokuapp.ui.activegame.ActiveGameScreen
import com.example.simplesudokuapp.ui.activegame.ActiveGameViewModel
import com.example.simplesudokuapp.ui.activegame.buildlogic.buildActiveGameLogic

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() , ActiveGameContainer{
    private lateinit var logic : ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            SimpleSudokuAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActiveGameScreen(onEventHandler = logic::onEvent, viewModel = viewModel)
                }
            }
        }

        logic = buildActiveGameLogic(this, viewModel , applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)
        finish()
    }

    override fun showError() = makeToast("error has been occurred")
    override fun onNewGameClicked() {
        startActivity(
            Intent(this, NewGameActivity::class.java)
        )
    }



}
