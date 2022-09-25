package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.readText

private const val WC_COMMAND_NAME = "wc"

class WcCommand(override val arguments: List<String>) : Command {

    override val name: String = WC_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int =
        if (arguments.isEmpty()) {
            executeEmptyArguments(inputStream, outputStream)
        } else {
            executeOnFile(arguments[0], outputStream)
        }

    private data class Result(
        val lines: Int,
        val words: Int,
        val bytes: Int,
    )

    private fun computeResult(text: String): Result {
        val lines = text.count { it == '\n' }
        val words = text.trim().split("\\s+".toRegex()).size
        val bytes = text.length
        return Result(lines, words, bytes)
    }

    private fun executeEmptyArguments(inputStream: InputStream, outputStream: OutputStream): Int {
        val (lines, words, bytes) = computeResult(inputStream.reader().readText())
        outputStream.printAndFlush("%7d %7d %7d ".format(lines, words, bytes))
        return 0
    }

    private fun executeOnFile(fileName: String, outputStream: OutputStream): Int =
        checkExistsAndNotDirectory(fileName, outputStream) { file ->
            val text = file.readText()
            val (lines, words, bytes) = computeResult(text)
            outputStream.printAndFlush("%7d %7d %7d %s".format(lines, words, bytes, fileName))
            0
        }
}