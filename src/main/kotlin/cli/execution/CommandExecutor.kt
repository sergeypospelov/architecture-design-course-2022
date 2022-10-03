package cli.execution

import cli.command.Command
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Executor for sequence of commands
 */
interface CommandExecutor {
    fun execute(command: Command): ExecutionResult
}

/**
 * @param [inputStream] used as an input stream for a single command.
 */
class CommandExecutorImpl(private val inputStream: InputStream) : CommandExecutor {

    /**
     * executes [command]
     * @return [ExecutionResult]
     * TODO("Phase 2: Add command sequence instead of one command at")
     */
    override fun execute(command: Command): ExecutionResult {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val exitCode = command.execute(inputStream, byteArrayOutputStream)
        val result = byteArrayOutputStream.toByteArray().decodeToString()
        return ExecutionResult(result, exitCode)
    }
}