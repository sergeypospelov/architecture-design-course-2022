package cli.command

import java.io.InputStream
import java.io.OutputStream

class PwdCommand(
    override val arguments: List<String>
) : Command {
    override val name = "pwd"


    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        TODO("Not yet implemented")
    }
}