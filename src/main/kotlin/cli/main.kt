package cli

import cli.context.SessionContext
import cli.execution.CommandExecutor
import cli.execution.CommandExecutorImpl
import cli.io.CommandReader
import cli.io.ConsoleReader
import cli.preprocessing.*

fun main() {
    val commandReader: CommandReader = ConsoleReader()
    val substitutor: Substitutor = SubstitutorImpl(SessionContext.variables)
    val commandParser: CommandParser = CommandParserImpl()
    val commandBuilder: CommandBuilder = CommandBuilderImpl()
    val pipelineBuilder: PipelineBuilder = PipelineBuilderImpl(commandBuilder)
    val commandExecutor: CommandExecutor = CommandExecutorImpl(System.`in`, System.out, System.err)
    // val resultPrinter: ResultPrinter = ConsolePrinter()

    while (true) {
        print("$ ")
        val input = commandReader.readInput()
        val inputWithSubstitutions = substitutor.substitute(input)

        when (val parserResult = commandParser.parse(inputWithSubstitutions)) {
            Retry -> continue
            is CommandSequenceTemplate -> {
                val pipeline = pipelineBuilder.buildPipeline(parserResult)
                val exitCode = commandExecutor.execute(pipeline)
                SessionContext.variables.set("?", exitCode.toString())
            }
            is ParseError -> {
                System.err.println(parserResult.errorDescription)
            }
            is VariableAssignment -> {
                SessionContext.variables.set(parserResult.name, parserResult.value)
            }
        }
    }
}