package tools

import io.micronaut.configuration.picocli.PicocliRunner
import org.slf4j.bridge.SLF4JBridgeHandler
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.ScopeType
import tools.download.DownloadData

@Command(
    name = "tools",
    description = ["A project for doing jan loos stuff"],
    mixinStandardHelpOptions = true,
    scope = ScopeType.LOCAL,
    subcommands = [
        DownloadData::class
    ]
)
open class Application : Runnable {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SLF4JBridgeHandler.removeHandlersForRootLogger()
            SLF4JBridgeHandler.install()
            PicocliRunner.run(Application::class.java, *args)
        }
    }

    // everything is done in subcommands, but we need a run() method and for the class to be Runnable in order for PiccliRunner above to work.
    override fun run() {}
}
