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
            finalizedByTask(
                TaskGenerateChangelog::class,
                TaskUpdateSiteData::class,
                TaskGenerateScreenshots::class,
                TaskSiteBuild::class
            )
        }
        assembleTask {
            finalizedByTask(
                TaskGenerateChangelog::class,
                TaskUpdateSiteData::class,
                TaskGenerateScreenshots::class,
                TaskSiteBuild::class
            )
        }
        cleanTask {
            finalizedByTask(
                TaskDeleteTemporaryFiles::class
            )
        }
    }
}
