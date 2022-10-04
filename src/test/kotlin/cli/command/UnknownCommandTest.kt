package cli.command

import cli.command.TestUtil.convertToString
import org.apache.commons.lang3.SystemUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class UnknownCommandTest {

    @Test
    fun `should show contents of the current directory`() {
        val name = if (SystemUtils.IS_OS_WINDOWS) "dir" else "ls"
        val arguments = listOf<String>()
        val outputStream = ByteArrayOutputStream()
        val exitCode = UnknownCommand(name, arguments)
            .execute(InputStream.nullInputStream(), outputStream)
        assertEquals(0, exitCode)
    }

    @Test
    @Disabled
    fun `should call python`() {
        val name = if (SystemUtils.IS_OS_WINDOWS) "python" else "python3"
        val arguments = listOf("./src/test/resources/script.py")
        val outputStream = ByteArrayOutputStream()
        val exitCode = UnknownCommand(name, arguments)
            .execute(InputStream.nullInputStream(), outputStream)
        assertEquals(0, exitCode)
        assertEquals("hello", outputStream.convertToString())
    }

    @Test
    fun `should fail on the unknown command`() {
        val name = "unknown_command_228"
        val arguments = listOf<String>()
        val exitCode = UnknownCommand(name, arguments)
            .execute(InputStream.nullInputStream(), OutputStream.nullOutputStream())
        Assertions.assertNotEquals(0, exitCode)
    }
}