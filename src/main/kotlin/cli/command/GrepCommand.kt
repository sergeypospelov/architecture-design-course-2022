package cli.command

import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import kotlin.math.min

private const val GREP_COMMAND_NAME = "grep"

internal const val GREP_RESULT_SEPARATOR = "\n--\n"

/**
 * Implementation for [Command] interface for bash command grep:
 * grep is used to search text and strings in a given file
 */
class GrepCommand(override val arguments: List<String>) : Command {

    override val name = GREP_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        val grepArguments = parseArguments(errorStream)
            ?: return 1

        checkExistsAndNotDirectory(grepArguments.fileName, errorStream) { path ->
            val fileContent = path.toFile().readText()
            val fileContentLines = fileContent.split(System.lineSeparator())
            val queryRegex = constructQueryRegex(grepArguments)
            val matchRanges = queryRegex.findAll(fileContent).map { it.range }.toList()
            getLineNumbersByRanges(matchRanges, fileContentLines).forEachIndexed { index, matchLineNumber ->
                if (index != 0) {
                    outputStream.printAndFlush(GREP_RESULT_SEPARATOR)
                }
                for (lineNumber in matchLineNumber..min(matchLineNumber + grepArguments.extraLinesToPrint, fileContentLines.size - 1)) {
                    if (lineNumber != matchLineNumber) {
                        outputStream.printAndFlush("\n")
                    }
                    outputStream.printAndFlush(fileContentLines[lineNumber])
                }
            }
            0
        }

        return 0
    }

    // internal

    private fun constructQueryRegex(grepArguments: GrepArguments): Regex {
        val regexOptions = mutableListOf(RegexOption.MULTILINE)
        if (grepArguments.isCaseInsensitive)
            regexOptions.add(RegexOption.IGNORE_CASE)

        val query = if (grepArguments.isWholeWords) {
            "\\b${grepArguments.query}\\b"
        } else {
            grepArguments.query
        }

        return Regex(query, regexOptions.toSet())
    }

    private fun getLineNumbersByRanges(ranges: List<IntRange>, lines: List<String>): List<Int> {
        val sortedEnds = ranges.map { it.last }.sorted()
        val resultLineNumbers = mutableListOf<Int>()

        var currentIndex = 0
        var currentEndsIndex = 0
        for ((lineIndex, line) in lines.withIndex()) {
            currentIndex += line.length + System.lineSeparator().length
            while (currentEndsIndex < sortedEnds.size && sortedEnds[currentEndsIndex] < currentIndex) {
                resultLineNumbers.add(lineIndex)
                currentEndsIndex += 1
            }
        }

        return resultLineNumbers
    }

    // Argument parsing

    inner class GrepArguments(parser: ArgParser) {

        val isWholeWords: Boolean by parser.flagging(
            "-w", "--is_whole_words",
            help = "Find only the whole words on not"
        )

        val isCaseInsensitive: Boolean by parser.flagging(
            "-i", "--is_case_insensitive",
            help = "Case insensitive search or not"
        )

        val extraLinesToPrint: Int by parser.storing(
            "-A", "--extra_lines",
            help = "How many lines need to be printed"
        ) { toInt() }.default(0)

        val query: String by parser.positional(
            help = "Query as RegEx"
        )

        val fileName: String by parser.positional(
            help = "Destination filename"
        )
    }

    private fun parseArguments(errorStream: OutputStream): GrepArguments? =
        try {
            ArgParser(arguments.toTypedArray()).parseInto(::GrepArguments)
        } catch (e: Throwable) {
            errorStream.printAndFlush(e.message ?: "An error has occurred while parsing arguments")
            null
        }
}