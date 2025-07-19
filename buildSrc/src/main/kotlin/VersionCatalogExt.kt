import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.catalogs: VersionCatalog
    get() = getCatalog()

fun Project.getCatalog(
    name: String = "libs"
): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named(name)

fun VersionCatalog.valueString(
    name: String
): String = findVersion(name).get().requiredVersion

fun VersionCatalog.valueInt(
    name: String
): Int = findVersion(name).get().requiredVersion.toInt()