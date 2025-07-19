import Constants.TASK_GROUP_MJDEV
import org.gradle.api.DefaultTask

open class TaskDeleteTemporaryFiles : DefaultTask() {
    private val baseDirs
        get() =
            listOf(
                project.rootDir,
                project.file("composeApp"),
                project.file("site"),
                project.file("buildSrc"),
            )
    private val dirsToDelete =
        listOf(
            "build",
            ".jekyll-cache",
            "_site",
            ".kotlin",
            "screenshots",
        )

    init {
        group = TASK_GROUP_MJDEV
        doLast {
            baseDirs.forEach { bd ->
                dirsToDelete.forEach { dir ->
                    bd[dir].also { deleteDir ->
                        println("Deleting $deleteDir")
                        deleteDir.deleteRecursively()
                    }
                }
            }
        }
    }
}
