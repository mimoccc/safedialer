import Constants.TASK_ASSEMBLE
import Constants.TASK_BUILD
import Constants.TASK_CLEAN
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.kotlin.dsl.the

val isCI
    get() =
        System
            .getenv("CI")
            .contentEquals("true", true)

val Project.libs
    get() = the<LibrariesForLibs>()

@Throws(UnknownTaskException::class)
fun Project.cleanTask(configurationAction: Action<Task>) {
    tasks.filter { t ->
        t.name.contains (TASK_CLEAN)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

@Throws(UnknownTaskException::class)
fun Project.buildTask(configurationAction: Action<Task>) {
    tasks.filter { t ->
        t.name.contains (TASK_BUILD)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

@Throws(UnknownTaskException::class)
fun Project.assembleTask(configurationAction: Action<Task>) {
    tasks.filter { t ->
        t.name.contains (TASK_ASSEMBLE)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}
