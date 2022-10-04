package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream

private const val ECHO_COMMAND_NAME = "echo"

/**
 * Implementation of [Command] interface for bash command echo:
 * echo - display your argument (or arguments)
 * @property arguments contains arguments to display
 */
class EchoCommand(override val arguments: List<String>) : Command {

    override val name: String = ECHO_COMMAND_NAME

    /**
     * displays [arguments] to [outputStream]
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        for (arg in arguments) {
            outputStream.printAndFlush(arg)
        }
        return 0
    }
}