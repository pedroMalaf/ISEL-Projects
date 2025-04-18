package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.BOARD_DIM
import isel.leic.tds.checkers.model.BoardFinish
import isel.leic.tds.checkers.model.BoardRun
import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.model.Queen
import isel.leic.tds.checkers.model.Square

fun Game.showBoardPieces() {
    when(this.board){
        is BoardRun -> {
            val board = board
            var rowIdx = BOARD_DIM
            val lastCol = BOARD_DIM - 1
            val sepLine =  " +" + "-".repeat(BOARD_DIM* 2 -1) + "+ "
            println(sepLine + " Turn = ${board.playerTurn.symbol}")
            print("${rowIdx}|")

            Square.values.forEach { square ->
                val col = square.column
                val row = square.row
                val piece = board.moves[square]
                val player = if (square.black) {
                    piece.let {
                        if(it is Queen) it.player.symbol.uppercase()
                        else it?.player?.symbol?.lowercase()
                    } ?: "-"
                } else board.moves[square]?.player?.symbol ?: " "

                if (col.index == lastCol && !square.black) {
                    print(" |\n")
                    if (row.index == BOARD_DIM - 1) print(sepLine + "\n")
                    else print("${--rowIdx}|")
                } else {
                    (square.black).let {
                        if (col.symbol == 'a' && it) print("$player ")
                        else {
                            if (col.index == lastCol) {
                                print(" $player|\n")
                                print("${--rowIdx}|")
                            } else {
                                if (it) print(" $player ")
                                else print(player)
                            }
                        }
                    }
                }
            }
            val colIdx = "  a b c d e f g h "
            val printaveis = colIdx.trim().split(" ").take(BOARD_DIM).joinToString(" ")
            println("  $printaveis")
        }
        is BoardFinish -> {
            println("GAME OVER, ${(this.board as BoardFinish).winner.symbol} WINS")
        }
    }
}