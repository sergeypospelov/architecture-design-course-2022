package cli

import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsolePrinter
import cli.io.ConsoleReader
import cli.io.ResultPrinter
import cli.pipeline.PipelineBuilder
import cli.pipeline.PipelineBuilderImpl
import cli.preprocessing.CommandParser
import cli.preprocessing.CommandParserImpl
import cli.preprocessing.CommandTemplate
import cli.preprocessing.VariableAssignment

fun main() {
    val commandReader: CommandReader = ConsoleReader()
    val commandParser: CommandParser = CommandParserImpl()
    val pipelineBuilder: PipelineBuilder = PipelineBuilderImpl()
    val commandExecutor: CommandExecutor = CommandExecutorImpl()
    val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            is CommandTemplate -> {
                val command = pipelineBuilder.build(parserResult)
                val (result, _) = commandExecutor.execute(command)
                resultPrinter.printResult(result)
            }
            is VariableAssignment -> TODO()
        }
    }
}