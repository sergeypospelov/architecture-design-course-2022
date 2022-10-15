package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.readText

private const val WC_COMMAND_NAME = "wc"

/**
 * Implementation of [Command] interface for bash command wc:
 * wc - display the [Result] for file
 * @property arguments contains files to display the info above
 */
class WcCommand(override val arguments: List<String>) : Command {

    override val name: String = WC_COMMAND_NAME

    /**
     * if [arguments] are empty, print [Result] for [inputStream] to [outputStream]
     * otherwise print [Result] for files passed in [arguments] to [outputStream]
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int =
        if (arguments.isEmpty()) {
            executeEmptyArguments(inputStream, outputStream)
        } else {
            executeOnFiles(arguments, outputStream, errorStream)
        }

    private data class Result(
        val lines: Int,
        val words: Int,
        val bytes: Int,
    )

    private val results = mutableListOf<Result>()

    private fun computeResult(text: String): Result {
        val lines = text.count { it == '\n' }
        val words = text.split("\\s+".toRegex()).filterNot { it.isBlank() }.size
        val bytes = text.replace("\r\n", "\n").length
        return Result(lines, words, bytes)
    }

    private fun executeEmptyArguments(inputStream: InputStream, outputStream: OutputStream): Int {
        val (lines, words, bytes) = computeResult(inputStream.reader().readText())
        outputStream.printAndFlush("%7d %7d %7d ".format(lines, words, bytes))
        return 0
    }

    private fun executeOnFiles(fileNames: List<String>, outputStream: OutputStream, errorStream: OutputStream): Int {
        if (fileNames.size == 1) {
            return executeOnFile(fileNames[0], outputStream, errorStream)
        }
        val exitCodes = fileNames.map { fileName ->
            val exitCode = executeOnFile(fileName, outputStream, errorStream)
            if (exitCode == 0)
                outputStream.printAndFlush("\n")
            exitCode
        }
        val (lines, words, bytes) = aggregateResults()
        outputStream.printAndFlush("%7d %7d %7d %s".format(lines, words, bytes, "total"))
        return exitCodes.firstOrNull { it != 0 } ?: 0
    }

    private fun executeOnFile(fileName: String, outputStream: OutputStream, errorStream: OutputStream): Int =
        checkExistsAndNotDirectory(fileName, errorStream) { file ->
            val text = file.readText()
            val result = computeResult(text)
            results += result
            val (lines, words, bytes) = result
            outputStream.printAndFlush("%7d %7d %7d %s".format(lines, words, bytes, fileName))
            0
        }

    private fun aggregateResults(): Result {
        var (totalLines, totalWords, totalBytes) = Result(0, 0, 0)
        for ((lines, words, bytes) in results) {
            totalLines += lines
            totalWords += words
            totalBytes += bytes
        }
        return Result(totalLines, totalWords, totalBytes)
    }
}