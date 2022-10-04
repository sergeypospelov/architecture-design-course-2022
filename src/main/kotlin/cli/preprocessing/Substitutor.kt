package cli.preprocessing

import cli.context.SessionContext

interface Substitutor {
    fun substitute(string: String): String
}

class SubstitutorImpl : Substitutor {

    override fun substitute(string: String): String {
        val result = StringBuilder()

        var openC: Char? = null
        var isVariable = false
        var variable = ""

        fun substitute() {
            if (variable.isNotEmpty()) {
                val value = SessionContext.variables.get(variable)
                result.append(value)
                isVariable = false
                variable = ""
            }
        }

        for (c in string) {
            if (isVariable && variable.canAppend(c)) {
                variable += c
                continue
            }
            substitute()
            if (openC != null) {
                if (c == '$' && openC == '\"') {
                    isVariable = true
                } else if (c == openC) {
                    openC = null
                    result.append(c)
                } else {
                    result.append(c)
                }
            } else {
                if (c == '$') {
                    isVariable = true
                } else if (c == '\'' || c == '\"') {
                    openC = c
                    result.append(c)
                } else {
                    result.append(c)
                }
            }
        }
        substitute() // last variable

        return result.toString()
    }

    private fun String.canAppend(ch: Char) =
        (ch.isLetter() or (ch == '_') or (ch.isDigit() && isNotEmpty()) or (ch == '?'))
}