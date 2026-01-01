import org.gradle.api.Project

@Suppress("unused")
class ProjectPlugin : BasePlugin() {

    open class ProjectPluginExtension() {
        lateinit var credentialsMap: SafeMap
        companion object {
            val name = "projectPlugin"
            val CREDENTIALS_FILE_NAME = "credentials"
        }
    }

    override fun Project.onApply() {
        val extension = project.extensions.create(
            ProjectPluginExtension.name,
            ProjectPluginExtension::class.java
        )
        extension.credentialsMap =
            readPropsFile(ProjectPluginExtension.CREDENTIALS_FILE_NAME)
    }

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

    companion object {
        @Suppress("USELESS_CAST")
        val Project.credentialsMap : SafeMap
            get() = extensions
                .getByType(ProjectPluginExtension::class.java)
                .credentialsMap as SafeMap
    }
}
