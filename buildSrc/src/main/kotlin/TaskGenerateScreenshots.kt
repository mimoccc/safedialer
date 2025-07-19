import Constants.TASK_GROUP_MJDEV
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

@Suppress("DEPRECATION")
open class TaskGenerateScreenshots : DefaultTask() {
    private val rootDir: File
        get() = project.rootDir
    private val targetDir: File
        get() = rootDir["screenshots"].apply { mkdirs() }
    private val siteDir: File
        get() = rootDir["_site"]["assets"]["screenshots"].apply { mkdirs() }
    private val paparazziReportsDir
        get() = rootDir["composeApp"]["build"]["reports"]["paparazzi"]["debug"]

    init {
        group = TASK_GROUP_MJDEV
        description = "Generate screenshots of MainScreen for all tabs"
        dependsOn("testDebugUnitTest")
    }

    @TaskAction
    fun generateScreenshots() {
        if (!paparazziReportsDir.exists()) {
            println("Paparazzi reports directory not found. Running tests first...")
            return
        }
        val runsDir = File(paparazziReportsDir, "runs")
        val latestRunFile = runsDir.listFiles()?.maxByOrNull { f -> f.lastModified() }
        if (latestRunFile == null) {
            println("No Paparazzi run metadata found")
            return
        }
        val runContent = latestRunFile.readText()
        val imagePattern = """"name":\s*"([^"]+)"[\s\S]*?"file":\s*"([^"]+)"""".toRegex()
        val matches = imagePattern.findAll(runContent)
        targetDir.mkdirs()
        var copiedCount = 0
        matches.forEach { match ->
            val screenshotName = match.groupValues[1]
            val imageFile = match.groupValues[2]
            val sourceFile = File(paparazziReportsDir, imageFile)
            val targetFile = File(targetDir, "$screenshotName.png")
            if (sourceFile.exists()) {
                sourceFile.copyTo(targetFile, overwrite = true)
                println("Copied $screenshotName.png")
                copiedCount++
            } else {
                println("Warning: Source file not found: $sourceFile")
            }
        }
        println("Successfully copied $copiedCount screenshots to $targetDir")
        targetDir.copyRecursively(siteDir, overwrite = true)
        println("Successfully copied $copiedCount screenshots to $siteDir")
    }
}
