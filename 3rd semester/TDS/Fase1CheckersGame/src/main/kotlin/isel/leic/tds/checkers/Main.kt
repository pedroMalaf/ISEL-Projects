package isel.leic.tds.checkers

import isel.leic.tds.checkers.model.Clash
import isel.leic.tds.checkers.model.GameSerializer
import isel.leic.tds.checkers.model.showBoard
import isel.leic.tds.checkers.ui.Command
import isel.leic.tds.checkers.ui.getCommand
import isel.leic.tds.checkers.ui.readCommand
import isel.leic.tds.storage.TextFileStorage


fun main() {
    var clash = Clash(TextFileStorage("games", GameSerializer))
    val cmds: Map<String, Command> = getCommand()
    while (true) {
        val (name, args) = readCommand()
        val cmd = cmds[name]
        if (cmd == null) println("Invalid command $name")
        else try {
            clash = cmd.execute(args, clash)
            if (cmd.isTerminate) break
            clash.showBoard()
        } catch (e: IllegalArgumentException) {
            println(e.message)
        } catch (e: IllegalStateException){
            println(e.message)
        }
    }
}