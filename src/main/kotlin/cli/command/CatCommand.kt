package cli.command

import cli.io.printAndFlush
import cli.preprocessing.CommandBuilder
import cli.preprocessing.defaultCommandBuilder
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.readText

private const val CAT_COMMAND_NAME = "cat"

class CatCommand(
    override val arguments: List<String>
) : Command {
    override val name: String = CAT_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int =
        if (arguments.isEmpty()) {
            executeEmptyArguments(inputStream, outputStream)
        } else {
            executeOnFile(arguments[0], outputStream)
        }

    private fun executeEmptyArguments(inputStream: InputStream, outputStream: OutputStream): Int {
        inputStream.reader().forEachLine { inputString ->
            outputStream.printAndFlush(inputString + "\n")
        }
        return 0
    }

    private fun executeOnFile(fileName: String, outputStream: OutputStream): Int =
        checkExistsAndNotDirectory(fileName, outputStream) { file ->
            val text = file.readText()
            outputStream.printAndFlush(text)
            0
        }

    object Builder : CommandBuilder by defaultCommandBuilder(CAT_COMMAND_NAME, ::CatCommand)
}