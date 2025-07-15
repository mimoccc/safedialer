package org.mjdev.safedialer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.components.FabState.Companion.rememberFabState

@Previews
@Composable
fun FloatButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Phone,
    fabState: FabState = rememberFabState(),
    onClick: () -> Unit = {},
) = AnimatedVisibility(
    modifier = modifier,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it }),
    visible = fabState.isVisible
) {
    FloatingActionButton(
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(),
        onClick = onClick,
    ) {
        Icon(icon, null)
    }
}

class FabState(
    visible: Boolean = true,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) {
    val fabVisibleState: MutableState<Boolean> = mutableStateOf(visible)
    var fabJob: Job? = null

    var isVisible
        get() = fabVisibleState.value
        set(value) {
            fabVisibleState.value = value
        }

    fun hideFabTemporarily(
        delayTime: Long = 500L
    ) {
        fabJob?.cancel()
        fabJob = scope.launch {
            isVisible = false
            delay(delayTime)
            isVisible = true
        }
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            hideFabTemporarily()
            return Offset.Zero
        }
    }

    companion object {
        @Composable
        fun rememberFabState(
            isVisible: Boolean = true,
            scope: CoroutineScope = rememberCoroutineScope(),
        ): FabState {
            return remember { FabState(isVisible, scope) }
        }
    }
}
