package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.file.Paths

class LsCommandTest {

    @Test
    fun testLsCommand() {
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()

        val arguments = listOf("${Paths.get(System.getProperty("user.dir"))}/src/test/resources")

        val exitCode = LsCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)

        Assertions.assertEquals(0, exitCode)
        val expectedOutput = listOf("blank.txt", "empty.txt", "grep.txt", "line.txt", "script.py", "text.txt", "directory", "")

        val actualOutput = outputStream.convertToString().split("\n")
        Assertions.assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testLsCommandIfUnknownPath() {
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val arguments = listOf("unknown_path")

        val exitCode = LsCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)

        Assertions.assertEquals(1, exitCode)
    }
}
