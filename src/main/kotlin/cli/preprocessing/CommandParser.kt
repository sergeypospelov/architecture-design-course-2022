package cli.preprocessing

interface CommandParser {
    /** parses [string] to [ParserResult] */
    fun parse(string: String): ParserResult
}

/**
 * Implementation for [CommandParser]
 */
class CommandParserImpl : CommandParser {

    private enum class ParseState {
        NoQuotes,
        SingleQuote,
        DoubleQuote
    }

    private fun String.canAppend(ch: Char) =
        (ch.isLetter() or (ch == '_') or (ch.isDigit() && isNotEmpty()) or (ch == '?'))

    private fun parseVariableAssignment(string: String): ParserResult? {
        var firstEqIdx = -1
        var openC: Char? = null

        for ((i, c) in string.withIndex()) {
            if (openC != null) {
                if (c == openC) {
                    openC = null
                }
            } else {
                if (c == '=') {
                    firstEqIdx = i
                    break
                } else if (c == '\'' || c == '\"') {
                    openC = c
                }
            }
        }

        if (firstEqIdx == -1) {
            return null
        }

        val leftSubstring = string.substring(0, firstEqIdx).trim()
        var left = ""
        for (c in leftSubstring) {
            if (left.canAppend(c)) {
                left += c
            } else {
                return ParseError("assignment has inappropriate left part")
            }
        }

        val right = string.substring(firstEqIdx + 1, string.length)

        return VariableAssignment(left, right)
    }

    override fun parse(string: String): ParserResult =
        parseVariableAssignment(string) ?: parseImpl(string)

    private fun parseImpl(string: String): ParserResult {
        val commands = mutableListOf<CommandTemplate>()

        val tokens = mutableListOf<String>()
        var token = ""

        fun addToken() {
            if (token.isNotEmpty()) {
                tokens += token
                token = ""
            }
        }

        fun addCommand(): Boolean {
            addToken() // last token may not be added
            val name = tokens.firstOrNull() ?: return false
            val arguments = tokens.drop(1)
            commands += CommandTemplate(name, arguments)
            tokens.clear()
            return true
        }

        var state = ParseState.NoQuotes
        for (c in string) {
            when (state) {
                ParseState.NoQuotes -> when (c) {
                    '\'' -> state = ParseState.SingleQuote
                    '\"' -> state = ParseState.DoubleQuote
                    ' ', '\t', '\n' -> addToken()
                    '|' -> if (!addCommand()) {
                        return ParseError("syntax error near unexpected token `|'")
                    }
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
        if (commands.isEmpty() && tokens.isEmpty()) {
            return Retry
        }

        if (!addCommand()) {
            return ParseError("syntax error")
        }

        return CommandSequenceTemplate(commands)
    }
}
