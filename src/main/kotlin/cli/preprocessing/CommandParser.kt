package cli.preprocessing

interface CommandParser {
    fun parse(string: String): ParserResult
}

class CommandParserImpl : CommandParser {

    private enum class ParseState {
        NoQuotes,
        SingleQuote,
        DoubleQuote
    }

    override fun parse(string: String): ParserResult {
        var state = ParseState.NoQuotes
        val tokens = mutableListOf<String>()
        var token = ""

        fun addToken() {
            if (token.isNotEmpty()) {
                tokens += token
                token = ""
            }
        }

        for (c in string) {
            when (state) {
                ParseState.NoQuotes -> when (c) {
                    '\'' -> state = ParseState.SingleQuote
                    '\"' -> state = ParseState.DoubleQuote
                    ' ', '\t', '\n'  -> addToken()
                    else -> token += c
                }
                ParseState.SingleQuote -> when (c) {
                    '\'' -> state = ParseState.NoQuotes
                    else -> token += c
                }
                ParseState.DoubleQuote -> when (c) {
                    '\"' -> state = ParseState.NoQuotes
                    else -> token += c
                }
            }
        }
        if (state != ParseState.NoQuotes) {
            return ParseError("Unmatched quote")
        }
        addToken() // last token may not be added

        val name = tokens.firstOrNull() ?: return Retry
        val arguments = tokens.drop(1)
        return CommandTemplate(name, arguments)
    }
}
