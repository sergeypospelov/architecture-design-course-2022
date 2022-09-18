package cli.io

import java.io.OutputStream

internal fun OutputStream.printAndFlush(string: String) {
    write(string.toByteArray())
    flush()
}