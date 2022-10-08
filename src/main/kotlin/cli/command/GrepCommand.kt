package cli.command

import java.io.InputStream
import java.io.OutputStream
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

private const val GREP_COMMAND_NAME = "grep"

class GrepCommand(override val arguments: List<String>) : Command {

    override val name = GREP_COMMAND_NAME

    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        println("grepArguments = ${grepArguments.isWholeWords}")
        println("grepArguments = ${grepArguments.isCaseInsensitive}")
        println("grepArguments = ${grepArguments.linesCountToPrint}")
        return 0
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

        val linesCountToPrint: Int by parser.storing(
            "-A", "--line_count",
            help = "How many lines need to be printed"
        ) { toInt() }.default(1)
    }

    private val grepArguments = ArgParser(arguments.toTypedArray()).parseInto(::GrepArguments)
}