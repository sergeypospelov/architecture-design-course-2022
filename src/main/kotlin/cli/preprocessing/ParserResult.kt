package cli.preprocessing

sealed interface ParserResult

data class VariableAssignment(
    val name: String,
    val value: String
) : ParserResult

data class CommandTemplate(
    val name: String,
    val arguments: List<String>
) : ParserResult

object Retry : ParserResult

data class ParseError(
    val errorDescription: String
) : ParserResult