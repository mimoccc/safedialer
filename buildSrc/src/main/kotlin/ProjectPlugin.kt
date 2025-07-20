import org.gradle.api.Project

class ProjectPlugin : BasePlugin() {
    override fun Project.onConfigure() {
    }

    override fun Project.registerTasks() {
        registerTask<TaskSiteBuild>()
        registerTask<TaskDeleteTemporaryFiles>()
        registerTask<TaskUpdateSiteData>()
        registerTask<TaskGenerateChangelog>()
    }

    override fun Project.onBeforeEvaluate() {
    }

    override fun Project.onAfterEvaluate() {
        buildTask {
            dependsOnTask(
                TaskSiteBuild::class,
                TaskGenerateChangelog::class
            )
        }
        assembleTask {
            dependsOnTask(
                TaskSiteBuild::class,
                TaskGenerateChangelog::class
            )
        }
        cleanTask {
            dependsOnTask(
                TaskDeleteTemporaryFiles::class
            )
        }
    }
}
