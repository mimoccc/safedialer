import Constants.TASK_ASSEMBLE
import Constants.TASK_BUILD
import Constants.TASK_CLEAN
import SafeMap.Companion.toSafeMap
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.kotlin.dsl.the
import kotlin.collections.getOrElse
import kotlin.reflect.KProperty

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
        t.name.contains(TASK_CLEAN)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

@Throws(UnknownTaskException::class)
fun Project.buildTask(configurationAction: Action<Task>) {
    tasks.filter { t ->
        t.name.contains(TASK_BUILD)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

@Throws(UnknownTaskException::class)
fun Project.assembleTask(configurationAction: Action<Task>) {
    tasks.filter { t ->
        t.name.contains(TASK_ASSEMBLE)
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

class SafeMap : HashMap<String, String>() {
    companion object {
        operator fun SafeMap.getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): String = getOrElse(property.name) { "" }

        @Suppress("UnusedReceiverParameter")
        fun List<Pair<String, String>>.toSafeMap() = SafeMap().apply {
            forEach { (k, v) -> put(k, v) }
        }
    }

    override operator fun get(
        key: String
    ): String = runCatching {
        get(key)
    }.getOrNull() ?: ""
}

fun Project.readPropsFile(
    fileName: String
): SafeMap = runCatching {
    rootDir.resolve(fileName)
        .readLines().mapNotNull { line ->
            runCatching {
                line.trim()
                    .split("=")
                    .let { ss ->
                        Pair(ss[0].trim(), ss[1].trim())
                    }
            }.getOrNull()
        }.toSafeMap()
}.getOrNull() ?: SafeMap()
