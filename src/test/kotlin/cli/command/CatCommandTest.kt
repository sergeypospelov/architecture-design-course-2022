package cli.command

import cli.command.TestUtil.convertToString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
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
                val errorStream = ByteArrayOutputStream()

                val exitCode = CatCommand(listOf())
                    .execute(inputStream, outputStream, errorStream)

                assertEquals(0, exitCode)
                assertEquals(userInput, outputStream.convertToString())
                assertEquals("", errorStream.convertToString())
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
                val errorStream = ByteArrayOutputStream()

                val exitCode = CatCommand(listOf(testFile.absolutePath))
                    .execute(InputStream.nullInputStream(), outputStream, errorStream)

                assertEquals(0, exitCode)
                assertEquals(testFile.readText(), outputStream.convertToString())
                assertEquals("", errorStream.convertToString())
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
                val errorStream = ByteArrayOutputStream()

                val exitCode = CatCommand(testFiles.map { it.absolutePath })
                    .execute(InputStream.nullInputStream(), outputStream, errorStream)

                assertEquals(0, exitCode)
                val expectedOutput = testFiles.joinToString("") { it.readText() }
                assertEquals(expectedOutput, outputStream.convertToString())
                assertEquals("", errorStream.convertToString())
            }
        }

    @Test
    fun `should fail if the file does not exist`() {
        val fakeFile = "./src/test/resources/some_weird_file_name.wtf"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val catCommand = CatCommand(listOf(fakeFile))

        val exitCode = catCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        assertEquals("", outputStream.convertToString())
        assertEquals(catCommand.fileDoesNotExist(fakeFile), errorStream.convertToString())
    }

    @Test
    fun `should process all existing files`() {
        val trueFile = "./src/test/resources/line.txt"
        val fakeFile = "./src/test/resources/some_weird_file_name.wtf"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val catCommand = CatCommand(listOf(trueFile, fakeFile, trueFile))

        val exitCode = catCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        val expectedOutput = listOf(
            File(trueFile).readText(),
            File(trueFile).readText()
        ).joinToString("")

        val expectedError = catCommand.fileDoesNotExist(fakeFile) // only the second file was not processed correctly

        assertEquals(expectedOutput, outputStream.convertToString())
        assertEquals(expectedError, errorStream.convertToString())
    }

    @Test
    fun `should fail if the file is a directory`() {
        val directory = "./src/test/resources/directory"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val catCommand = CatCommand(listOf(directory))

        val exitCode = catCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        assertEquals("", outputStream.convertToString())
        assertEquals(catCommand.fileIsDirectory(directory), errorStream.convertToString())
    }

    @Test
    fun `should process all files that are not directories`() {
        val trueFile = "./src/test/resources/line.txt"
        val directory = "./src/test/resources/directory"
        val outputStream = ByteArrayOutputStream()
        val errorStream = ByteArrayOutputStream()
        val catCommand = CatCommand(listOf(trueFile, directory, trueFile))

        val exitCode = catCommand.execute(InputStream.nullInputStream(), outputStream, errorStream)

        assertEquals(1, exitCode)
        val expectedOutput = listOf(
            File(trueFile).readText(),
            File(trueFile).readText()
        ).joinToString("")

        val expectedError = catCommand.fileIsDirectory(directory)

        assertEquals(expectedOutput, outputStream.convertToString())
        assertEquals(expectedError, errorStream.convertToString())
    }
}