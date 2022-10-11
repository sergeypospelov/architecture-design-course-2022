package cli.command

import java.io.ByteArrayOutputStream

object TestUtil {
    fun ByteArrayOutputStream.convertToString() =
        String(this.toByteArray())
}