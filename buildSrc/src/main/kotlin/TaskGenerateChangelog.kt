import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import kotlin.collections.first
import kotlin.collections.getOrNull
import kotlin.collections.last
import kotlin.io.writeText
import kotlin.sequences.forEach
import kotlin.text.lineSequence
import kotlin.text.lowercase
import kotlin.text.split
import kotlin.text.startsWith
import kotlin.text.trim

open class TaskGenerateChangelog : DefaultTask() {
    @Input
    var appName: String = ""
    @Input
    var versionName: String = ""
    @Input
    var tagPrefix: String = ""
        get() = field.lowercase()

    private val buildType: String
        get() = if (tagPrefix == "development") "debug" else "release"
  
    @TaskAction
    fun generate() {
        val tagsCommand = listOf("git", "tag", "--sort=-committerdate")
        val tagsOutput = execAndGetOutput(tagsCommand)
        val tags = tagsOutput.split("\n")
        val currentVersion = tags.first()
        val previousVersion = tags.last()
        val changes = execAndGetOutput(
            listOf(
                "git",
                "log",
                "--pretty=format:%s <%an>",
                "$previousVersion..$currentVersion"
            )
        )
        println("Changes between $previousVersion and $currentVersion:\n$changes")
        var features = "Features:\n"
        var fixes = "Fixes:\n"
        var improvements = "Improvements:\n"
        changes.lineSequence().forEach { line ->
            val parts = line.split(":", limit = 2)
            val prefix = parts.getOrNull(0) ?: ""
            val rest = parts.getOrNull(1) ?: ""
            when {
                prefix.startsWith("ft") -> features += "$rest\n"
                prefix.startsWith("fi") -> fixes += "$rest\n"
                prefix.startsWith("im") -> improvements += "$rest\n"
            }
        }
        val header = "Changelog from $previousVersion to $currentVersion\n\n"
        val changelog = header + features + "\n" + fixes + "\n" + improvements
        val fileName = "${appName}-${versionName}-$tagPrefix"
        val changelogDir = project.file("./build/outputs/apk/$buildType")
        val changelogFilePath = "$changelogDir/CHANGELOG-$fileName.md"
        println("Changelog file path: $changelogFilePath")
        changelogDir.mkdirs()
        val changelogFile = project.file(changelogFilePath)
        changelogFile.createNewFile()
        changelogFile.writeText(changelog)
        println(changelog)
    }

    private fun execAndGetOutput(command: List<String>): String {
        val outputStream = ByteArrayOutputStream()
        project.exec {
            commandLine(command)
            standardOutput = outputStream
        }
        return String(outputStream.toByteArray(), Charsets.UTF_8).trim()
    }
}