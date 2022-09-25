package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class CatCommandTest {

    @TestFactory
    fun `should work without arguments`() =
        listOf(
            "0",
            "some text",
            "some\nmultiline\ntext",
            " some\nmultiline\ntext with  spaces\tand\ttabs",
            """ 'single' quotes """,
            """ "double" quotes """,
            """ qp_1"87''*1%"!'$7#gh """,
        ).mapIndexed { index, userInput ->
            DynamicTest.dynamicTest("$index. $userInput") {
                val inputStream = ByteArrayInputStream(userInput.toByteArray())
                val outputStream = ByteArrayOutputStream()
                val exitCode = CatCommand(listOf())
                    .execute(inputStream, outputStream)
                assertEquals(0, exitCode)
                assertEquals(userInput + "\n", outputStream.convertToString())
            }
        }

    @TestFactory
    fun `should print the contents of the file`() =
        listOf(
            "empty.txt",
            "blank.txt",
            "line.txt",
            "text.txt",
        ).mapIndexed { index, fileName ->
            DynamicTest.dynamicTest("$index. $fileName") {
                val testFile = File("./src/test/resources/$fileName")
                val outputStream = ByteArrayOutputStream()
                val exitCode = CatCommand(listOf(testFile.absolutePath))
                    .execute(InputStream.nullInputStream(), outputStream)
                assertEquals(0, exitCode)
                assertEquals(testFile.readText(), outputStream.convertToString())
            }
        }

    @TestFactory
    fun `should print the contents of several files`() =
        listOf(
            listOf("empty.txt", "empty.txt"),
            listOf("empty.txt", "blank.txt", "line.txt", "empty.txt"),
            listOf("text.txt", "empty.txt", "line.txt", "text.txt", "empty.txt", "blank.txt"),
        ).mapIndexed { index, fileNames ->
            DynamicTest.dynamicTest("$index. $fileNames") {
                val testFiles = fileNames.map { fileName ->
                    File("./src/test/resources/$fileName")
                }
                val outputStream = ByteArrayOutputStream()
                val exitCode = CatCommand(testFiles.map { it.absolutePath })
                    .execute(InputStream.nullInputStream(), outputStream)
                assertEquals(0, exitCode)
                val expectedOutput = testFiles.joinToString("") { it.readText() }
                assertEquals(expectedOutput, outputStream.convertToString())
            }
        }
}