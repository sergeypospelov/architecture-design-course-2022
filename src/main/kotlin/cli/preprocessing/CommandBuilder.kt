package cli.preprocessing

import cli.command.*

interface CommandBuilder {
    fun buildCommand(template: CommandTemplate): Command
}

class CommandBuilderImpl : CommandBuilder {

    override fun buildCommand(template: CommandTemplate): Command =
        when (template.name) {
            "cat"  -> CatCommand(template.arguments)
            "echo" -> EchoCommand(template.arguments)
            "exit" -> ExitCommand()
            "pwd"  -> PwdCommand(template.arguments)
            "wc"   -> WcCommand(template.arguments)
            else   -> UnknownCommand(template.name, template.arguments)
        }
}
