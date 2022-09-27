package cli.io

import java.io.OutputStream

/**
 * prints [string] and flushes [OutputStream]
 */
internal fun OutputStream.printAndFlush(string: String) {
    write(string.toByteArray())
    flush()
}