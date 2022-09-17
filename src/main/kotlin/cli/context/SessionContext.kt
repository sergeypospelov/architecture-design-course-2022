package cli.context

import java.nio.file.Path
import java.nio.file.Paths

object SessionContext {
    var currentDirectory: Path = Paths.get(".")
}