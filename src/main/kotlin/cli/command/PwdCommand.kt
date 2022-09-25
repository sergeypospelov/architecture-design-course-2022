package cli.command

import cli.context.SessionContext
import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream

private const val PWD_COMMAND_NAME = "pwd"

class PwdCommand(override val arguments: List<String>) : Command {

    override val name = PWD_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        outputStream.printAndFlush(SessionContext.currentDirectory.toAbsolutePath().toString())
        return 0
    }
}