package cli.execution

/**
 * result returned by [CommandExecutor.execute]
 * @property result - [CommandExecutor] output
 * @property exitCode - [CommandExecutor] exit code
 */
data class ExecutionResult(
    val result: String,
    val exitCode: Int
)