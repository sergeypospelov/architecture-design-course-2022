package cli.preprocessing

import cli.context.EnvironmentVariables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SubstitutorTest {

    private val environmentVariables = listOf(
        "KEY" to "VALUE",
        "x" to "ex",
        "y" to "it",
        "num" to "1",
        "my_command" to "echo",
    )

    @TestFactory
    fun `should substitute environment variables`() =
        (listOf("?" to "0") + environmentVariables)
            .run {
                val env = EnvironmentVariables(environmentVariables)
                mapIndexed { index, (userInput, expectedResult) ->
                    dynamicTest("$index. $userInput") {
                        val actualResult = SubstitutorImpl(env).substitute("$$userInput")
                        assertEquals(expectedResult, actualResult)
                    }
                }
            }

    @TestFactory
    fun `should substitute environment variables into text`() =
        listOf(
            "${"$"}x${"$"}y" to "exit",
            "new_var=${"$"}x" to "new_var=ex",
            "${"$"}my_command 1 | ${"$"}x${"$"}y" to "echo 1 | exit",
            " ${"$"}my_command\tsome text " to " echo\tsome text ",
            "cat ${"$"}KEY " to "cat VALUE ",
            "${"$"}my_command ${"$"}num" to "echo 1",
        ).run {
            val env = EnvironmentVariables(environmentVariables)
            mapIndexed { index, (userInput, expectedResult) ->
                dynamicTest("$index. $userInput") {
                    val actualResult = SubstitutorImpl(env).substitute(userInput)
                    assertEquals(expectedResult, actualResult)
                }
            }
        }

    @TestFactory
    fun `should not substitute environment variables without dollar`() =
        listOf(
            "xy" to "xy",
            "new_var=x" to "new_var=x",
            " my_command\tsome text " to " my_command\tsome text ",
            "cat KEY" to "cat KEY",
            "cat KEY | echo" to "cat KEY | echo",
            "my_command num" to "my_command num",
        ).run {
            val env = EnvironmentVariables(environmentVariables)
            mapIndexed { index, (userInput, expectedResult) ->
                dynamicTest("$index. $userInput") {
                    val actualResult = SubstitutorImpl(env).substitute(userInput)
                    assertEquals(expectedResult, actualResult)
                }
            }
        }

    @TestFactory
    fun `should substitute environment variables into double quotes`() =
        listOf(
            "\"${"$"}x${"$"}y\"" to "\"exit\"",
            "new_var=\"${"$"}x\"" to "new_var=\"ex\"",
            "\" ${"$"}my_command\"\tsome text " to "\" echo\"\tsome text ",
            "\"${"$"}my_command\" 1 | ${"$"}x${"$"}y" to "\"echo\" 1 | exit",
            "\"cat ${"$"}KEY\"" to "\"cat VALUE\"",
            "cat \"${"$"}KEY\".txt" to "cat \"VALUE\".txt",
            "\"${"$"}my_command ${"$"}num\"" to "\"echo 1\"",
            "${"$"}my_command \"${"$"}num\"" to "echo \"1\"",
        ).run {
            val env = EnvironmentVariables(environmentVariables)
            mapIndexed { index, (userInput, expectedResult) ->
                dynamicTest("$index. $userInput") {
                    val actualResult = SubstitutorImpl(env).substitute(userInput)
                    assertEquals(expectedResult, actualResult)
                }
            }
        }

    @TestFactory
    fun `should not substitute environment variables into single quotes`() =
        listOf(
            "\'${"$"}x${"$"}y\'" to "\'${"$"}x${"$"}y\'",
            "new_var=\'${"$"}x\'" to "new_var=\'${"$"}x\'",
            "\' ${"$"}my_command\'\tsome text " to "\' ${"$"}my_command\'\tsome text ",
            "\'${"$"}my_command\' 1 | ${"$"}x${"$"}y" to "\'${"$"}my_command\' 1 | exit",
            "\'cat ${"$"}KEY\'" to "\'cat ${"$"}KEY\'",
            "cat \'${"$"}KEY\'.txt" to "cat \'${"$"}KEY\'.txt",
            "\'${"$"}my_command ${"$"}num\'" to "\'${"$"}my_command ${"$"}num\'",
            "${"$"}my_command \'${"$"}num\'" to "echo \'${"$"}num\'",
        ).run {
            val env = EnvironmentVariables(environmentVariables)
            mapIndexed { index, (userInput, expectedResult) ->
                dynamicTest("$index. $userInput") {
                    val actualResult = SubstitutorImpl(env).substitute(userInput)
                    assertEquals(expectedResult, actualResult)
                }
            }
        }

    @TestFactory
    fun `should not substitute to the bad pattern on the unknown variable`() =
        listOf(
            "${"$"}!" to "!",
            "${"$"}UNKNOWN" to "",
            "${"$"}${"$"}" to "",
        ).run {
            val env = EnvironmentVariables(environmentVariables)
            mapIndexed { index, (userInput, expectedResult) ->
                dynamicTest("$index. $userInput") {
                    val actualResult = SubstitutorImpl(env).substitute(userInput)
                    assertEquals(expectedResult, actualResult)
                }
            }
        }
}