package org.mjdev.safedialer.service.command

enum class ServiceCommand{
    ShowAlert,
    HideAlert,
    Restart,
    Start,
    Stop;

    companion object {
        operator fun invoke(command: String): ServiceCommand? {
            return entries.find { it.name.equals(command, ignoreCase = true) }
        }
    }
}
