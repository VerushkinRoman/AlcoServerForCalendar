package presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Image
import kotlin.math.roundToInt

@Suppress("FunctionName")
@Composable
fun ApplicationScope.ServerGUI(
    connect: () -> Unit,
    disconnect: () -> Unit,
    isRunning: () -> Boolean
) {
    var isVisible by remember { mutableStateOf(true) }


    Window(
        onCloseRequest = { isVisible = false },
        state = rememberWindowState(size = DpSize(250.dp, 100.dp)),
        visible = isVisible,
        title = "AlcoServer",
        icon = painterResource("logo.png")
    ) {
        MaterialTheme {
            ButtonPanel(
                connect = connect,
                disconnect = disconnect,
                isRunning = isRunning,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    val trayIcon = remember(isRunning()) { getTrayIcon(isRunning()) }
    Tray(
        icon = trayIcon,
        tooltip = "AlcoServer",
        onAction = { isVisible = true },
        menu = { Item("Exit", onClick = ::exitApplication) }
    )
}

@Suppress("FunctionName")
@Composable
private fun ButtonPanel(
    connect: () -> Unit,
    disconnect: () -> Unit,
    isRunning: () -> Boolean,
    modifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                disabledBackgroundColor = Color.Green
            ),
            border = if (isRunning()) null else BorderStroke(Dp.Hairline, Color.Black),
            enabled = !isRunning(),
            onClick = connect,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 30.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                disabledBackgroundColor = Color.White
            ),
            border = if (isRunning()) null else BorderStroke(Dp.Hairline, Color.Black),
            enabled = isRunning(),
            onClick = disconnect,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            Text(
                text = "Stop",
                fontSize = 30.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
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