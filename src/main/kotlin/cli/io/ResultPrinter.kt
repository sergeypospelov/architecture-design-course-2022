package cli.io

interface ResultPrinter {
    fun printResult(result: String)
}

class ConsolePrinter : ResultPrinter {
    override fun printResult(result: String) = println(result)
}