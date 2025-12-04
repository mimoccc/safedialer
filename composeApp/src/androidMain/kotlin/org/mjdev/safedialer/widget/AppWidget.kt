package org.mjdev.safedialer.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.mjdev.safedialer.R
import org.mjdev.safedialer.activity.MainActivity

class AppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            MyContent()
        }
    }

    @Composable
    private fun MyContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Where to?",
                modifier = GlanceModifier.padding(12.dp)
            )
            Row(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    text = "Home",
                    onClick = actionStartActivity<MainActivity>()
                )
                Button(
                    text = "Work",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }

    @Composable
    fun AppWidgetComposable(
        modifier: Modifier = Modifier,
        context: Context = LocalContext.current,
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ) {
        Box(
            modifier = modifier.clickable {
                coroutineScope.launch {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = AppWidgetReceiver::class.java,
                        preview = AppWidget(),
                        previewState = DpSize(245.dp, 115.dp)
                    )
                }
            }
        ) {
            Text("Add widget")
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun test(context: Context) {
        AppWidgetManager.getInstance(context.applicationContext).setWidgetPreview(
            ComponentName(
                context.applicationContext,
                AppWidgetReceiver::class.java
            ),
            AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
            RemoteViews("com.example", R.layout.widget_preview)
        )
    }
}