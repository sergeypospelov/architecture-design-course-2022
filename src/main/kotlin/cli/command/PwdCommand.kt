package cli.command

import cli.context.SessionContext
import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream

private const val PWD_COMMAND_NAME = "pwd"

/**
 * Implementation of [Command] interface for bash command pwd:
 * pwd - display current directory
 */
class PwdCommand : Command {

    override val name = PWD_COMMAND_NAME

    /** pwd does not take any arguments **/
    override val arguments: List<String> = listOf()

    /**
     * displays current directory to [outputStream]
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        outputStream.printAndFlush(SessionContext.currentDirectory.toAbsolutePath().toString())
        return 0
    }
}