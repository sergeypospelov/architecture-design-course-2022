package cli.command

import cli.context.SessionContext
import cli.io.printAndFlush
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Implementation of [Command] interface for bash command cd:
 * cd - change actual directory depending on user's input
 * @property arguments contains user's input about directory to change
 */
class CdCommand(override val arguments: List<String>) : Command {

    private val homeDirectory = System.getProperty("user.home")
    private val regexp = "/+".toRegex()
    override val name = CD_COMMAND_NAME

    /**
     * Changes current directory according to first argument's path
     * @return status code (0 if success, other if error)
     */
    override fun execute(inputStream: InputStream, outputStream: OutputStream, errorStream: OutputStream): Int {
        var exitCode = 0

        if (arguments.isEmpty()) {
            SessionContext.currentDirectory = Paths.get(homeDirectory)
        } else {
            val inputPath = arguments.first().replace(regexp, PATH_DELIMITER)
            val parsedInputPath = inputPath.split(PATH_DELIMITER)

            val updatedDirectoryPath = when {
                parsedInputPath.first() == TILDA -> updateDirectoryPath(
                    Paths.get(homeDirectory),
                    parsedInputPath.drop(1)
                )

                inputPath.startsWith(PATH_DELIMITER) -> updateDirectoryPath(Paths.get(PATH_DELIMITER), parsedInputPath)
                else -> updateDirectoryPath(Paths.get(SessionContext.currentDirectory.toString()), parsedInputPath)
            }

            if (updatedDirectoryPath == null) {
                errorStream.printAndFlush(fileOrDirectoryDoesNotExists(arguments.first()))
                exitCode = 1
            } else {
                SessionContext.currentDirectory = updatedDirectoryPath
            }
        }
        return exitCode
    }

    private fun updateDirectoryPath(directory: Path, inputPath: List<String>): Path? {
        var updatedDirectoryPath = directory
        for (pathElem in inputPath) {
            updatedDirectoryPath = if (pathElem == SINGLE_DOT || pathElem == EMPTY_STRING) {
                continue
            } else if (pathElem == TWO_DOTS && updatedDirectoryPath.parent != null) {
                updatedDirectoryPath.parent
            } else {
                val possibleChild = "$updatedDirectoryPath/$pathElem"
                val possibleChildPath = Paths.get(possibleChild)
                if (Files.exists(possibleChildPath)) {
                    possibleChildPath
                } else {
                    return null
                }
            }
        }
        return updatedDirectoryPath
    }

    companion object {

        private const val CD_COMMAND_NAME = "cd"
        private const val TILDA = "~"
        private const val PATH_DELIMITER = "/"
        private const val EMPTY_STRING = ""
        private const val SINGLE_DOT = "."
        private const val TWO_DOTS = ".."
    }
}
