package cli.context


/**
 * Class for environmental variables storage
 */
class EnvironmentVariables {

    private val storage: MutableMap<String, String> = mutableMapOf(
        "?" to "0"
    )

    /**
     * add new variable with name [variable] and value [value] to storage
     */
    fun set(variable: String, value: String) {
        storage[variable] = value
    }

    /**
     * @return variable value by name (empty if no such variable)
     */
    fun get(variable: String): String =
        storage[variable] ?: ""

    /**
     * @return map from variable names to their values
     */
    fun getAll(): Map<String, String> =
        storage
}