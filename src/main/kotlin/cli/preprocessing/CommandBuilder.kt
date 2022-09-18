package cli.preprocessing

import cli.command.Command

interface CommandBuilder {
    fun tryBuildCommand(template: CommandTemplate): Command?
}

class MultiCommandBuilder : CommandBuilder {
    private val delegateBuilders = mutableListOf<CommandBuilder>()

    fun addCommandBuilder(builder: CommandBuilder) {
        delegateBuilders.add(builder)
    }

    override fun tryBuildCommand(template: CommandTemplate): Command? {
        for (builder in delegateBuilders) {
            val cmd = builder.tryBuildCommand(template)
            if (cmd != null) {
                return cmd
            }
        }
        return null
    }
}

fun defaultCommandBuilder(
    name: String,
    producer: (List<String>) -> Command
) : CommandBuilder = object : CommandBuilder {
    override fun tryBuildCommand(template: CommandTemplate): Command? =
        if (template.name == name) {
            producer.invoke(template.arguments)
        } else {
            null
        }

}