package cli.command

import java.io.InputStream
import java.io.OutputStream

class UnknownCommand(
    override val name: String,
    override val arguments: List<String>
) : Command {

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        TODO("Not yet implemented")
    }
}