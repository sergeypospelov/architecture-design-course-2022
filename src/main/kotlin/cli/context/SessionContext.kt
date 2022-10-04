package cli.context

import java.nio.file.Path
import java.nio.file.Paths

/**
 * SessionContext holds some set of (mutable) properties that are shared throughout the execution of the program.
 */
object SessionContext {
    var currentDirectory: Path = Paths.get(".")
}