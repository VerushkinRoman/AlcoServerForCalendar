import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Image
import ru.alcoserver.verushkinrg.common.core.PlatformSDK
import ru.alcoserver.verushkinrg.notificationService.compose.ServiceScreen
import kotlin.math.roundToInt

fun main() = application {
    PlatformSDK.init()

    var isVisible by remember { mutableStateOf(true) }
    var isRunning by remember { mutableStateOf(false) }

    Window(
        title = "AlcoServer",
        state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
        resizable = false,
        visible = isVisible,
        onCloseRequest = { isVisible = false },
        icon = painterResource("logo.png")
    ) {
        ServiceScreen(
            onStateChange = { isRunning = it },
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
        )
    }

    val trayIcon = remember(isRunning) { getTrayIcon(isRunning) }
    Tray(
        icon = trayIcon,
        tooltip = "AlcoServer",
        onAction = { isVisible = true },
        menu = { Item("Exit", onClick = ::exitApplication) }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
private fun getTrayIcon(isRunning: Boolean): Painter {
    val image = ResourceLoader.Default.load("logo.png")
        .use { Image.makeFromEncoded(it.readAllBytes()).toComposeImageBitmap() }

    return object : Painter() {
        override val intrinsicSize: Size = Size.Unspecified

        override fun DrawScope.onDraw() {
            drawImage(
                image,
                IntOffset.Zero,
                IntSize(image.width, image.height),
                dstSize = IntSize(
                    this@onDraw.size.width.roundToInt(),
                    this@onDraw.size.height.roundToInt()
                ),
                alpha = 1.0f,
                colorFilter = if (isRunning) ColorFilter.tint(Color.Green) else null,
                filterQuality = FilterQuality.Low
            )
        }
    }
}
