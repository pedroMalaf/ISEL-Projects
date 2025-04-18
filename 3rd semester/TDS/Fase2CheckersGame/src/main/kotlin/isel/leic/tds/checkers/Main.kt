package isel.leic.tds.checkers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import isel.leic.tds.checkers.ui.*
import isel.leic.tds.storage.MongoDriver
import java.lang.reflect.Modifier

@Composable
fun FrameWindowScope.CheckersMenuBar(vm: CheckersViewModel, onExit: () -> Unit) {
    MenuBar {
        Menu("Game") {
            Item("Start", onClick = vm::startClash)
            Item("Refresh", enabled = vm.hasClash(), onClick = vm::refreshBoard)
            Item("Exit", onClick = onExit)
        }
        Menu("Options") {
            CheckboxItem(
                text = "Show targets",
                checked = vm.showTargetsEnabled,
                enabled = vm.hasClash(),
                onCheckedChange = { vm.toggleShowTargets() }
            )
            CheckboxItem(
                text = "Auto-refresh",
                checked = vm.autoRefreshEnabled,
                enabled = vm.hasClash(),
                onCheckedChange = { vm.toggleAutoRefresh() }
            )
        }
    }
}

@Composable
fun FrameWindowScope.CheckersApp(viewModel: CheckersViewModel, onExit: () -> Unit) {
    CheckersMenuBar(viewModel, onExit)
    MaterialTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Grid(
                viewModel.board,
                onClickCell = viewModel::onClickSquare,
                selectedSquares = viewModel.selectedSquares,
                possibleMoves = viewModel.possibleMoves,
                playerID = viewModel.playerID
            )
            StatusBar(viewModel.board, viewModel.id, viewModel.playerID)
        }

        viewModel.currentAction?.let {
            ClashNameEdit(it, viewModel::cancelClash, viewModel::performClash)
        }
    }
}

fun main() {
    MongoDriver("Cluster0").use {driver ->
        application {
            val scope = rememberCoroutineScope()
            val vm = remember { CheckersViewModel(scope, driver) }
            val onExit = { vm.exit(); exitApplication() }
            Window(
                onCloseRequest = ::exitApplication,
                title = "Checkers game",
                state = rememberWindowState(size = DpSize.Unspecified)
            ) {
                CheckersApp(vm, onExit = onExit)
            }
        }
    }
}
