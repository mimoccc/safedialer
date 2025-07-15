import org.gradle.api.DefaultTask

// todo: yaml file support and more variables
open class TaskUpdateSiteData : DefaultTask() {
    private val dataFile
        get() = project.rootProject.rootDir["site"]["_data"]["data.yml"]
    private val version
        get() = libs.versions.android.versionName.stringValue

    init {
        group = "mjdev"
        doLast {
            val fileContent =
                StringBuilder()
                    .appendLine("version: $version")
                    .toString()
            dataFile.writeText(fileContent)
        }
    }
}
