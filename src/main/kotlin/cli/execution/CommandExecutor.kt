package cli.execution

import cli.command.Command

interface CommandExecutor {
    fun execute(command: Command): ExecutionResult
}

class CommandExecutorImpl : CommandExecutor {
    override fun execute(command: Command): ExecutionResult {
        TODO("Not yet implemented")
    }
}