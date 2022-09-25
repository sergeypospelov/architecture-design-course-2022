package cli.command

import org.apache.commons.lang3.SystemUtils
import java.io.InputStream
import java.io.OutputStream

class UnknownCommand(
    override val name: String,
    override val arguments: List<String>
) : Command {

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
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