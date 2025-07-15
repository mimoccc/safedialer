import Constants.TASK_GROUP_MJDEV
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import kotlin.reflect.KClass

abstract class BasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            registerTasks()
            beforeEvaluate {
                onBeforeEvaluate()
            }
            afterEvaluate {
                onAfterEvaluate()
            }
            project.configure(listOf(project)) {
                onConfigure()
            }
        }
    }

    abstract fun Project.onConfigure()

    abstract fun Project.registerTasks()

    abstract fun Project.onBeforeEvaluate()

    abstract fun Project.onAfterEvaluate()

    companion object {
        val KClass<*>.taskName: String?
            get() = this.simpleName?.unCapitalize()

        fun String.unCapitalize(): String = replaceFirstChar(Char::lowercase)

        inline fun <reified T : Task> Project.registerTask(
            name: String? = null,
            group: String = TASK_GROUP_MJDEV,
            crossinline block: T.() -> Unit = {},
        ) = tasks
            .register(
                name ?: T::class.taskName
                    ?: throw(GradleException("No task name defined, please define task name")),
                T::class.java,
            ) {
                this.group = group
                block.invoke(this)
            }.get()
    }
}
