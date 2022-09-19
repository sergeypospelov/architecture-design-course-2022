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

    val commandParser: CommandParser = CommandParserImpl()

    val commandBuilder: CommandBuilder = MultiCommandBuilder().apply {
        addCommandBuilder(CatCommand.Builder)
        addCommandBuilder(EchoCommand.Builder)
        addCommandBuilder(ExitCommand.Builder)
        addCommandBuilder(PwdCommand.Builder)
        addCommandBuilder(WcCommand.Builder)
        addCommandBuilder(UnknownCommand.Builder)
    }

//    val pipelineBuilder: PipelineBuilder = PipelineBuilderImpl(commandBuilder)

    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`)

    val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        print("$ ")
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            Retry -> continue
            is CommandTemplate -> {
//                val command = pipelineBuilder.build(parserResult)
                val command = commandBuilder.tryBuildCommand(parserResult)

                if (command == null) {
                    resultPrinter.printResult(commandNotFound(parserResult.name))
                    // maybe change exit code?
                    continue
                }

                val (result, _) = commandExecutor.execute(command)
                resultPrinter.printResult(result)
            }
            is ParseError -> {
                resultPrinter.printResult(parserResult.errorDescription)
                // maybe change exit code?
            }
            is VariableAssignment -> TODO() // maybe change exit code?
        }
    }
}