package cli.io

interface CommandReader {
    fun readInput(): String
}

class ConsoleReader : CommandReader {
    override fun readInput(): String {
        TODO("Not yet implemented")
    }
}