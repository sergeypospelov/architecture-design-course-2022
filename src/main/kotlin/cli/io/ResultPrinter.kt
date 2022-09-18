package cli.io

interface ResultPrinter {
    fun printResult(result: String)
}

class ConsolePrinter : ResultPrinter {
    override fun printResult(result: String) = print(result) // TODO: unify '\n' in commands
}