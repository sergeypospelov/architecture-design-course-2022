package cli.command

import cli.context.SessionContext
import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Implementation of [Command] interface for bash command ls:
 * ls - display folders and files in directory
 * @property arguments contains directory to display
 */
class LsCommand(override val arguments: List<String>) : Command {

    override val name = LS_COMMAND_NAME

    /**
     * displays current directory's content to [outputStream]
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {

        val directory = when (arguments.isNotEmpty()) {
            true -> Paths.get(arguments.first())
            false -> SessionContext.currentDirectory
        }

        if (Files.exists(directory).not()) {
            errorStream.printAndFlush(fileOrDirectoryDoesNotExists(arguments.first()))
            return 1
        }

        val fileDirMap =
            Files.list(directory).collect(Collectors.partitioningBy { Files.isDirectory(it) })

        fileDirMap.values.forEach { paths ->
            paths.forEach {
                outputStream.printAndFlush(it.fileName.toString() + "\n")
            }
        }

        return 0
    }

    companion object {
        private const val LS_COMMAND_NAME = "ls"
    }
}
