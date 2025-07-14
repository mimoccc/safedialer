
import org.gradle.api.Task

fun Task.onlyIfIsCI() = onlyIf { isCI }
