package isel.leic.tds.checkers.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import isel.leic.tds.checkers.model.*

@Composable
fun StatusBar(board: Board?, id: String?, you: Player?){
    Row (
        modifier = Modifier.width(GRID_SIZE + space).height(space).background(Color(0xFF994D1C)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val state = when(board){
            is BoardRun -> {
                "Game: $id   You: $you   Turn: ${board.turn}"
            }
            is BoardFinish -> "Winner: ${board.winner}"
            null -> "No board"
        }
        Text(state, style = MaterialTheme.typography.h6)
    }
}

@Composable
@Preview
fun StatusBarPreview() = StatusBar(Game().board, "game1", Player.WHITE)