package cli

import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsolePrinter
import cli.io.ConsoleReader
import cli.io.ResultPrinter
import cli.preprocessing.*

fun main() {
    val commandReader: CommandReader = ConsoleReader()
    val commandParser: CommandParser = CommandParserImpl()
    val commandBuilder: CommandBuilder = CommandBuilderImpl()
    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`)
    val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        print("$ ")
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            Retry -> continue
            is CommandTemplate -> {
                val command = commandBuilder.buildCommand(parserResult)
                val (result, _) = commandExecutor.execute(command)
                resultPrinter.printResult(result)
            }
            is ParseError -> {
                resultPrinter.printResult(parserResult.errorDescription)
            }
            is VariableAssignment -> TODO()
        }
    }
}