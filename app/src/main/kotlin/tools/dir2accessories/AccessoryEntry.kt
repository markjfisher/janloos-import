package tools.dir2accessories

data class AccessoryEntry(
    val name: String,
    val filename: String,
    val qty: Int? = null,
    val id: String? = null,
    val type: String? = null,
    val noflip: String? = null
)
