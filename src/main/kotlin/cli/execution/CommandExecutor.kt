package cli.execution

import cli.preprocessing.Pipeline
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Executor for sequence of commands
 */
interface CommandExecutor {
    fun execute(pipeline: Pipeline): Int
}

/**
 * @param [inputStream] used as an input stream for a single command.
 */
class CommandExecutorImpl(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val errorStream: OutputStream
) : CommandExecutor {

    /**
     * executes [pipeline]
     * @return exitCode of last instruction
     */
    override fun execute(pipeline: Pipeline): Int {
        var lastExitCode = 0

        var inputStream = inputStream
        for (command in pipeline.commands) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val exitCode = command.execute(inputStream, byteArrayOutputStream, errorStream)
            inputStream = byteArrayOutputStream.toByteArray().inputStream()
            lastExitCode = exitCode
        }
        inputStream.copyTo(outputStream)
        outputStream.write("\n".toByteArray())

        return lastExitCode
    }
}