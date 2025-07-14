import Constants.BUILD_TASK
import Constants.CLEAN_TASK
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException

val isCI
    get() =
        System
            .getenv("CI")
            .contentEquals("true", true)

@Throws(UnknownTaskException::class)
fun Project.cleanTask(configurationAction: Action<Task>) {
    tasks.named(CLEAN_TASK, configurationAction)
}

@Throws(UnknownTaskException::class)
fun Project.buildTask(configurationAction: Action<Task>) {
    tasks.named(BUILD_TASK, configurationAction)
}
