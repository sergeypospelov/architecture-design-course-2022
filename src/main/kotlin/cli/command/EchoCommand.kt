package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream

private const val ECHO_COMMAND_NAME = "echo"

class EchoCommand(override val arguments: List<String>) : Command {

    override val name: String = ECHO_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        for (arg in arguments) {
            outputStream.printAndFlush(arg)
        }
        return 0
    }
}