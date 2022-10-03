package cli.preprocessing

import cli.command.Command

/**
 * interface for entities that could be got from [CommandParser]
 */
sealed interface ParserResult

/**
 * assignment of a new variable
 * TODO(Phase 2: add variable assignment)
 */
data class VariableAssignment(
    val name: String,
    val value: String
) : ParserResult

/**
 * bare command that can be converted to [Command] with [CommandBuilder]
 */
data class CommandTemplate(
    val name: String,
    val arguments: List<String>
) : ParserResult

/**
 * user needs to retry his input
 */
object Retry : ParserResult


/**
 * error while parsing user's input
 */
data class ParseError(
    val errorDescription: String
) : ParserResult