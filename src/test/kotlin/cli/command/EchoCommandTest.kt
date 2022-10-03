package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EchoCommandTest {

    @TestFactory
    fun `should work with one argument`() =
        listOf(
            "0",
            "a",
            "text",
            """'single quotes'""",
            """"double quotes"""",
            """'  \n \n\t \t  '""",
            """"  \n \n\t \t  """",
        ).mapIndexed { index, argument ->
            DynamicTest.dynamicTest("$index. $argument") {
                val outputStream = ByteArrayOutputStream()
                val exitCode = EchoCommand(listOf(argument))
                    .execute(InputStream.nullInputStream(), outputStream)
                assertEquals(0, exitCode)
                assertEquals(argument, outputStream.convertToString())
            }
        }

    @TestFactory
    fun `should work with several arguments`() =
        listOf(
            listOf("0", "1", "2"),
            listOf("a", "ab", "abc"),
            listOf("some", "test", "text"),
            listOf(
                """'first'""",
                """'second'""",
                """"another"""",
                """" outside 'inside' """",
                """' " nested again " '"""
            ),
        ).mapIndexed { index, arguments ->
            DynamicTest.dynamicTest("$index. $arguments") {
                val outputStream = ByteArrayOutputStream()
                val exitCode = EchoCommand(arguments)
                    .execute(InputStream.nullInputStream(), outputStream)
                assertEquals(0, exitCode)
                assertEquals(arguments.joinToString(""), outputStream.convertToString())
            }
        }
}