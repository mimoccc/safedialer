package org.mjdev.safedialer.widget.base

import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = 400,
    heightDp = 800
)
annotation class GlancePreviewPortrait

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = 800,
    heightDp = 400
)
annotation class GlancePreviewLandscape

@GlancePreviewPortrait
@GlancePreviewLandscape
annotation class GlancePreviews