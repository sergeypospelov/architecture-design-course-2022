package cli.command

import java.io.InputStream
import java.io.OutputStream

class WcCommand(
    override val arguments: List<String>
) : Command {
    override val name: String = "wc"

    override fun execute(inputStream: InputStream, outputStream: OutputStream): Int {
        TODO("Not yet implemented")
    }
}