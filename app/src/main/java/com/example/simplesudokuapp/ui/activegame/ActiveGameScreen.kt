package com.example.simplesudokuapp.ui.activegame

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.simplesudokuapp.R
import com.example.simplesudokuapp.common.toTime
import com.example.simplesudokuapp.computationlogic.sqrt
import com.example.simplesudokuapp.ui.compenents.AppToolbar
import com.example.simplesudokuapp.ui.compenents.LoadingScreen
import com.example.simplesudokuapp.ui.theme.activeGameSubtitle
import com.example.simplesudokuapp.ui.theme.inputButton
import com.example.simplesudokuapp.ui.theme.mutableSudokuSquare
import com.example.simplesudokuapp.ui.theme.newGameSubtitle
import com.example.simplesudokuapp.ui.theme.readOnlySudokuSquare
import com.example.simplesudokuapp.ui.theme.textColorDark
import com.example.simplesudokuapp.ui.theme.textColorLight
import com.example.simplesudokuapp.ui.theme.userInputtedNumberDark
import com.example.simplesudokuapp.ui.theme.userInputtedNumberLight
import java.util.HashMap

enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}


@Composable
fun ActiveGameScreen(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel,
) {


    //In very simple language, whenever we have some kind of data, or state, which may change at
    //runtime, we want to wrap that data in a remember delegate. This tells the tells the compose
    //library under the hood, to watch for changes, and to redraw the UI if a change occurs.
    val contentTransitionState = remember {
        MutableTransitionState(
            ActiveGameScreenState.LOADING
        )
    }

    //Our remember delegate prepares compose for updates, but we also need a way to actually update
    //the value. We do this by binding a lambda expression to one of the Function Types which
    //our ViewModel possesses. When one of those functions is invoked in the ViewModel,
    //the program automatically jumps to and executes this code within our composable.
    //This is what actually triggers the Recomposition.
    viewModel.subContentState = {
        contentTransitionState.targetState = it
    }

    //We have a remembered transition state, and a way to update that state from the ViewModel.
    //Now we need to set up the transitions animations themselves. This is where you can get as
    //creative as you like. In this app, each content state has it's own composable associated
    //with it. We animate between them simply by changing the alpha, or transparency.
    val transition = updateTransition(contentTransitionState)

    val loadingAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.LOADING) 1f else 0f
    }

    val activeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.ACTIVE) 1f else 0f
    }

    val completeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.COMPLETE) 1f else 0f
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxHeight()
    ) {

        AppToolbar(
            modifier = Modifier.wrapContentHeight(),
            title = stringResource(id = R.string.app_name)
        ) {
            NewGameIcon(onEventHandler = onEventHandler)
        }

        Box(
            modifier = Modifier.fillMaxHeight().padding(4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (contentTransitionState.currentState) {
                ActiveGameScreenState.LOADING -> Box(modifier = Modifier.alpha(loadingAlpha)) {
                    LoadingScreen()
                }

                ActiveGameScreenState.ACTIVE -> Box(modifier = Modifier.alpha(activeAlpha)) {
                    GameContent(onEventHandler, viewModel)
                }

                ActiveGameScreenState.COMPLETE -> Box(modifier = Modifier.alpha(completeAlpha)) {
                    GameCompleteContent(viewModel.timerState, viewModel.isNewRecordState)
                }
            }
        }
    }
}

@Composable
fun GameCompleteContent(timerState: Long, newRecordState: Boolean) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                Modifier.size(128.dp)
            )

            if (newRecordState) Image(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier.size(128.dp)
            )

            Text(
                text = "Total Time", style = newGameSubtitle.copy(
                    color = MaterialTheme.colors.secondary
                )
            )
            Text(
                text = timerState.toTime(),
                style = newGameSubtitle.copy(
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Normal
                )
            )

        }
    }
}

@Composable
fun GameContent(onEventHandler: (ActiveGameEvent) -> Unit, viewModel: ActiveGameViewModel) {
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }
        val margin = with(LocalDensity.current) {
            when {
                constraints.maxHeight.toDp().value < 500f -> 20
                constraints.maxHeight.toDp().value < 550f -> 8
                else -> 0
            }
        }

//        val offset = with(LocalDensity.current){
//            when {
//                constraints.maxHeight.toDp().value < 500f ->20
//                constraints.maxHeight.toDp().value < 550f ->8
//                else -> 0
//            }
//        }.let {
//            screenWidth.value - it / viewModel.boundary
//        }


        ConstraintLayout {
            val (board, timer, diff, inputs) = createRefs()
            Box(
                modifier = Modifier
                    .constrainAs(board) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(MaterialTheme.colors.surface)
                    .size(screenWidth - margin.dp)
                    .border(width = 2.dp, color = MaterialTheme.colors.primary)
            ) {
                ScreenBoard(onEventHandler, viewModel, screenWidth - margin.dp)

            }

            //Next, we create a layout container for the countdown timer
            Box(Modifier
                .wrapContentSize()
                .constrainAs(timer) {
                    top.linkTo(board.bottom)
                    start.linkTo(parent.start)
                }
                .padding(start = 16.dp))
            {
                TimerText(viewModel)
            }

            //This container is for some icons which indicate the difficulty
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(diff) {
                        top.linkTo(board.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                (0..viewModel.difficulty.ordinal).forEach {
                    Icon(
                        contentDescription = stringResource(R.string.difficulty),
                        imageVector = Icons.Filled.Star,
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 4.dp)
                    )
                }
            }

            //this container holds the input buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(inputs) {
                        top.linkTo(timer.bottom)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Now, hard coding this is kind of bad practice, but the reason is that the
                //Compose team deprecated FlowRow which worked perfectly for this situation X(
                if (viewModel.boundary == 4) {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )
                } else {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )

                    InputButtonRow(
                        (5..9).toList(),
                        onEventHandler
                    )
                }
            }
        }
    }
}

@Composable
fun TimerText(viewModel: ActiveGameViewModel) {
    var timerState by remember {
        mutableStateOf("")
    }

    viewModel.subTimerState = {
        timerState = it.toTime()
    }

    Text(
        modifier = Modifier.requiredHeight(36.dp),
        text = timerState,
        style = activeGameSubtitle.copy(color = MaterialTheme.colors.secondary)
    )
}

@Composable
fun InputButtonRow(
    numbers: List<Int>,
    onEventHandler: (ActiveGameEvent) -> Unit
) {
    Row {
        numbers.forEach {
            SudokuInputButton(
                onEventHandler,
                it
            )
        }
    }

    //margin between rows
    Spacer(modifier = Modifier.size(2.dp))
}

@Composable
fun SudokuInputButton(
    onEventHandler: (ActiveGameEvent) -> Unit,
    number: Int
) {
    //This wrapper allows us to style a nice looking button instead of just adding onClick on a
    //text composable
    TextButton(
        //Here is how we handle click events using onClick and our onEventHandler
        onClick = { onEventHandler.invoke(ActiveGameEvent.OnInput(number)) },
        modifier = Modifier
            .requiredSize(56.dp)
            .padding(2.dp),
        border = BorderStroke(ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.onPrimary),

        ) {
        Text(
            text = number.toString(),
            style = inputButton.copy(color = MaterialTheme.colors.onPrimary),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ScreenBoard(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel,
    size: Dp,
) {
    val boundary = viewModel.boundary
    val tileOffSet = size.value / boundary

    var boardState by remember {
        mutableStateOf(viewModel.boardState, neverEqualPolicy())
    }

    viewModel.subBoarderState = {
        boardState = it
    }

    SudokuTextFields(onEventHandler, tileOffSet, boardState)
    BoardGrid(boundary, tileOffSet)
}

@Composable
fun BoardGrid(boundary: Int, tileOffSet: Float) {

    (1 until boundary).forEach {
        val width = if (it % boundary.sqrt == 0) 3.dp
        else 1.dp
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier
                .absoluteOffset((tileOffSet * it).dp, 0.dp)
                .fillMaxHeight()
                .width(width)
        )

        val height = if (it % boundary.sqrt == 0) 3.dp
        else 1.dp
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier
                .absoluteOffset(0.dp, (tileOffSet * it).dp)
                .fillMaxWidth()
                .height(height)
        )
    }
}

@Composable
fun SudokuTextFields(
    onEventHandler: (ActiveGameEvent) -> Unit,
    tileOffSet: Float,
    boardState: HashMap<Int, SudokuTitle>,
) {
    boardState.values.forEach {
        var text = it.value.toString()
        if (!it.readOnly) {
            if (text == "0") text = ""


            Text(
                text = text,
                style = mutableSudokuSquare(tileOffSet).copy(color = if (MaterialTheme.colors.isLight) userInputtedNumberLight else userInputtedNumberDark),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOffSet * (it.x - 1)).dp,
                        (tileOffSet * (it.y - 1)).dp
                    )
                    .size(tileOffSet.dp)
                    .background(
                        if (it.hasFocus) MaterialTheme.colors.onPrimary.copy(alpha = .25f)
                        else MaterialTheme.colors.surface
                    )
                    .clickable {
                        onEventHandler.invoke(
                            ActiveGameEvent.OnTitleFocused(it.x, it.y)
                        )
                    }
            )
        } else {
            Text(
                text = text, style = readOnlySudokuSquare(tileOffSet),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOffSet * (it.x - 1)).dp,
                        (tileOffSet * (it.y - 1)).dp
                    )
                    .size(tileOffSet.dp)
            )
        }
    }
}

@Composable
fun NewGameIcon(onEventHandler: (ActiveGameEvent) -> Unit) {
    Icon(
        imageVector = Icons.Filled.Add,
        tint = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
        modifier = Modifier
            .clickable {
                onEventHandler.invoke(ActiveGameEvent.OnNewGameClicked)
            }
            .padding(16.dp)
            .height(36.dp),
        contentDescription = null
    )
}
