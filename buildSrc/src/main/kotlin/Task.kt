@file:Suppress("unused")

import BasePlugin.Companion.taskName
import org.gradle.api.Task
import kotlin.reflect.KClass

val Task.libs
    get() = project.libs

fun Task.onlyIfIsCI() = onlyIf { isCI }

fun Task.dependsOnTask(
    vararg classes: KClass<*>
): Task = dependsOn(classes.map { cls ->
    cls.taskName
})

fun Task.finalizedByTask(
    vararg classes: KClass<*>
): Task = finalizedBy(classes.map { cls ->
    cls.taskName
})
