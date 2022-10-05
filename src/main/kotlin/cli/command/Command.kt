package cli.command

import java.io.InputStream
import java.io.OutputStream

/**
 * bash command interface
 */
interface Command {

    /** command name **/
    val name: String

    /** command arguments **/
    val arguments: List<String>

    /**
     * @param inputStream input stream for command
     * @param outputStream output stream for command
     * @return status code (0 if success, other if error)
     */
    fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int
}
