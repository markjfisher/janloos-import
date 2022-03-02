package tools

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import picocli.CommandLine

// see https://github.com/micronaut-projects/micronaut-picocli/blob/master/picocli/src/test/java/io/micronaut/configuration/picocli/MicronautFactoryCommandTest.java
// for detailed examples of testing micronaut cli applications

class ApplicationTest {
    @Test
    fun `should be able to run the application command without args but it does nothing`() {
        val cmd = CommandLine(Application::class.java, createFactory())
        val application = cmd.getCommand<Application>()
        assertThat(application).isNotNull
    }
}