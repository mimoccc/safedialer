import Constants.TASK_ASSEMBLE_RELEASE
import Constants.TASK_GROUP_MJDEV
import org.gradle.api.tasks.Exec
import java.io.File

@Suppress("DEPRECATION")
open class TaskSiteBuild : Exec() {
    private val rootDir: File
        get() = project.rootDir
    private val siteSourceDir: File
        get() = rootDir["site"]
    private val siteDestDir: File
        get() =
            rootDir["_site"].apply { mkdirs() }
    private val layoutsDir: File
        get() = siteSourceDir["_layouts"]
    private val apksDir: File
        get() = rootDir["composeApp"]["build"]["outputs"]["apk"]
    private val apksDirRelease: File
        get() = apksDir["release"]
    private val apksDirDebug: File
        get() = apksDir["debug"]
    private val jekyllCacheDir: File
        get() = siteSourceDir[".jekyll-cache"]
    private val appName: String
        get() = libs.versions.android.appName.stringValue

    init {
        group = TASK_GROUP_MJDEV
        dependsOnTask(TaskUpdateSiteData::class)
        dependsOn(TASK_ASSEMBLE_RELEASE)
        commandLine(
            "jekyll",
            "build",
            "-s",
            siteSourceDir.absolutePath,
            "-d",
            siteDestDir.absolutePath,
            "--layouts",
            layoutsDir.absolutePath,
        )
        doLast {
            siteDestDir["layouts"].deleteRecursively()
            apksDirDebug.listFiles { f ->
                f.name.endsWith(".apk")
            }?.forEach { f ->
                val dest = f.absolutePath
                    .replace("-unsigned", "")
                    .let { fname -> File(fname) }
                    .let { file ->
                        File(
                            file.parentFile,
                            file.name.replace("composeApp", appName)
                        )
                    }
                println("Renaming file: $f -> $dest")
                f.renameTo(dest)
            }
            apksDirRelease.listFiles { f ->
                f.name.endsWith(".apk")
            }?.forEach { f ->
                val dest = f.absolutePath
                    .replace("-unsigned", "")
                    .let { fname -> File(fname) }
                    .let { file ->
                        File(
                            file.parentFile,
                            file.name.replace("composeApp", appName)
                        )
                    }
                println("Renaming file: $f -> $dest")
                f.renameTo(dest)
            }
            jekyllCacheDir.deleteRecursively()
        }
    }
}
