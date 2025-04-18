package isel.leic.tds.checkers.ui

data class LineCommand (val name: String, val args: List<String>)

tailrec fun readCommand(): LineCommand {
    print('>')

    // List with all the args in the console
    val command = readln().trim().lowercase().split(' ')
    return if (command.isNotEmpty() && command.first().isNotBlank() )
        LineCommand(command[0], command.drop(1))
    else readCommand()
}