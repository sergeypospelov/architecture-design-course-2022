package cli.command

import cli.preprocessing.CommandBuilder
import cli.preprocessing.CommandTemplate
import java.io.InputStream
import java.io.OutputStream

class UnknownCommand(
    override val name: String,
    override val arguments: List<String>
) : Command {

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        val process = ProcessBuilder(name, *arguments.toTypedArray()).start()

        TODO()
    }

    object Builder : CommandBuilder {
        override fun tryBuildCommand(template: CommandTemplate): Command =
            UnknownCommand(template.name, template.arguments)
    }
}