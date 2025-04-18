package isel.leic.tds.checkers.model

/**
 * Represents the player of the game
 * @property symbol the symbol of the player
 * @property other the other player
 */
enum class Player(val symbol: Char) {
    BLACK('b'),
    WHITE('w');
    val other get() = if (this == BLACK) WHITE else BLACK
}

/**
 * Creates a player based on the string
 * @param player the string to create the player
 * @return the player created
 */
fun playerCreate(player:String) = when(player) {
    "W" -> Player.WHITE
    "w" -> Player.WHITE
    "B" -> Player.BLACK
    "b" -> Player.BLACK
    else -> throw IllegalArgumentException("Invalid player")
}