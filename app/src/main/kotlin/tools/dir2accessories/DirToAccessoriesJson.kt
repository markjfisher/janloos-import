package tools.dir2accessories

import jakarta.inject.Singleton
import mu.KotlinLogging
import picocli.CommandLine
import picocli.CommandLine.Command
import tools.mapper
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.writeText

private val logger = KotlinLogging.logger {}

@Singleton
@Command(
    name = "dir-2-accessories",
    aliases = ["d2a"],
    description = ["Directory to accessories json for tokenstein"]
)
class DirToAccessoriesJson(
) : Runnable {
    @CommandLine.Option(
        names = ["-d", "--image-dir"],
        description = ["A directory to find image files"],
        required = true
    )
    lateinit var imageDir: Path

    @CommandLine.Option(
        names = ["-o", "--output-dir"],
        description = ["A directory to write the json files to"],
        required = true
    )
    lateinit var outputDir: Path

    @CommandLine.Option(
        names = ["-f", "--exclude-filter"],
        description = ["Exclude any dirs in this comma separated list, default: 'swatches'"]
    )
    var excludeFilter: String = "swatches"

    override fun run() {
        val excludes = excludeFilter.split(",")
        // take an input dir, find all files and directories and create equivalent accessories json files for tokenstein
        val categories = mutableMapOf<String, MutableList<AccessoryEntry>>()
        Files.walk(imageDir).filter { it.isRegularFile() && it.parent != imageDir && it.parent.name !in excludes}.forEach { path ->
            val category = path.parent.name
            // logger.info { "got path: $path, category: $category"}
            val categoryEntry = categories.getOrDefault(category, mutableListOf())
            if (categoryEntry.isEmpty()) categories[category] = categoryEntry
            val fileName = path.name.substringBeforeLast(".")
            val accessoryEntry = when (val fileExtension = path.name.substringAfterLast(".")) {
                "png" -> AccessoryEntry(
                    name = fileName,
                    filename = path.name,
                    qty = 1,
                    id = fileName,
                    type = "img"
                )
                else -> throw UnsupportedOperationException("Can't process $fileExtension yet")
            }
            categoryEntry.add(accessoryEntry)
        }

        if (!outputDir.exists()) Files.createDirectories(outputDir)

        logger.info { "Writing json files to $outputDir" }
        categories.forEach { (name, entries) ->
            logger.info { "$name -> ${entries.map { it.name }}" }
            val outFile = outputDir.resolve("${name}.json")
            outFile.writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entries))
        }

    }

}
