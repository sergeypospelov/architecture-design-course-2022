package cli

import cli.command.CatCommand
import cli.command.EchoCommand
import cli.command.ExitCommand
import cli.command.PwdCommand
import cli.command.UnknownCommand
import cli.command.WcCommand
import cli.command.commandNotFound
import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsolePrinter
import cli.io.ConsoleReader
import cli.io.ResultPrinter
import cli.preprocessing.PipelineBuilder
import cli.preprocessing.PipelineBuilderImpl
import cli.preprocessing.CommandBuilder
import cli.preprocessing.CommandParser
import cli.preprocessing.CommandParserImpl
import cli.preprocessing.CommandTemplate
import cli.preprocessing.MultiCommandBuilder
import cli.preprocessing.ParseError
import cli.preprocessing.Retry
import cli.preprocessing.VariableAssignment


fun main() {
    val commandReader: CommandReader = ConsoleReader()

    val commandParser: CommandParser = CommandParserImpl()

    val commandBuilder: CommandBuilder = MultiCommandBuilder().apply {
        addCommandBuilder(CatCommand.Builder)
        addCommandBuilder(EchoCommand.Builder)
        addCommandBuilder(ExitCommand.Builder)
        addCommandBuilder(PwdCommand.Builder)
        addCommandBuilder(WcCommand.Builder)
        addCommandBuilder(UnknownCommand.Builder)
    }

    val pipelineBuilder: PipelineBuilder = PipelineBuilderImpl(commandBuilder)

    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`)

    val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            Retry -> continue
            is CommandTemplate -> {
                val command = pipelineBuilder.build(parserResult)

                if (command == null) {
                    resultPrinter.printResult(commandNotFound(parserResult.name))
                    // maybe change exit code?
                    continue
                }

                val (result, _) = commandExecutor.execute(command)
                resultPrinter.printResult(result)
            }
            is VariableAssignment -> TODO() // maybe change exit code?
            is ParseError -> {
                resultPrinter.printResult(parserResult.errorDescription)
                // maybe change exit code?
            }
        }
    }
}