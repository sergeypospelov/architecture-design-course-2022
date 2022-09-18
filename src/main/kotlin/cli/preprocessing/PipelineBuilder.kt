package cli.preprocessing

import cli.command.Command

interface PipelineBuilder {
    fun build(commandTemplate: CommandTemplate): Command?
}

class PipelineBuilderImpl(
    private val commandBuilder: CommandBuilder
): PipelineBuilder {
    override fun build(commandTemplate: CommandTemplate): Command? {
        return commandBuilder.tryBuildCommand(commandTemplate)
    }
}
