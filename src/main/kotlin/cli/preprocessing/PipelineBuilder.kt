package cli.preprocessing

interface PipelineBuilder {
    /**
     * builds [Pipeline] from [CommandSequenceTemplate]
     */
    fun buildPipeline(commandSequenceTemplate: CommandSequenceTemplate): Pipeline
}

/**
 * [PipelineBuilder] implementation
 */
class PipelineBuilderImpl(
    private val commandBuilder: CommandBuilder
) : PipelineBuilder {
    override fun buildPipeline(commandSequenceTemplate: CommandSequenceTemplate): Pipeline {
        // maybe some extra logic will be here in the future
        val commands = commandSequenceTemplate.commands.map(commandBuilder::buildCommand)
        return Pipeline(commands)
    }

}
