package isel.leic.tds.checkers.ui
import isel.leic.tds.checkers.model.*

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Piece(
    player: Player?,
    isQueen: Boolean = false,
    modifier: Modifier = Modifier.size(100.dp),
    onClick: (() -> Unit)? = null
) {
    if (player == null) {
        Box(
            modifier = onClick?.let { modifier.clickable(onClick = it) } ?: modifier
        )
    } else {
        val piece = when (player) {
            Player.BLACK -> if (isQueen) "bk" else "b"
            Player.WHITE -> if (isQueen) "wk" else "w"
        }

        Image(
            painter = painterResource("piece_$piece.png"),
            contentDescription = piece,
            modifier = modifier
        )
    }
}

@Composable
@Preview
fun PiecePreview(){
    Column {
        Piece(Player.WHITE)
        Piece(Player.BLACK)
    }
}


