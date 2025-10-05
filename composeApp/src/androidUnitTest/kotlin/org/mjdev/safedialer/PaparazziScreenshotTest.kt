package org.mjdev.safedialer

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_6_PRO
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.google.gson.GsonBuilder
import org.junit.Rule
import org.junit.Test
import org.kodein.di.compose.withDI
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.ui.screen.MainScreen
import org.mjdev.safedialer.ui.theme.AppTheme
import java.io.File

class PaparazziScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material.Dark.NoActionBar",
        showSystemUi = true
    )

    @Test
    fun generateScreenshots() {
        val di = mainDI(paparazzi.context)
        val rootDirPath = System.getProperty("project.rootDir")
        val screenshotsDir = File(rootDirPath, "screenshots")
        val outputJson = screenshotsDir.resolve("screenshots.json")
        val screenshots = mutableListOf<Map<String, String>>()
        val tabs: List<Enum<*>> = Tabs.entries.toMutableList().apply {
            val isServer = BuildConfig.SERVER.isNotEmpty()
            val isUser = BuildConfig.SERVER_UNAME.isNotEmpty()
            val isPass = BuildConfig.SERVER_UPASS.isNotEmpty()
            if (!(isServer && isUser && isPass)) remove(Tabs.Emails)
        }
        DeviceConfigs.entries.forEach { entry ->
            tabs.forEach { tab ->
                val tabName = tab.name
                val snapShotName = "${entry.name}_${tabName.lowercase()}"
                paparazzi.apply {
                    unsafeUpdateConfig(entry.config)
                }.snapshot(snapShotName) {
                    withDI(di) {
                        AppTheme {
                            MainScreen(startTab = tab as Tabs)
                        }
                    }
                }
                val fileName = "$snapShotName.png"
                screenshots.add(
                    mapOf(
                        "filename" to fileName,
                        "caption" to tabName,
                        "alt" to tabName
                    )
                )
            }
        }
        outputJson.parentFile?.mkdirs()
        val gson = GsonBuilder().setPrettyPrinting().create()
        outputJson.writeText(gson.toJson(screenshots))
        println("Exported screenshots JSON: ${outputJson.absolutePath}")
    }

    companion object {
        val deviceConfigDarkPortrait = PIXEL_6_PRO.copy(
            nightMode = NightMode.NIGHT,
            orientation = ScreenOrientation.PORTRAIT
        )
        val deviceConfigDarkLandscape = PIXEL_6_PRO.copy(
            nightMode = NightMode.NIGHT,
            orientation = ScreenOrientation.LANDSCAPE
        )
        val deviceConfigLightPortrait = PIXEL_6_PRO.copy(
            nightMode = NightMode.NOTNIGHT,
            orientation = ScreenOrientation.PORTRAIT
        )
        val deviceConfigLightLandscape = PIXEL_6_PRO.copy(
            nightMode = NightMode.NOTNIGHT,
            orientation = ScreenOrientation.LANDSCAPE
        )

        enum class DeviceConfigs(
            val config: DeviceConfig
        ) {
            DP(deviceConfigDarkPortrait),
            DL(deviceConfigDarkLandscape),
            LP(deviceConfigLightPortrait),
            LL(deviceConfigLightLandscape)
        }
    }
}
