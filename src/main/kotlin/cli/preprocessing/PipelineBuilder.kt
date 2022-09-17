package cli.pipeline

import cli.command.Command
import cli.preprocessing.CommandTemplate

interface PipelineBuilder {
    fun build(commandTemplate: CommandTemplate): Command
}

class PipelineBuilderImpl : PipelineBuilder {
    override fun build(commandTemplate: CommandTemplate): Command {
        TODO("Not yet implemented")
    }
}