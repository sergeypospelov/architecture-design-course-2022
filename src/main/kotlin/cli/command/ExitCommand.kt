package cli.command

import java.io.InputStream
import java.io.OutputStream

class ExitCommand(
    override val arguments: List<String>
) : Command {
    override val name: String = "exit"

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        TODO("Not yet implemented")
    }
}