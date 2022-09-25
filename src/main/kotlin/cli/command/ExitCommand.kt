package cli.command

import java.io.InputStream
import java.io.OutputStream
import kotlin.system.exitProcess

private const val EXIT_COMMAND_NAME = "exit"

class ExitCommand : Command {

    override val name: String = EXIT_COMMAND_NAME

    override val arguments: List<String> = listOf()

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        exitProcess(0)
    }
}