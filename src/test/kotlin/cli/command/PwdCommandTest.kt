package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

class PwdCommandTest {

    @Test
    fun `should returns the current directory`() {
        val outputStream = ByteArrayOutputStream()
        val exitCode = PwdCommand()
            .execute(InputStream.nullInputStream(), outputStream)
        assertEquals(0, exitCode)
        val expectedPath = Paths.get(File(".").absolutePath)
        val actualPath = Paths.get(outputStream.convertToString())
        assertEquals(expectedPath, actualPath)
    }
}