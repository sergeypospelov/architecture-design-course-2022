package cli.command

import java.io.InputStream
import java.io.OutputStream
import kotlin.system.exitProcess

private const val EXIT_COMMAND_NAME = "exit"

/**
 * Implementation of [Command] interface for bash command exit:
 * exit - exit the interpreter
 */
class ExitCommand : Command {

    override val name: String = EXIT_COMMAND_NAME

    /** exit does not take any arguments **/
    override val arguments: List<String> = listOf()

    /**
     * exit the interpreter
     * never @return
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        exitProcess(0)
    }
}