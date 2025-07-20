import org.gradle.api.Project

@Suppress("unused")
class ProjectPlugin : BasePlugin() {
    override fun Project.onConfigure() {
    }

    override fun Project.registerTasks() {
        registerTask<TaskSiteBuild>()
        registerTask<TaskDeleteTemporaryFiles>()
        registerTask<TaskUpdateSiteData>()
        registerTask<TaskGenerateChangelog>()
        registerTask<TaskGenerateScreenshots>()
    }

    override fun Project.onBeforeEvaluate() {
    }

    override fun Project.onAfterEvaluate() {
        buildTask {
            dependsOnTask(TaskSiteBuild::class)
        }
        assembleTask {
            dependsOnTask(TaskSiteBuild::class)
        }
        cleanTask {
            dependsOnTask(TaskDeleteTemporaryFiles::class)
        }
    }
}
