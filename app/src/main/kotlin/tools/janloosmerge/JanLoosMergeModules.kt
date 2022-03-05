package tools.janloosmerge

import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Singleton
import mu.KotlinLogging
import net.lingala.zip4j.ZipFile
import picocli.CommandLine
import picocli.CommandLine.Command
import tools.client.PatreonClient
import tools.mapper
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines
import kotlin.io.path.writeText
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

@Singleton
@Command(
    name = "jl-merge",
    aliases = ["jlm"],
    description = ["Download and merge modules from Jan Loos patreon links"]
)
class JanLoosMergeModules(
    private val patreonClient: PatreonClient
) : Runnable {
    // Pipe Separated Delimiter file of format:
    // https://www.patreon.com/file?h=1&i=2|Name of the VTT Module
    private val dataExtractor by lazy { Regex("""https://www.patreon.com/file\?h=(.*)&i=(.*)\|(.*)$""") }

    @CommandLine.Option(
        names = ["-i", "--session-id"],
        description = ["A patreon session_id cookie value. Get this from logging in and inspecting the page. e.g. GB-bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb-aaaaaa"],
        required = true
    )
    lateinit var sessionId: String

    @CommandLine.Option(
        names = ["-f", "--module-data-file"],
        description = ["A file of pipe separated values of the form `https://www.patreon.com/file?h=1&i=2|Name of module 1`"],
        required = true
    )
    lateinit var dataFile: File

    @CommandLine.Option(
        names = ["-o", "--output-dir"],
        description = ["A directory to build the output compendium module."],
        required = true
    )
    lateinit var outDir: Path

    override fun run() {
        if (outDir.exists() && outDir.isRegularFile()) throw Exception("Output directory is a file. It should either not exist, or be a directory")
        if (!outDir.exists()) Files.createDirectories(outDir)
        val rootDir = outDir.resolve("janloos-all").also { it.createDirectories() }
        val modulesDir = rootDir.resolve("modules").also { it.createDirectories() }

        val lines = dataFile.readLines()
        val modules = lines.mapNotNull { line ->
            dataExtractor.find(line)?.destructured!!.let { (h, i, name) ->
                val file = patreonClient.getFile(h, i, "session_id=$sessionId")
                val fileAsLines = file.lines()
                val dropBoxUrl = fileAsLines.firstOrNull { it.contains("Manifest URL") }?.split(" ")?.last()
                if (dropBoxUrl == null) {
                    println("couldn't find dropbox url in:\n$file")
                    null
                } else {
                    logger.info { "Processing \"$name\" with url \"$dropBoxUrl\"" }
                    // now we can grab the data, it looks like "https://www.dropbox.com/s/somevalue/module.json?dl=1"
                    // and as it has no auth, we can simply use URL class directly.
                    val moduleJson = URL(dropBoxUrl).readText()
                    val module = mapper.readValue<VTTModule>(moduleJson).also {
                        val packs = it.packs
                        if (packs.size != 1) throw Exception("Cannot deal with module with multiple packs: $it")
                        val firstPack = packs.first()
                        if (firstPack.type == null) firstPack.type = firstPack.entity
                    }

                    logger.info { "Getting archive for ${module.name}" }
                    // get the data from the download value of the module
                    val downloadURL = module.download
                    if (downloadURL == null) {
                        logger.error { "No download path specified in module ${module.name}" }
                    } else {
                        val tmpExtract = Files.createTempFile("jl-temp", ".zip").also { it.deleteIfExists() }
                        URL(downloadURL).openStream().use { Files.copy(it, tmpExtract) }
                        val moduleDir = modulesDir.resolve(module.name)
                        ZipFile(tmpExtract.toFile()).extractAll(moduleDir.absolutePathString())

                        // remove the module.json file, it's no longer required as it's now part of a larger compendium
                        moduleDir.resolve("module.json").deleteIfExists()

                        // now fix the database file, prepending new module structure where needed
                        val dbPathEntry = getDbPathEntry(module)
                        val jsonDBFilePath = findPathIgnoringCase(moduleDir, dbPathEntry) ?: throw Exception("Could not find $dbPathEntry in archive")

                        // update the module's path value with the found file (relative to rootDir) in case there are issues with the name
                        // e.g. "/paths" when it's actually "/Paths" - I'm looking at you thugs and thieves!
                        val relativePath = rootDir.relativize(jsonDBFilePath)
                        module.packs.first().path = "./$relativePath"

                        val newDb = jsonDBFilePath.readLines().map { jsonLine ->
                            jsonLine.replace("modules/${module.name}", "modules/janloos-all/modules/${module.name}")
                        }
                        jsonDBFilePath.writeText(newDb.joinToString("\n"))
                        tmpExtract.deleteIfExists()
                    }

                    module
                }
            }
        }

        // Now we can convert the modules into a single compendium
        val packs: List<VTTModulePack> = createPacks(modules)

        val allModule = VTTModule(
            name = "janloos-all",
            title = "Jan Loos Combined Compendiums",
            description = "<p>A compendium combining all Jan Loos VTT compendiums into a single module by Mark Fisher</p>",
            version = "1.0.0",
            minimumCoreVersion = "0.5.0",
            compatibleCoreVersion = "9",
            author = "Jan Loos",
            url = "https://www.patreon.com/onlinetabletop/",
            packs = packs
        )

        val allModuleJson = mapper.writeValueAsString(allModule)
        val moduleFilePath = rootDir.resolve("module.json")
        moduleFilePath.writeText(allModuleJson)

        logger.info { "Completed downloading all data to $outDir" }

    }

    private fun getDbPathEntry(module: VTTModule): String {
        return module.packs.first().path.let { if (it.startsWith("/")) it.substring(1) else it }
    }

    private fun findPathIgnoringCase(moduleDir: Path, path: String): Path? {
        // the path value might be "/packs/janloos-thugsandthieves.db" but actually live under "/Packs/..."
        return Files.walk(moduleDir).toList().firstOrNull { it.absolutePathString().lowercase().contains(path.lowercase()) }
    }

    private fun createPacks(modules: List<VTTModule>): List<VTTModulePack> {
        return modules.map { module ->
            VTTModulePack(
                name = module.name,
                label = module.title,
                path = module.packs.first().path,
                entity = "Actor",
                type = "Actor"
            )
        }
    }

}
