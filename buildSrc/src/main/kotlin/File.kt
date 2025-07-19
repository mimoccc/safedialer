import org.gradle.api.Project
import java.io.File

operator fun File.get(
    name: String
): File = resolve(name)
