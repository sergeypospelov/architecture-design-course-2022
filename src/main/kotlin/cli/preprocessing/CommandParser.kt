package cli.preprocessing

interface CommandParser {
    /** parses [string] to [ParserResult] */
    fun parse(string: String): ParserResult
}

/**
 * Implementation for [CommandParser]
 */
class CommandParserImpl(
    private val variables: Map<String, String> = emptyMap()
) : CommandParser {

    private enum class ParseState {
        NoQuotes,
        SingleQuote,
        DoubleQuote
    }


    private fun String.canAppend(ch: Char) = (ch.isLetter() or (ch == '_') or (ch.isDigit() && isNotEmpty()))

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

        val leftSubstring = string.substring(0, firstEqIdx)
        var left = ""
        for (c in leftSubstring) {
            if (left.canAppend(c)) {
                left += c
            } else {
                return ParseError("assignment has inappropriate left part")
            }
        }

        val rightSubstring = string.substring(firstEqIdx + 1, string.length)
        val right = makeSubstitutions(rightSubstring)

        return VariableAssignment(left, right)
    }

    private fun makeSubstitutions(string: String): String {
        val result = StringBuilder()

        var openC: Char? = null

        var isVariable = false
        var variable = ""

        fun substitute() {
            if (variable.isNotEmpty()) {
                val value = variables.getOrDefault(variable, "")
                result.append(value)
                isVariable = false
                variable = ""
            }
        }

        for (c in string) {
            if (isVariable && variable.canAppend(c)) {
                variable += c
                continue
            }
            substitute()
            if (openC != null) {
                if (c == '$' && openC == '\"') {
                    isVariable = true
                } else if (c == openC) {
                    openC = null
                    result.append(c)
                } else {
                    result.append(c)
                }
            } else {
                if (c == '$') {
                    isVariable = true
                } else if (c == '\'' || c == '\"') {
                    openC = c
                    result.append(c)
                 } else {
                    result.append(c)
                }
            }
        }
        substitute() // last variable


        return result.toString()
    }

    override fun parse(string: String): ParserResult {
        val variableAssignment = parseVariableAssignment(string)
        if (variableAssignment != null) {
            return variableAssignment
        }

        val stringWithSubstitutions = makeSubstitutions(string)

        return parseImpl(stringWithSubstitutions)
    }

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
