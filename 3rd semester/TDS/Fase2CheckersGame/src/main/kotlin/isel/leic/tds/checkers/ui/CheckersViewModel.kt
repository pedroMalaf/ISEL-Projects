package isel.leic.tds.checkers.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import isel.leic.tds.checkers.model.*
import isel.leic.tds.storage.MongoDriver
import isel.leic.tds.storage.MongoStorage
import isel.leic.tds.storage.TextFileStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.get

class CheckersViewModel(private val scope: CoroutineScope, driver: MongoDriver) {

    // Model State
    //private val storage = TextFileStorage<Name, Game>("games", GameSerializer)

    private val storage = MongoStorage<Name, Game>("games", driver, GameSerializer)


    var clash by mutableStateOf(Clash(storage))

    var autoRefreshEnabled by mutableStateOf(false)
        private set

    var showTargetsEnabled by mutableStateOf(false)
        private set

    private var autoRefreshJob: Job? = null

    val board get() = (clash as? ClashRun)?.game?.board
    val id get() = (clash as? ClashRun)?.game?.id
    val playerID get() = (clash as? ClashRun)?.playerID

    /**
     * Toggles the auto-refresh feature.
     */
    fun toggleAutoRefresh() {
        autoRefreshEnabled = !autoRefreshEnabled
        if (autoRefreshEnabled) {
            autoRefresh()
        } else {
            stopAutoRefresh()
        }
    }

    /**
     * Toggles the show targets feature.
     */
    fun toggleShowTargets(){
        showTargetsEnabled = !showTargetsEnabled
    }

    fun hasClash(): Boolean = clash is ClashRun

    fun updateClash(newClash: Clash) = { clash = newClash }

    /**
     * Starts the auto-refresh feature.
     * This feature will automatically refresh the board every 500ms.
     */
    fun autoRefresh() {
        autoRefreshJob = scope.launch {
            while (autoRefreshEnabled) {
                refreshBoard()
                delay(500)
            }
        }
    }

    /**
     * Stops the auto-refresh feature.
     */
    private fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    /**
     * Refreshes the board.
     */
    fun refreshBoard() = oper(Clash::refresh)

    var currentAction: Action? by mutableStateOf(null)
        private set

    var selectedSquares by mutableStateOf<List<Square>>(emptyList())
        private set

    var possibleMoves by mutableStateOf<List<Square>>(emptyList())
        private set

    /**
     * Handles the click on a square.
     * @param square The square that was clicked.
     */
    fun onClickSquare(square: Square) {
        val b = board as BoardRun
        if (playerID != b.playerTurn) {
            // It's not the player's turn, do nothing
            return
        }

        when {
            square in selectedSquares -> {
                selectedSquares = selectedSquares - square
                if (showTargetsEnabled) {
                    updatePossibleMoves()
                }
            }

            square in possibleMoves && selectedSquares.isNotEmpty() -> {
                play(selectedSquares.first(), square)
                clearSelection()
            }

            b.moves[square]?.player == playerID -> {
                selectedSquares = listOf(square)
                if (showTargetsEnabled) {
                    updatePossibleMoves()
                } else {
                    possibleMoves = emptyList()
                }
            }

            selectedSquares.isNotEmpty() -> {
                play(selectedSquares.first(), square)
                clearSelection()
            }

            else -> clearSelection()
        }
    }

    /**
     * Updates the possible moves for the selected square.
     * if the square clicked is not a piece, it does nothing
     */
    private fun updatePossibleMoves() {
        val b = board as BoardRun
        possibleMoves = if (selectedSquares.isNotEmpty() && board is BoardRun) {
            val capturesAvailable = b.checkCapturesAvailable()

            if (capturesAvailable.containsValue(true)) {
                val selectedSquare = selectedSquares.first()
                if (capturesAvailable[selectedSquare] == true) {
                    val piece = b.moves[selectedSquare]
                    when (piece) {
                        is Pawn -> b.possibleMovesForPawn(selectedSquare)
                        is Queen -> b.possibleMovesForQueen(selectedSquare)
                        else -> emptyList()
                    }
                } else {
                    emptyList()
                }
            } else {
                val selectedSquare = selectedSquares.first()
                val piece = b.moves[selectedSquare]
                when (piece) {
                    is Pawn -> b.possibleMovesForPawn(selectedSquare)
                    is Queen -> b.possibleMovesForQueen(selectedSquare)
                    else -> emptyList()
                }
            }
        } else emptyList()
    }


    /**
     * Clears the selection.
     */
    private fun clearSelection() {
        selectedSquares = emptyList()
        possibleMoves = emptyList()
    }

    /**
     * Plays a move from one square to another.
     * @param from The square to move from.
     * @param to The square to move to.
     */
    fun play(from: Square, to: Square) {
        if (board is BoardRun) {
            oper { play(from, to) }
            clearSelection()
        }
    }

    /**
     * Starts a new clash.
     */
    fun startClash() {
        currentAction = Action.START
    }

    /**
     * Allows you to cancel a clash before it's started.
     */
    fun cancelClash() {
        currentAction = null
    }

    /**
     * Performs the clash with the given name.
     */
    fun performClash(name: Name) {
        oper {
            when (currentAction as Action) {
                Action.START -> clash.start(name)
                Action.REFRESH -> clash.refresh()
            }
        }
        currentAction = null
    }

    var message: String? by mutableStateOf(null)
        private set


    fun exit(){
        TODO()
    }

    /**
     * Performs an operation on the clash.
     * @param fx The function to perform on the clash.
     */
    private fun oper(fx: Clash.() -> Clash) {
        try {
            clash = clash.fx() // function newBoard (Kotlin reflection is not available)
        } catch (ex: Exception) {
            if (ex is IllegalStateException || ex is IllegalArgumentException)
                message = ex.message
            else throw ex
        }
    }
}