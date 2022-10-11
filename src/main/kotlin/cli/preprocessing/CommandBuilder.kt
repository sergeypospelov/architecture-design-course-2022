package cli.preprocessing

import cli.command.*

interface CommandBuilder {
    /**
     * builds [Command] from [CommandTemplate]
     */
    fun buildCommand(template: CommandTemplate): Command
}

/**
 * [CommandBuilder] implementation
 */
class CommandBuilderImpl : CommandBuilder {

    override fun buildCommand(template: CommandTemplate): Command =
        when (template.name) {
            "cat"  -> CatCommand(template.arguments)
            "echo" -> EchoCommand(template.arguments)
            "exit" -> ExitCommand()
            "pwd"  -> PwdCommand()
            "wc"   -> WcCommand(template.arguments)
            "grep" -> GrepCommand(template.arguments)
            else   -> UnknownCommand(template.name, template.arguments)
        }
}
