package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class GrepCommandTest {

    @TestFactory
    fun `should not find any matches`() =
        listOf(
            "not found",
            "11",
            "123456",
            "Hello",
            "HELLO",
            "end of file",
            "end of FILE",
            "End OF file",
        ).mapIndexed { index, query ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals("", outputStream.convertToString())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should find only one match`() =
        listOf(
            "12345" to "12345",
            "23" to "12345",
            "hello" to "hello",
            "ell" to "hello",
            "8 9" to "8 9",
            "End of File" to "End of File",
            "End of Fil" to "End of File",
            "d of Fil" to "End of File",
        ).mapIndexed { index, (query, expectedResult) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedResult, outputStream.convertToString())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should find all matches`() =
        listOf(
            "Some test text" to "Some test text 1${nl}Some test text 9",
            "some test text" to " some test text 2${nl}  some test text 7${nl} some test text 8",
            "10" to "    10${nl}    10",
        ).mapIndexed { index, (query, expectedResult) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedResult, outputStream.convertToString())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should find all matches by RegEx`() =
        listOf(
            "." to File(testFileName).readLines().sumOf { it.length },
            "\\d" to 22,
            "\\d\\d" to 4,
            "[Ss]ome" to 9,
            "[Tt]est" to 8,
            "[Ss]ome [Tt]est text" to 6,
            "End .*" to 1,
            "some .*" to 7,
            ".*test.*" to 7,
        ).mapIndexed { index, (query, expectedResultCount) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedResultCount, outputStream.convertToString().countGrepResults())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should find all matches with -i option`() =
        listOf(
            "1" to 4,
            "hello" to 1,
            "Hello" to 1,
            "HELLO" to 1,
            "end of file" to 1,
            "end OF file" to 1,
            "some test text" to 9,
            "Some test text" to 9,
            "SOME TEST TEXT" to 9,
            "sOmE tEsT tExT" to 9,
        ).mapIndexed { index, (query, expectedResultCount) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName, "-i"))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedResultCount, outputStream.convertToString().countGrepResults())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should find all matches with -w option`() =
        listOf(
            "hello" to 1,
            "Some" to 2,
            "test" to 7,
            "Text" to 1,
            "some test" to 5,
            "End" to 1,
            "End of" to 1,
            "nd" to 0,
            "est" to 0,
            "ex" to 0,
            "o" to 0,
        ).mapIndexed { index, (query, expectedResultCount) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName, "-w"))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedResultCount, outputStream.convertToString().countGrepResults())
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should print correct number of lines with -A option`() =
        listOf(
            Triple("12345", 0, 1),
            Triple("23", 1, 2),
            Triple("hello", 2, 3),
            Triple("ell", 3, 4),
            Triple("8 9", 4, 5),
            Triple("End of File", 5, 2),
            Triple("End of Fil", 6, 2),
        ).mapIndexed { index, (query, positionalA, expectedLinesCount) ->
            DynamicTest.dynamicTest("$index. $query") {
                val inputStream = InputStream.nullInputStream()
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()

                val exitCode = GrepCommand(listOf(query, testFileName, "-A", positionalA.toString()))
                    .execute(inputStream, outputStream, errorStream)

                Assertions.assertEquals(0, exitCode)
                Assertions.assertEquals(expectedLinesCount, outputStream.convertToString().split("\n").size)
                Assertions.assertEquals("", errorStream.convertToString())
            }
        }

    // internal

    private val testFileName = "./src/test/resources/grep.txt"
    
    private val nl = GREP_RESULT_SEPARATOR // alias for convenience

    private fun String.countGrepResults(): Int =
        this.split(nl).filterNot { it.isEmpty() }.size
}