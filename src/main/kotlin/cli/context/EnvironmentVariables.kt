package cli.context

class EnvironmentVariables {

    private val storage: MutableMap<String, String> = mutableMapOf(
        "?" to "0"
    )

    fun set(variable: String, value: String) {
        storage[variable] = value
    }

    fun get(variable: String): String =
        storage[variable] ?: ""

    fun getAll(): Map<String, String> =
        storage
}