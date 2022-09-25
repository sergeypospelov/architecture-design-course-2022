package cli.command

import cli.preprocessing.CommandBuilder
import cli.preprocessing.CommandTemplate
import cli.preprocessing.defaultCommandBuilder
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

    object Builder : CommandBuilder {
        override fun tryBuildCommand(template: CommandTemplate): Command = ExitCommand()
    }
}