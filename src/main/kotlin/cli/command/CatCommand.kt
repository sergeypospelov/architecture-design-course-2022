package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.readText

private const val CAT_COMMAND_NAME = "cat"

/**
 * Implementation of [Command] interface for bash command cat:
 * cat - display the contents of a file
 * @property arguments contains files to display
 */
class CatCommand(override val arguments: List<String>) : Command {

    override val name: String = CAT_COMMAND_NAME

    /**
     * if [arguments] are empty, prints [inputStream] to [outputStream]
     * otherwise prints the contents of files passed in [arguments] to [outputStream]
     *
     * @param [errorStream] default error stream
     *
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int =
        if (arguments.isEmpty()) {
            executeEmptyArguments(inputStream, outputStream)
        } else {
            val exitCodes = arguments.map { fileName ->
                executeOnFile(fileName, outputStream, errorStream)
            }
            exitCodes.firstOrNull { it != 0 } ?: 0
        }

    private fun executeEmptyArguments(inputStream: InputStream, outputStream: OutputStream): Int {
        inputStream.reader().forEachLine { inputString ->
            outputStream.printAndFlush(inputString)
        }
        return 0
    }

    private fun executeOnFile(fileName: String, outputStream: OutputStream, errorStream: OutputStream): Int =
        checkExistsAndNotDirectory(fileName, errorStream) { file ->
            val text = file.readText()
            outputStream.printAndFlush(text)
            0
        }
}