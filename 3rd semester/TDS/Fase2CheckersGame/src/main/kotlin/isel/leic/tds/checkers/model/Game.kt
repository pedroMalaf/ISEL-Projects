package isel.leic.tds.checkers.model

import isel.leic.tds.storage.Serializer

// Typealias that represents the score of the game
typealias Score = Map<Player?, Int>

/**
 * Represents a game of Checkers
 * @property id the id of the game
 * @property playerID the player that has the turn
 * @property board the board of the game
 * @property score the score of the game
 */
data class Game(
    val id: String? = null,
    var board: Board = initBoard(),
)

/**
 * Creates a new board for the game
 * @return the game with the initial board created
 */
fun Game.createBoard() = Game(
        id = id,
    )

/**
 * Makes the move from one square to another
 * @param from the square to move from
 * @param to the square to move to
 * @return the game after the move is made
 */
fun Game.play(from: Square, to: Square): Game {
    val board = board.play(from, to)
    return copy(
        board = board,
    )
}


object GameSerializer: Serializer<Game> {
    /**
     * Serializes the game data to a string
     * @param data the game to serialize
     * @return the serialized game string
     */
    override fun serialize(data: Game): String = buildString {
        val board = data.board?.let {
            appendLine(BoardSerializer.serialize(it))
        } ?: " "
        return "${data.id} # $board"
    }

    /**
     * Deserializes the game data from a string
     * @param text the string to deserialize
     * @return the deserialized game
     */
    override fun deserialize(text: String): Game {
        val (gameid, board) = text.split(" # ")

        val boardData = if (board.isNotEmpty()) BoardSerializer.deserialize(board) else null
        checkNotNull(boardData) { "No board to work with" }

        return Game(id = gameid, board = boardData)
    }
}
