import org.gradle.api.DefaultTask

// todo: yaml file support and more variables
open class TaskUpdateSiteData : DefaultTask() {
    private val dataFile
        get() = project.rootProject.rootDir["site"]["_data"]["data.yml"]
    private val version
        get() = libs.versions.android.versionName.stringValue
    private val appName
        get() = libs.versions.android.appName.stringValue

    init {
        group = "mjdev"
        dependsOn("taskGenerateChangelog")
        dependsOn("taskGenerateScreenshots")
        doLast {
            val fileContent =
                StringBuilder()
                    .appendLine("version: $version")
                    .appendLine("appName: $appName")
                    .toString()
            dataFile.writeText(fileContent)
        }
    }
}
