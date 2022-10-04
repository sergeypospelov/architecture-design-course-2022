package cli.command

import org.apache.commons.lang3.SystemUtils
import java.io.InputStream
import java.io.OutputStream

/**
 * Implementation of [Command] interface for command unknown for the interpreter
 * In this case external program will be called
 */
class UnknownCommand(
    override val name: String,
    override val arguments: List<String>
) : Command {

    /**
     * execute command with [name] and [arguments] as external command
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        val process = ProcessBuilder()
            .command(*consoleRunnerPrefix.toTypedArray(), name, *arguments.toTypedArray())
            .start()
        val exitCode = process.waitFor()
        outputStream.write(process.inputStream.readAllBytes())
        return exitCode
    }

    private val consoleRunnerPrefix by lazy {
        when {
            SystemUtils.IS_OS_WINDOWS -> listOf("cmd.exe", "/c")
            SystemUtils.IS_OS_UNIX -> listOf("sh", "-c")
            else -> listOf("sh", "-c")
        }
    }
}