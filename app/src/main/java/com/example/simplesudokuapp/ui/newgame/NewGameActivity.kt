package com.example.simplesudokuapp.ui.newgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplesudokuapp.common.makeToast
import com.example.simplesudokuapp.ui.activegame.MainActivity
import com.example.simplesudokuapp.ui.theme.SimpleSudokuAppTheme

private const val TAG = "NewGameActivity"
class NewGameActivity : ComponentActivity() , NewGameContainer{
    lateinit var logic: NewGameLogic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = NewGameViewModel()

        setContent {
            SimpleSudokuAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NewGameScreen(onEventHandler = logic::onEvent, viewModel = viewModel
                    )
                }
            }
        }

        logic = buildNewGameLogic(this , viewModel , applicationContext)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: new Game Launched")
        logic.onEvent(NewGameEvent.OnStart)
    }
    override fun showError() =makeToast("error happened in launch new game")

    override fun onDoneClick() {
        startActiveGameActivity()
    }

    override fun onBackPressed() {
        startActiveGameActivity()
        super.onBackPressed()
    }

    fun startActiveGameActivity(){
        startActivity(
            Intent(this , MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}

