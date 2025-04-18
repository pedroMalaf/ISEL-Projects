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
    val playerID: Player = Player.WHITE,
    var board: Board = initBoard(),
    // val score: Score = (Player.entries+null).associateWith { 0 }
)

/**
 * Creates a new board for the game
 * @return the game with the initial board created
 */
fun Game.createBoard(): Game {
    return Game(
        id = id,
        playerID = playerID,
        board = initBoard()
    )
}

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
        // score =  score
    )
}


object GameSerializer: Serializer<Game> {
    /**
     * Serializes the game data to a string
     * @param data the game to serialize
     * @return the serialized game string
     */
    override fun serialize(data: Game): String = buildString {
        /*  val score = data.score.map{
            "${it.key}:${it.value}"
        }.joinToString("")
        */
        val board = data.board?.let {
            appendLine(BoardSerializer.serialize(it))
        } ?: " "
        return "${data.id} # ${data.playerID.symbol} # $board"
    }

    /**
     * Deserializes the game data from a string
     * @param text the string to deserialize
     * @return the deserialized game
     */
    override fun deserialize(text: String): Game {
        val (gameid, player, board) = text.split(" # ")

        val firstPlayer = playerCreate(player)
        /*val scoreMap = score.split(" ").associate {
            val (play, count) = it.split(":")
            Player.entries.firstOrNull {
                it.name == play
            } to count.toInt()
        }*/
        val boardData = if (board.isNotEmpty()) BoardSerializer.deserialize(board) else null
        checkNotNull(boardData) { "No board to work with" }

        return Game(id = gameid, playerID = firstPlayer, board = boardData)
    }
}

/*
private fun Score.advance(player: Player?) =
    this - player + (player to checkNotNull(this[player]) +1)
*/
