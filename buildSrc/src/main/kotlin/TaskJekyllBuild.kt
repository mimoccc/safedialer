import Constants.TASK_GROUP_MJDEV
import org.gradle.api.tasks.Exec
import java.io.File

@Suppress("DEPRECATION")
open class TaskJekyllBuild : Exec() {
    private val rootDir: File?
        get() = project.rootDir
    private val buildDir: File?
        get() = project.buildDir
    private val siteSourceDir: File?
        get() = rootDir["site"]
    private val siteDestDir: File?
        get() =
            rootDir["_site"].apply { this?.mkdirs() }
    private val layoutsDir: File?
        get() = siteSourceDir["layouts"]
    private val apksDir: File?
        get() = buildDir["outputs"]["apk"]
    private val apksDirRelease: File?
        get() = apksDir["release"]
    private val apksDirDebug: File?
        get() = apksDir["debug"]
    private val siteDestAssetsDir: File?
        get() = siteDestDir["assets"].apply { this?.mkdirs() }
    private val siteDownloadsDir: File?
        get() = siteDestAssetsDir["downloads"].apply { this?.mkdirs() }

    init {
        group = TASK_GROUP_MJDEV
        commandLine(
            "jekyll",
            "build",
            "-s",
            siteSourceDir?.absolutePath,
            "-d",
            siteDestDir?.absolutePath,
            "--layouts",
            layoutsDir?.absolutePath,
        )
        doLast {
            siteDestDir["layouts"]?.deleteRecursively()
            apksDirRelease
                ?.listFiles { f ->
                    f.name.endsWith(".apk")
                }?.forEach { f ->
                    val dest = siteDownloadsDir[f.name]
                    println("Copying file: $f -> $dest")
                    if (dest != null) {
                        f.copyTo(dest, true)
                    }
                }
        }
    }
}
