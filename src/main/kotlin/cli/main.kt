package cli

import cli.context.SessionContext
import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsolePrinter
import cli.io.ConsoleReader
import cli.io.ResultPrinter
import cli.preprocessing.*

fun main() {
    val commandReader: CommandReader = ConsoleReader()
    val commandParser: CommandParser = CommandParserImpl(SessionContext.variables)
    val commandBuilder: CommandBuilder = CommandBuilderImpl()
    val pipelineBuilder: PipelineBuilder = PipelineBuilderImpl(commandBuilder)
    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`, System.out, System.err)
    val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        print("$ ")
        val input = commandReader.readInput()

        when (val parserResult = commandParser.parse(input)) {
            Retry -> continue
            is CommandSequenceTemplate -> {
                val pipeline = pipelineBuilder.buildPipeline(parserResult)
                commandExecutor.execute(pipeline)
            }
            is ParseError -> {
                resultPrinter.printResult(parserResult.errorDescription)
            }
            is VariableAssignment -> {
                SessionContext.variables[parserResult.name] = parserResult.value
            }
        }
    }
}