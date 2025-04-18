package isel.leic.tds.checkers.model


import isel.leic.tds.checkers.ui.showBoardPieces
import isel.leic.tds.storage.Storage

@JvmInline
value class Name(val value: String) {
    init {
        require(value.isNotEmpty() && value.all { it.isLetterOrDigit() }) { "Name not valid " }
    }

    override fun toString() = value
}

// Typealias that represents a game with a name
typealias GameStorage = Storage<Name, Game>

/**
 * Represents a clash of the game
 * @property storage the storage of the game
 */
open class Clash(val storage: GameStorage)

/**
 * Represents a clash of the game that is running
 * @property storage the storage of the game
 * @property name the name of the clash
 * @property game the game of the clash
 * @property sidePlayer the player that has the turn
 */
class ClashRun(
    storage: GameStorage,
    val name: Name,
    val game: Game,
    val sidePlayer: Player
) : Clash(storage)

/**
 * Function that shows the board of the game
 */
fun Clash.showBoard() {
    val clash = this as? ClashRun ?: return
    println("Clash: ${clash.name} you are ${clash.sidePlayer}")
    clash.game.showBoardPieces()
}

/**
 * Stats a new clash with the given name
 * If the name already exists, the player joins the clash
 * @param name the name of the clash
 * @return the new clash
 */
fun Clash.start(name: Name): Clash {
    if(storage.exist(name)) {
        return this.join(name) }
    val game = Game(id = name.toString()).createBoard()
    storage.create(name, game)
        return ClashRun(storage, name, game, Player.WHITE)
}

fun Clash.deleteIfIsOwner() {
    if (this is ClashRun && sidePlayer == Player.WHITE) storage.delete(name)
}

/**
 * Function that allows a player to join a clash
 * @param name the name of the clash to join
 * @return the clash joined
 */
fun Clash.join(name: Name): Clash {
    val game = storage.read(name) ?: error("Clash $name not found")
    deleteIfIsOwner()
    return ClashRun(
        storage, name, game, sidePlayer = Player.BLACK
    )
}

/**
 * Utility function to ensure the clash is a ClashRun and
 * @return a new ClashRun with the game returned by the function getGame.
 * @param getGame an extension function of ClashRun and returns a Game
 */
private fun Clash.onClashRun(getGame: ClashRun.() -> Game): Clash {
    check(this is ClashRun) { "There is no clash yet " }
    return ClashRun(storage, name, getGame(), sidePlayer)
}

/**
 * Refreshes the game, allowing the player to see the changes
 * @return the game after the refresh
 */
fun Clash.refresh() = onClashRun {
    val gameAfter = storage.read(name) as Game
    check(game.board != gameAfter.board) { "No changes" }
    gameAfter
}

/**
 *  Makes a play on the board
 * @param from the square the piece is at
 * @param to the square you want to move to
 * @return the game after the play
 */
fun Clash.play(from: Square, to: Square) = onClashRun {
    game.play(from, to)
        .also {
            check((game.board as? BoardRun)?.turn == sidePlayer) { "Not your turn to play" }
            storage.update(name, it)
        }
}

/**
 * Allows the player to see the board
 */
fun Clash.grid() = onClashRun {
    checkNotNull(storage.read(name))
}
