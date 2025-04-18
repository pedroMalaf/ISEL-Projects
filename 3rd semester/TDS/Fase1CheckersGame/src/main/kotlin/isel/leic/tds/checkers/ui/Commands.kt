package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*

class Command(
    val syntax: String = "",
    val isTerminate: Boolean = false,
    val execute: (args: List<String>, clash: Clash) -> Clash = { _, clash -> clash }
) {
    fun terminate() = Command(isTerminate = true)
}

private val playCommand = Command("from to") { args, clash ->
    require(args.size == 2) { "Invalid number of arguments" }
    require(args[0].toSquareOrNull() != null) { "Invalid square" }
    require(args[1].toSquareOrNull() != null) { "Invalid square" }
    val from = args[0].toSquare()
    val to = args[1].toSquare()
    clash.play(from, to)
}

private fun commandsWithNoArgs(clashFunction: Clash.() -> Clash) = Command{ _, clash ->
    clash.clashFunction()
}

private fun commandsWithName (clashFunction: Clash.(Name) -> Clash) = Command("<name>") {
    args, clash ->
    require(args.isNotEmpty()) { "Missing game Id" }
    val gameId = args[0]
    clash.start(Name(gameId))
    //clash.clashFunction(Name(gameId))
}

fun getCommand(): Map<String, Command> = mapOf(

    "start" to commandsWithName(Clash::start),

    "play" to playCommand,

    "grid" to commandsWithNoArgs(Clash::grid),

    "refresh" to commandsWithNoArgs(Clash::refresh),

    "exit" to Command(isTerminate = true)
)
