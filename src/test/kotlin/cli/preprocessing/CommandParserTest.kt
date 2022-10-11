package cli.preprocessing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class CommandParserTest {

    @TestFactory
    fun `should return Retry for the empty input`() =
        listOf(
            "",
            " ",
            "\t",
            "\n",
            "  \t\n  \t\t  \n\n  \t\n  ",
        ).mapIndexed { index, userInput ->
            dynamicTest("$index. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                assertTrue(parserResult is Retry)
            }
        }

    @TestFactory
    fun `should parse the command name`() =
        listOf(
            "1" to "1",
            "a" to "a",
            "text" to "text",
            "TEXT" to "TEXT",
            "1t_!" to "1t_!",
            "  no_spaces " to "no_spaces",
            " name  a r g u m e n t s " to "name",
            """ "double quotes" """ to "double quotes",
            """ 'single quotes' """ to "single quotes",
            """ "double quotes" arg0 arg1 """ to "double quotes",
            """ 'single quotes' some text """ to "single quotes",
            """ 'double quote " inside single quotes' """ to """double quote " inside single quotes""",
            """ "single quote ' inside double quotes" """ to """single quote ' inside double quotes""",
            """ " nested ' single quotes' !" """ to """ nested ' single quotes' !""",
            """ ' nested " double quotes" !' """ to """ nested " double quotes" !""",
        ).mapIndexed { index, (userInput, expectedName) ->
            dynamicTest("${index + 1}. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                val actualName = (parserResult as CommandSequenceTemplate).commands.single().name
                assertEquals(expectedName, actualName)
            }
        }

    @TestFactory
    fun `should parse the command arguments`() =
        listOf(
            "_ 1" to listOf("1"),
            "_ a" to listOf("a"),
            "_ text" to listOf("text"),
            "_ TEXT" to listOf("TEXT"),
            "_ 1t_!" to listOf("1t_!"),
            "_ arg1 arg2" to listOf("arg1", "arg2"),
            "_  0  1  2  3" to listOf("0", "1", "2", "3"),
            """_ "double quotes" """ to listOf("double quotes"),
            """_ 'single quotes' """ to listOf("single quotes"),
            """_ 'double quote " inside single quotes' """ to listOf("""double quote " inside single quotes"""),
            """_ "single quote ' inside double quotes" """ to listOf("""single quote ' inside double quotes"""),
            """_ " nested ' single quotes' !" """ to listOf(""" nested ' single quotes' !"""),
            """_ ' nested " double quotes" !' """ to listOf(""" nested " double quotes" !"""),
            """_ a, "aa", 'aaa', "'a'", '"aa"' """ to listOf("a,", "aa,", "aaa,", "'a',", """"aa""""),
        ).mapIndexed { index, (userInput, expectedArguments) ->
            dynamicTest("${index + 1}. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                val actualArguments = (parserResult as CommandSequenceTemplate).commands.single().arguments
                assertEquals(expectedArguments, actualArguments)
            }
        }

    @TestFactory
    fun `should fail on the unmatched quote`() =
        listOf(
            """ ' """,
            """ " """,
            """ 'name """,
            """ "name """,
            """ _ ' """,
            """ _ " """,
            """ _ arg0 "some text  """,
            """ _ "different quotes' """,
            """ _ 'another different quotes" """,
            """ _ ` backtick? ' """,
            """ _ ` backtick again? " """,
            """ _ "'intersection"'  """,
            """ _ '"intersection'"  """,
        ).mapIndexed { index, userInput ->
            dynamicTest("$index. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                val errorDescription = (parserResult as ParseError).errorDescription
                assertEquals("Unmatched quote", errorDescription)
            }
        }

    @TestFactory
    fun `should parse variable assignment`() =
        listOf(
            "a=b" to VariableAssignment("a", "b"),
            "a=" to VariableAssignment("a", ""),
            "a==" to VariableAssignment("a", "="),
            "a=a=a" to VariableAssignment("a", "a=a"),
            "aaa=bbb" to VariableAssignment("aaa", "bbb"),
            "  aaa=bbb" to VariableAssignment("aaa", "bbb"),
            "\taaa=bbb" to VariableAssignment("aaa", "bbb"),
            "Key=Value" to VariableAssignment("Key", "Value"),
            "Key=\"some text\"" to VariableAssignment("Key", "\"some text\""),
            "Key=\'some text\'" to VariableAssignment("Key", "\'some text\'"),
            "Key=\"some long\ttext with\ttabs\"" to VariableAssignment("Key", "\"some long\ttext with\ttabs\""),
            "Key=\'some long\ttext with\ttabs\'" to VariableAssignment("Key", "\'some long\ttext with\ttabs\'"),
        ).mapIndexed { index, (userInput, expectedResult) ->
            dynamicTest("${index + 1}. $userInput") {
                val actualResult = CommandParserImpl().parse(userInput) as VariableAssignment
                 assertEquals(expectedResult, actualResult)
            }
        }

    @TestFactory
    fun `should parse pipeline`() =
        listOf(
            "echo _ | cat" to listOf("echo", "cat"),
            "echo _ _ | cat _ _ _ " to listOf("echo", "cat"),
            "echo | echo | echo" to listOf("echo", "echo", "echo"),
            "echo some text | echo another text | echo " to listOf("echo", "echo", "echo"),
        ).mapIndexed { index, (userInput, expectedCommands) ->
            dynamicTest("${index + 1}. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                val actualCommands = (parserResult as CommandSequenceTemplate).commands.map { it.name }
                assertEquals(expectedCommands, actualCommands)
            }
        }

    @TestFactory
    fun `should fail on the incorrect pipeline`() =
        listOf(
            "|",
            "||",
            " |\t|\t| ",
            "echo _ | cat | ",
            "| echo _ _ | cat _ _ _ ",
            "echo | echo || echo",
            " | echo some text | echo another text | echo | ",
        ).mapIndexed { index, userInput ->
            dynamicTest("${index + 1}. $userInput") {
                val parserResult = CommandParserImpl().parse(userInput)
                assertTrue(parserResult is ParseError)
            }
        }
}