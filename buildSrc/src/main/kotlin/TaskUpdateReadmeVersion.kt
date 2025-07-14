import org.gradle.api.DefaultTask

open class TaskUpdateReadmeVersion : DefaultTask() {
    init {
        group = "mjdev"
        onlyIfIsCI()
        doLast {
//            val version = project.libs.versions.android.versionName.stringValue
//            val readmeFile =
//                project.rootProject.rootDir
//                    .resolve("site")
//                    .resolve("index.md")
//            val content = readmeFile.readText()
//            val newContent = content.replace("%%VERSION%%", "v$version")
//            readmeFile.writeText(newContent)
        }
    }
}
