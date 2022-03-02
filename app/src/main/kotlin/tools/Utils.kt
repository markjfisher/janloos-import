package tools

internal object Resources

fun readAsString(fileName: String): String {
    return String(Resources.javaClass.getResourceAsStream(fileName)!!.readBytes())
}