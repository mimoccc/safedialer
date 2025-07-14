import Constants.TASK_GROUP_MJDEV
import org.gradle.api.DefaultTask

open class TaskDeleteTemporaryFiles : DefaultTask() {
    private val dirsToDelete =
        listOf(
            ".jekyll-cache",
            "_site",
            ".kotlin",
        )

    init {
        group = TASK_GROUP_MJDEV
        doLast {
            dirsToDelete.forEach { dir ->
                project.rootDir
                    .resolve(dir)
                    .deleteRecursively()
            }
        }
    }
}
