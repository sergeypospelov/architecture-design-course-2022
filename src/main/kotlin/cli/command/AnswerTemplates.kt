package cli.command

import cli.context.SessionContext
import cli.io.printAndFlush
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

private const val FILE_DOES_NOT_EXIST = "No such file or directory"
private const val FILE_IS_DIRECTORY = "Is a directory"

/**
 * @return error message for file does not exist
 */
internal fun Command.fileDoesNotExist(fileName: String): String = "$name: $fileName: $FILE_DOES_NOT_EXIST\n"

/**
 * @return error message for file is directory
 */
internal fun Command.fileIsDirectory(fileName: String): String = "$name: $fileName: $FILE_IS_DIRECTORY\n"

/**
 * checks is file [fileName] exists and is not directory
 * @param block is executed on file if check pass
 */
internal fun Command.checkExistsAndNotDirectory(
    fileName: String,
    outputStream: OutputStream,
    block: (Path) -> Int
): Int {
    val file = SessionContext.currentDirectory.resolve(fileName)
    return when {
        !file.exists() -> {
            outputStream.printAndFlush(fileDoesNotExist(fileName))
            1
        }
        file.isDirectory() -> {
            outputStream.printAndFlush(fileIsDirectory(fileName))
            1
        }
        else -> block(file)
    }
}
