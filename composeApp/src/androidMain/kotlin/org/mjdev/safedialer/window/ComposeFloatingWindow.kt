package org.mjdev.safedialer.window

import android.app.Activity
import android.app.Application
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardLock
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.core.view.isNotEmpty
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Suppress("unused", "DEPRECATION", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
class ComposeFloatingWindow(
    val context: Context,
    val windowParams: WindowManager.LayoutParams = defaultLayoutParams(context),
    val onShown: ComposeFloatingWindow.() -> Unit = {},
    val onHidden: ComposeFloatingWindow.() -> Unit = {},
    block: ComposeFloatingWindow.() -> Unit = {}
) : DIAware, SavedStateRegistryOwner, ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    override val di: DI by closestDI(context)

    val windowManager by instance<WindowManager>()

    val keyGuardManager by instance<KeyguardManager>()

    var lifecycleRegistry: LifecycleRegistry =
        LifecycleRegistry(this)

    var savedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.Companion.create(this)
    private var _showing = MutableStateFlow(false)

    val showing: StateFlow<Boolean>
        get() = _showing.asStateFlow()

    var decorView: ViewGroup = FrameLayout(context)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        SavedStateViewModelFactory(
            context.applicationContext as Application,
            this@ComposeFloatingWindow,
            null
        )
    }

    override val defaultViewModelCreationExtras: CreationExtras = MutableCreationExtras().apply {
        val application = context.applicationContext?.takeIf { it is Application }
        if (application != null) {
            set(
                ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY,
                application as Application
            )
        }
        set(SAVED_STATE_REGISTRY_OWNER_KEY, this@ComposeFloatingWindow)
        set(VIEW_MODEL_STORE_OWNER_KEY, this@ComposeFloatingWindow)
    }

    override val viewModelStore: ViewModelStore = ViewModelStore()

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        enableSavedStateHandles()
        block(this)
    }

    fun setContent(content: @Composable () -> Unit) {
        setContentView(ComposeView(context).apply {
            setContent {
                CompositionLocalProvider(
                    LocalFloatingWindow provides this@ComposeFloatingWindow
                ) {
                    content()
                }
            }
            setViewTreeLifecycleOwner(this@ComposeFloatingWindow)
            setViewTreeViewModelStoreOwner(this@ComposeFloatingWindow)
            setViewTreeSavedStateRegistryOwner(this@ComposeFloatingWindow)
        })
    }

    fun setContentView(view: View) {
        if (decorView.isNotEmpty()) {
            decorView.removeAllViews()
        }
        decorView.addView(view)
        update()
    }

    fun show() {
        if (isAvailable().not()) return
        require(decorView.isNotEmpty()) {
            "Content view cannot be empty"
        }
        if (_showing.value) {
            update()
            return
        }
        val keyguardLock: KeyguardLock = keyGuardManager.newKeyguardLock("mini")
        keyguardLock.disableKeyguard()
        decorView.getChildAt(0)?.takeIf {
            it is ComposeView
        }?.let { composeView ->
            val thread = AndroidUiDispatcher.Companion.CurrentThread
            val reComposer = Recomposer(thread)
            composeView.compositionContext = reComposer
            lifecycleScope.launch(thread) {
                reComposer.runRecomposeAndApplyChanges()
            }
        }
        if (decorView.parent != null) {
            windowManager.removeViewImmediate(decorView)
        }
        windowManager.addView(decorView, windowParams)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        _showing.update { true }
        onShown(this)
    }

    fun update() {
        if (!_showing.value) return
        windowManager.updateViewLayout(decorView, windowParams)
    }

    fun hide() {
        if (!_showing.value) return
        _showing.update { false }
        windowManager.removeViewImmediate(decorView)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        onHidden(this)
    }

    fun isAvailable(): Boolean =
        Settings.canDrawOverlays(context)

    companion object {

        const val ALERT_WINDOW_FLAGS: Int = (
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                )

        const val DEFAULT_WINDOW_FLAGS: Int = (
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                )

        fun defaultLayoutParams(
            context: Context,
            windowHeight: Int = WindowManager.LayoutParams.WRAP_CONTENT,
            windowWidth: Int = WindowManager.LayoutParams.WRAP_CONTENT,
            windowFlags: Int = DEFAULT_WINDOW_FLAGS,
            windowAnimationResId: Int = androidx.appcompat.R.style.Animation_AppCompat_Dialog,
            windowGravity: Int = Gravity.START or Gravity.TOP
        ) = WindowManager.LayoutParams().apply {
            height = windowHeight
            width = windowWidth
            format = PixelFormat.TRANSLUCENT
            gravity = windowGravity
            windowAnimations = windowAnimationResId
            flags = windowFlags
            if (context !is Activity) {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
            }
        }

        fun alertLayoutParams(
            context: Context,
            windowAnimationResId: Int = androidx.appcompat.R.style.Animation_AppCompat_Dialog,
            windowGravity: Int = Gravity.START or Gravity.TOP
        ) = defaultLayoutParams(
            context,
            windowHeight = WindowManager.LayoutParams.WRAP_CONTENT,
            windowWidth = WindowManager.LayoutParams.MATCH_PARENT,
            windowFlags = ALERT_WINDOW_FLAGS,
            windowAnimationResId = windowAnimationResId,
            windowGravity = windowGravity
        )

        fun Modifier.dragFloatingWindow(): Modifier = composed {
            val floatingWindow = LocalFloatingWindow.current
            val windowParams = remember { floatingWindow.windowParams }
            pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val w = floatingWindow.decorView.width
                    val h = floatingWindow.decorView.height
                    val f = Rect().also {
                        floatingWindow.decorView.getWindowVisibleDisplayFrame(it)
                    }
                    windowParams.x = (windowParams.x + dragAmount.x.toInt())
                        .coerceIn(0..(f.width() - w))
                    windowParams.y = (windowParams.y + dragAmount.y.toInt())
                        .coerceIn(0..(f.height() - h))
                    floatingWindow.update()
                }
            }
        }

        val LocalFloatingWindow: ProvidableCompositionLocal<ComposeFloatingWindow> =
            compositionLocalOf {
                noLocalProvidedFor("LocalFloatingWindow")
            }

        @Suppress("SameParameterValue")
        private fun noLocalProvidedFor(name: String): Nothing {
            error("CompositionLocal $name not present")
        }

    }
}



