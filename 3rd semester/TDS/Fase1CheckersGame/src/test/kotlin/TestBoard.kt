import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.ui.showBoardPieces
import kotlin.test.*

class TestBoard {

    private fun playMoves(squares: List<Square>, game: Game): Game {
        var i = 0
        var g = game
        g.showBoardPieces()

        while (i in squares.indices) {
            if (i + 1 !in squares.indices) break
            assertNull(g.board.moves[squares[i + 1]]?.player)
            //assertNotNull(g.board.moves[squares[i]]?.player)
            g = g.play(squares[i], squares[i + 1])
            g.showBoardPieces()
            i += 2
        }
        return g
    }

    @Test
    fun `play to an empty square`() {
        val game = Game("gameTest1")
        game.showBoardPieces()
        val from = "3c".toSquare()
        val to = "4d".toSquare()
        assertNull(game.board.moves[to]?.player)
        assertNotNull(game.board.moves[from]?.player)
        val gameAfterPlay = game.play(from, to)
        gameAfterPlay.showBoardPieces()
        assertEquals(gameAfterPlay.board.moves[to]?.player, Player.WHITE)
    }

    @Test
    fun `play and capture a piece from the enemy`() {
        var game = Game("gameTest1")
        val squares = listOf("3c", "4d", "6f", "5e").map { it.toSquare() }
        game = playMoves(squares, game)

        val from = "4d".toSquare()
        val to = "6f".toSquare()

        assertNull(game.board.moves[to]?.player?.symbol)
        assertEquals(game.board.moves[from]?.player?.symbol, Player.WHITE.symbol)
        assertNull(game.board.moves[to]?.player)
        assertNotNull(game.board.moves[from]?.player)

        game = game.play(from, to)
        game.showBoardPieces()

        assertEquals(game.board.moves[to]?.player?.symbol, Player.WHITE.symbol)
        assertNull(game.board.moves["5e".toSquare()]?.player?.symbol)
    }

    @Test
    fun `play and promote a Pawn to Queen`() {
        val game = Game("gameTest1")
        game.showBoardPieces()
        val squares = listOf(
            "3c", "4d", "6f", "5e", "4d", "6f", "6h", "5g", "3g", "4h", "7g", "6h", "2h", "3g",
            "5g", "4f", "3e", "4d", "8h", "7g", "3a", "4b", "6h", "5g", "4b", "5c", "7g", "6h", "6f", "8h"
        ).map { it.toSquare() }

        val g = playMoves(squares, game)

        assertEquals(g.board.moves["8h".toSquare()]?.player?.symbol, Player.WHITE.symbol)
    }

    @Test
    fun `play, promote to Queen and capture`() {
        var game = Game("gameTest1")
        game.showBoardPieces()
        val squares = listOf(
            "3c", "4d", "6f", "5e", "4d", "6f", "6h", "5g", "3g", "4h", "7g", "6h", "2h", "3g",
            "5g", "4f", "3e", "4d", "8h", "7g", "3a", "4b", "6h", "5g", "4b", "5c", "7g", "6h", "6f", "7g",
            "6d", "5e", "7g", "8h", "8f", "7g",  "8h", "3c"
        ).map { it.toSquare() }

        game = playMoves(squares, game)

        assertNull(game.board.moves["7g".toSquare()])
        assertNull(game.board.moves["5e".toSquare()])
        assertEquals(game.board.moves["3c".toSquare()]?.player?.symbol, Player.WHITE.symbol)

        val squares1 =  listOf( "7e", "6f", "3c", "7g").map { it.toSquare() }
        game = playMoves(squares1, game)
        println(game.board.moves)

        assertNull(game.board.moves["7f".toSquare()])
        assertNull(game.board.moves["3c".toSquare()])
        assertEquals(game.board.moves["7g".toSquare()]?.player?.symbol, Player.WHITE.symbol)
    }
}