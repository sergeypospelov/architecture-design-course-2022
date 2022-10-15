package cli.command

import cli.context.SessionContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.file.Paths

class CdCommandTest {

    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var errorStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        outputStream = ByteArrayOutputStream()
        errorStream = ByteArrayOutputStream()
        SessionContext.currentDirectory = Paths.get(System.getProperty("user.dir"))
    }

    @Test
    fun testCdCommandIfNoArguments() {
        val expectedPath = Paths.get(System.getProperty("user.home"))

        val emptyArguments = listOf<String>()
        val exitCode = CdCommand(emptyArguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDot() {
        val expectedPath = Paths.get(System.getProperty("user.dir"))

        val arguments = listOf(".")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfDoubleDots() {
        val expectedPath = Paths.get(System.getProperty("user.dir")).parent

        val arguments = listOf("..")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfTilda() {
        val expectedPath = Paths.get(System.getProperty("user.home"))

        val arguments = listOf("~")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleSlash() {
        val expectedPath = Paths.get("/")

        val arguments = listOf("/")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSeveralSlashes() {
        val expectedPath = Paths.get("/")

        val arguments = listOf("//////")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDirectory() {
        val expectedPath = Paths.get(System.getProperty("user.dir") + "/src")

        val arguments = listOf("src")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDirectoryWithSlash() {
        val expectedPath = Paths.get(System.getProperty("user.dir") + "/src")

        val arguments = listOf("src/")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDirectoryWithSlashes() {
        val expectedPath = Paths.get(System.getProperty("user.dir") + "/src")

        val arguments = listOf("src/////")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDirectoryWithSingleDot() {
        val expectedPath = Paths.get(System.getProperty("user.dir") + "/src")

        val arguments = listOf("src/////.")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSingleDirectoryWithDots() {
        val expectedPath = Paths.get(System.getProperty("user.dir"))

        val arguments = listOf("src/////..")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfSeveralDirectories() {
        val expectedPath = Paths.get(System.getProperty("user.dir") + "/src/main")

        val arguments = listOf("src/main")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)
        val path = SessionContext.currentDirectory

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(path, expectedPath)
    }

    @Test
    fun testCdCommandIfUnknownDirectory() {
        val arguments = listOf("unknown_directory")
        val exitCode = CdCommand(arguments)
            .execute(InputStream.nullInputStream(), outputStream, errorStream)

        Assertions.assertEquals(1, exitCode)
    }
}
