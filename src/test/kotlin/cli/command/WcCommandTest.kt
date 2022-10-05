package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class WcCommandTest {
    @TestFactory
    fun `should work without arguments`() =
        listOf(
            "0"
                to WcResult(0, 1, 1),
            "some text"
                to WcResult(0, 2, 9),
            "some\nmultiline\ntext"
                to WcResult(2, 3, 19),
            " some\nmultiline\ntext with  spaces\tand\ttabs"
                to WcResult(2, 7, 42),
            """ 'single' quotes """
                to WcResult(0, 2, 17),
            """ "double" quotes """
                to WcResult(0, 2, 17),
            """ qp_1"87''*1%"!'$7#gh """
                to WcResult(0, 1, 22),
        ).mapIndexed { index, (userInput, expectedResult) ->
            DynamicTest.dynamicTest("$index. $userInput") {
                val inputStream = ByteArrayInputStream(userInput.toByteArray())
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()


                val exitCode = WcCommand(listOf())
                    .execute(inputStream, outputStream, errorStream)

                assertEquals(0, exitCode)
                assertEquals(listOf(expectedResult), outputStream.convertToString().parseWcResult())
                assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should process only one file`() =
        listOf(
            "empty.txt"
                to WcResult(0, 0, 0),
            "blank.txt"
                to WcResult(7, 0, 7),
            "line.txt"
                to WcResult(0, 3, 13),
            "text.txt"
                to WcResult(9, 10, 68),
        ).mapIndexed { index, (fileName, expectedResult) ->
            DynamicTest.dynamicTest("$index. $fileName") {
                val testFile = File("./src/test/resources/$fileName")
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()


                val exitCode = WcCommand(listOf(testFile.absolutePath))
                    .execute(InputStream.nullInputStream(), outputStream, errorStream)
                assertEquals(0, exitCode)

                val expectedResultsWithNames = listOf(expectedResult.copy(name = testFile.absolutePath))
                assertEquals(expectedResultsWithNames, outputStream.convertToString().parseWcResult())
                assertEquals("", errorStream.convertToString())
            }
        }

    @TestFactory
    fun `should process the contents of several files`() =
        listOf(
            listOf("empty.txt", "empty.txt")
                to listOf(
                    WcResult(0, 0, 0),
                    WcResult(0, 0, 0),
                    WcResult(0, 0, 0, "total")
                ),
            listOf("empty.txt", "blank.txt", "line.txt", "empty.txt")
                to listOf(
                    WcResult(0, 0, 0),
                    WcResult(7, 0, 7),
                    WcResult(0, 3, 13),
                    WcResult(0, 0, 0),
                    WcResult(7, 3, 20, "total")
                ),
            listOf("text.txt", "empty.txt", "line.txt", "text.txt", "empty.txt", "blank.txt")
                to listOf(
                    WcResult(9, 10, 68),
                    WcResult(0, 0, 0),
                    WcResult(0, 3, 13),
                    WcResult(9, 10, 68),
                    WcResult(0, 0, 0),
                    WcResult(7, 0, 7),
                    WcResult(25, 23, 156, "total")
                ),
        ).mapIndexed { index, (fileNames, expectedResults) ->
            DynamicTest.dynamicTest("$index. $fileNames") {
                val testFiles = fileNames.map { fileName ->
                    File("./src/test/resources/$fileName")
                }
                val outputStream = ByteArrayOutputStream()
                val errorStream = ByteArrayOutputStream()


                val exitCode = WcCommand(testFiles.map { it.absolutePath })
                    .execute(InputStream.nullInputStream(), outputStream, errorStream)
                assertEquals(0, exitCode)

                val expectedResultsWithNames = expectedResults.toMutableList()
                for (i in testFiles.indices) { // without the last "total" item
                    expectedResultsWithNames[i] = expectedResults[i].copy(name = testFiles[i].absolutePath)
                }
                assertEquals(expectedResultsWithNames, outputStream.convertToString().parseWcResult())
                assertEquals("", errorStream.convertToString())
            }
        }

    @Test
    fun `should fail if the file does not exist`() {
        val fakeFile = "./src/test/resources/some_weird_file_name.wtf"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val wcCommand = WcCommand(listOf(fakeFile))

        val exitCode = wcCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        assertEquals("", outputStream.convertToString())
        assertEquals(wcCommand.fileDoesNotExist(fakeFile), errorStream.convertToString())
    }

    @Test
    fun `should process all existing files`() {
        val trueFile = "./src/test/resources/line.txt"
        val fakeFile = "./src/test/resources/some_weird_file_name.wtf"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val wcCommand = WcCommand(listOf(trueFile, fakeFile, trueFile))

        val exitCode = wcCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
    }

    @Test
    fun `should fail if the file is a directory`() {
        val directory = "./src/test/resources/directory"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val wcCommand = WcCommand(listOf(directory))

        val exitCode = wcCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        assertEquals("", outputStream.convertToString())
        assertEquals(wcCommand.fileIsDirectory(directory), errorStream.convertToString())
    }

    @Test
    fun `should process all files that are not directories`() {
        val trueFile = "./src/test/resources/line.txt"
        val directory = "./src/test/resources/directory"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()

        val wcCommand = WcCommand(listOf(trueFile, directory, trueFile))

        val exitCode = wcCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)
        assertEquals(1, exitCode)
    }

    // internal

    private data class WcResult(
        val lines: Int,
        val words: Int,
        val bytes: Int,
        val name: String? = null
    )

    private fun String.parseWcResult(): List<WcResult> =
        this.split("\n").map { line ->
            val parsedLine = line.split("\\s+".toRegex()).filterNot { it.isBlank() }
            if (parsedLine.size == 3) {
                val (lines, words, bytes) = parsedLine
                WcResult(lines.toInt(), words.toInt(), bytes.toInt())
            } else {
                val (lines, words, bytes, name) = parsedLine
                WcResult(lines.toInt(), words.toInt(), bytes.toInt(), name)
            }
        }
}