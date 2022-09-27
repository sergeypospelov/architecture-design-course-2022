package cli.io

/**
 * [ResultPrinter] prints result
 */
interface ResultPrinter {
    fun printResult(result: String)
}

/**
 * [ConsolePrinter] prints result to console
 */
class ConsolePrinter : ResultPrinter {
    override fun printResult(result: String) = println(result)
}