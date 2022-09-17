package cli.command

import java.io.InputStream
import java.io.OutputStream

class CatCommand(
    override val arguments: List<String>
) : Command {
    override val name: String = "cat"

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        TODO("Not yet implemented")
    }
}