package cli.io

interface ResultPrinter {
    fun printResult(result: String)
}

class ConsolePrinter : ResultPrinter {
    override fun printResult(result: String) {
        TODO("Not yet implemented")
    }
}