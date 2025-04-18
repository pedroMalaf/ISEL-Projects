package isel.leic.tds.checkers.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.model.Square.Companion.blackSquares
import kotlin.text.get

//VIRAR ISTO QUANDO FOR O JOGADOR DIFERENTE, TIPO SE FOR AS PRETAS A JOGAR TEMOS DE VIRAR AS "CASAS"
// VISTO QUE HÁ UM JOGADOR DE CADA LADO DO TABULEIRO

val CELL_SIDE = 50.dp
val space = 25.dp
val GRID_SIZE = CELL_SIDE * BOARD_DIM + space

fun enableSquare(currentSquare: Square): Boolean = currentSquare in blackSquares


@Composable
@Preview
fun GridPreview() {
    val board = Game().board.play("3c".toSquare(), "4d".toSquare()) //null
    //Grid(board, {})
}

@Composable
fun RowText(text: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        text.forEach {
            Text(
                text = "$it",
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ShowPieces(board: Board?, currentSquare: Square) {
    Box {
        board?.moves?.get(currentSquare)?.let { piece ->
            Piece(
                player = piece.player,
                isQueen = piece is Queen,
                modifier = Modifier.size(CELL_SIDE)
            )
        }
    }
}

@Composable
fun Grid(
    board: Board?,
    onClickCell: (Square) -> Unit,
    selectedSquares: List<Square> = emptyList(),
    possibleMoves: List<Square> = emptyList(),
    playerID: Player?
) {
    val rows = if (playerID == Player.BLACK) (0 until BOARD_DIM).reversed() else (0 until BOARD_DIM)
    val cols = if (playerID == Player.BLACK) (0 until BOARD_DIM).reversed() else (0 until BOARD_DIM)
    val columnLabels = if (playerID == Player.BLACK) Column.values.reversed() else Column.values

    Column(
        Modifier
            .size(GRID_SIZE + space)
            .background(Color(0xFF994D1C))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.size(GRID_SIZE).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val text = columnLabels.map { it.symbol }.joinToString("")
            RowText(text)

            for (row in rows) {
                Row {
                    Text("${Row(row).digit}")
                    for (col in cols) {
                        val currentSquare = Square(Row(row), Column(col))
                        val isSelected = currentSquare in selectedSquares
                        val isPossibleMove = currentSquare in possibleMoves
                        val backGround = if (currentSquare in blackSquares) Color.DarkGray else Color.White

                        Box(
                            Modifier
                                .background(backGround)
                                .size(CELL_SIDE)
                                .let { mod ->
                                    if (isSelected) mod.border(2.dp, Color.Red)
                                    else mod
                                }
                                .clickable(enabled = enableSquare(currentSquare))
                                {
                                    onClickCell(currentSquare)
                                }
                        ) {
                            if (isPossibleMove) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .background(Color.Green.copy(alpha = 0.5f), CircleShape)
                                        .align(Alignment.Center)
                                )
                            }
                            ShowPieces(board, currentSquare)
                        }
                    }
                }
            }
        }
    }
}