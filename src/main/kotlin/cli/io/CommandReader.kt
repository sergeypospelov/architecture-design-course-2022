package cli.io

/**
 * [CommandReader] reads commands
 */
interface CommandReader {
    fun readInput(): String
}

/**
 * [ConsoleReader] reads commands from console
 */
class ConsoleReader : CommandReader {
    override fun readInput(): String = readln()
}