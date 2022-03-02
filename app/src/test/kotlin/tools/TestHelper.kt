package tools

import io.micronaut.configuration.picocli.MicronautFactory
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import picocli.CommandLine

fun createFactory(propertySource: PropertySource? = null, environments: List<String> = emptyList()): MicronautFactory {
    val applicationContext: ApplicationContext = if (propertySource == null) ApplicationContext.run(*environments.toTypedArray()) else ApplicationContext.run(propertySource, *environments.toTypedArray())
    return MicronautFactory(applicationContext)
}

fun CommandLine.toUserObject(args: List<String>): Any? = parseArgs(*args.toTypedArray()).subcommand().commandSpec().userObject()
