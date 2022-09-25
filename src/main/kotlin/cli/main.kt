package cli

import cli.command.*
import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsolePrinter
import cli.io.ConsoleReader
import cli.io.ResultPrinter
import cli.preprocessing.*

fun main() {
    val commandReader: CommandReader = ConsoleReader()
    val resultPrinter: ResultPrinter = ConsolePrinter()

    val commandParser: CommandParser = CommandParserImpl()
    val commandBuilder: CommandBuilder = MultiCommandBuilder().apply {
        addCommandBuilder(CatCommand.Builder)
        addCommandBuilder(EchoCommand.Builder)
        addCommandBuilder(ExitCommand.Builder)
        addCommandBuilder(PwdCommand.Builder)
        addCommandBuilder(WcCommand.Builder)
        addCommandBuilder(UnknownCommand.Builder)
    }
    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`)

    while (true) {
        print("$ ")
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            Retry -> continue
            is CommandTemplate -> {
                val command = commandBuilder.tryBuildCommand(parserResult)

                if (command == null) {
                    resultPrinter.printResult(commandNotFound(parserResult.name))
                    continue
                }

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