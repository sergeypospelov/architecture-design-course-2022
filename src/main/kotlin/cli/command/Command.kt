package cli.command

import java.io.InputStream
import java.io.OutputStream

interface Command {
    val name: String

    val arguments: List<String>

    fun execute(inputStream: InputStream, outputStream: OutputStream): Int
}
