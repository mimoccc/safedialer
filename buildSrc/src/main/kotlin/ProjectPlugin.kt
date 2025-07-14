import org.gradle.api.Project

class ProjectPlugin : BasePlugin() {
    override fun Project.onConfigure() {
    }

    override fun Project.registerTasks() {
        registerTask<TaskJekyllBuild>()
        registerTask<TaskDeleteTemporaryFiles>()
        registerTask<TaskUpdateReadmeVersion>()
    }

    override fun Project.onBeforeEvaluate() {
    }

    override fun Project.onAfterEvaluate() {
        buildTask {
            dependsOn(
                TaskUpdateReadmeVersion::class.taskName,
                TaskJekyllBuild::class.taskName,
            )
        }
        cleanTask {
            dependsOn(
                TaskDeleteTemporaryFiles::class.taskName,
            )
        }
    }
}
