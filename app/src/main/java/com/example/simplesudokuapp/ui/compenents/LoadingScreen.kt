package com.example.simplesudokuapp.ui.compenents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simplesudokuapp.R
import com.example.simplesudokuapp.ui.theme.lightGrey
import com.example.simplesudokuapp.ui.theme.mainTitle

@Composable
fun LoadingScreen() {
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.mipmap.ic_launcher_foreground), contentDescription = "", modifier = Modifier.size(122.dp))
            LinearProgressIndicator(color = lightGrey , modifier = Modifier.width(128.dp).padding(16.dp))
        }
    }
}