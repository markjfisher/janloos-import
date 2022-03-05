package tools.janloosmerge

data class VTTModule(
    val name: String,
    val title: String,
    val description: String,
    val version: String,
    val minimumCoreVersion: String,
    val compatibleCoreVersion: String,
    val author: String,
    val packs: List<VTTModulePack>,
    val url: String,
    val manifest: String? = null,
    val download: String? = null
)

data class VTTModulePack(
    val name: String,
    val label: String,
    var path: String,
    val entity: String,
    var type: String?
)