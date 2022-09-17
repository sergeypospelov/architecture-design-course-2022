package cli.preprocessing

interface CommandParser {
    fun parse(string: String): ParserResult
}

class CommandParserImpl : CommandParser {
    override fun parse(string: String): ParserResult {
        TODO("Not yet implemented")
    }
}