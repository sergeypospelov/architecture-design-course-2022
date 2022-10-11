package cli.preprocessing

import cli.command.Command

/**
 * Represents a pipe of [commands].
 *
 * In the future, we can store some extra information along the [Command]s, such as redirected input.
 */
data class Pipeline(
    val commands: List<Command>
)