import Constants.TASK_ASSEMBLE
import Constants.TASK_BUILD
import Constants.TASK_CLEAN
import Constants.TASK_RELEASE
import SafeMap.Companion.toSafeMap
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.kotlin.dsl.the
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
fun Project.buildTask(
    releaseOnly: Boolean = true,
    configurationAction: Action<Task>
) {
    tasks.filter { t ->
        t.name.contains(TASK_BUILD, true).let { cts ->
            (if (releaseOnly) t.name.contains(TASK_RELEASE, true) else true) && cts
        }
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

@Throws(UnknownTaskException::class)
fun Project.assembleTask(
    releaseOnly: Boolean = true,
    configurationAction: Action<Task>
) {
    tasks.filter { t ->
        t.name.contains(TASK_ASSEMBLE).let { cts ->
            (if (releaseOnly) t.name.contains(TASK_RELEASE, true) else true) && cts
        }
    }.forEach { t ->
        tasks.named(t.name, configurationAction)
    }
}

class SafeMap : HashMap<String, String>() {
    companion object {
        @Suppress("USELESS_ELVIS")
        operator fun SafeMap.getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): String = get(property.name) ?: ""

        fun List<Pair<String, String>>.toSafeMap() = SafeMap().apply {
            this@toSafeMap.forEach { (key, value) -> put(key, value) }
        }
    }

    override operator fun get(
        key: String
    ): String = runCatching { super.get(key) }.getOrNull() ?: ""
}

fun Project.readPropsFile(
    relativePath: String
): SafeMap = runCatching {
    val file = rootDir.resolve(relativePath)
    println("Reading props file: $file")
    file.readLines().mapNotNull { line ->
        runCatching {
            line.trim().split("=").let { ss ->
                if (ss.size == 2) {
                    Pair(ss[0].trim(), ss[1].trim())
                } else null
            }
        }.onFailure { e ->
            println("e: $e")
        }.getOrNull()
    }.toSafeMap().apply {
        forEach { p ->
            val key = p.key
            val value = if (key.contains("pass", true)) "******" else p.value
            println("$key = $value")
        }
    }
}.getOrNull() ?: SafeMap()
