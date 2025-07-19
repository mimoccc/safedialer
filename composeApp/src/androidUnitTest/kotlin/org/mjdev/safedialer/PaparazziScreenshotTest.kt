package org.mjdev.safedialer

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import org.kodein.di.LazyDI
import org.kodein.di.compose.withDI
import org.mjdev.safedialer.app.MainApp.Companion.mainDI
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.ui.screen.MainScreen
import org.mjdev.safedialer.ui.theme.AppTheme

class PaparazziScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun generateAllScreenshots() {
        val di: LazyDI = mainDI(paparazzi.context)
        Tabs.entries.forEach { tab ->
            paparazzi.snapshot(
                "mainscreen_${tab.name.lowercase()}"
            ) {
                withDI(di) {
                    AppTheme {
                        MainScreen(startTab = tab)
                    }
                }
            }
        }
    }
}
