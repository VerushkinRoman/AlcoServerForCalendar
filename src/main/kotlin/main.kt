import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jivesoftware.smack.XMPPException
import server.CcsClient
import java.io.File
import java.io.InputStream
import java.util.logging.Level
import java.util.logging.Logger

fun main() = application {

    val inputStream: InputStream = File("AlcoServerData.txt").inputStream()
    val lineList = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    val fcmProjectSenderId = lineList.find { it.contains("AppID") }?.substringAfterLast(" ")
    val fcmServerKey = lineList.find { it.contains("ServerKey") }?.substringAfterLast(" ")
    inputStream.close()
    if (fcmProjectSenderId.isNullOrEmpty() || fcmServerKey.isNullOrEmpty()) throw RuntimeException("Should set AppID & ServerKey")

    CcsClient.prepareClient(fcmProjectSenderId, fcmServerKey, false)

    var isVisible by remember { mutableStateOf(true) }
    val isRunning = remember { mutableStateOf(false) }

    Window(
        onCloseRequest = { isVisible = false },
        state = rememberWindowState(size = DpSize(250.dp, 100.dp)),
        visible = isVisible,
        title = "AlcoServer",
        icon = painterResource("logo.png")
    ) { MaterialTheme { ButtonPanel(Modifier.fillMaxSize(), isRunning) } }

    Tray(
        icon = painterResource("logo.png"),
        tooltip = "AlcoServer",
        onAction = { isVisible = true },
        menu = {
            Item("Exit", onClick = ::exitApplication)
        },
    )

    Thread {
        runClient(isRunning)
    }.start()
}

@Preview
@Composable
fun ButtonPanel(modifier: Modifier, isRunning: MutableState<Boolean>) {
    Row(modifier = modifier) {
        val buttonModifier = Modifier
            .align(Alignment.CenterVertically)
            .weight(1f)
            .padding(5.dp)
            .fillMaxHeight()

        Button(modifier = buttonModifier,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isRunning.value) Color.Green else Color.White,
                contentColor = Color.Black,
            ),
            border = if (isRunning.value) null else BorderStroke(Dp.Hairline, Color.Black),
            onClick = {
                runClient(isRunning)
            }) {
            Text("Start")
        }
        Button(modifier = buttonModifier,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ),
            onClick = {
                stopClient(isRunning)
            }) {
            Text("Stop")
        }
    }
}

fun runClient(isRunning: MutableState<Boolean>) {
    try {
        CcsClient.instance.connect { isRunning.value = it }
    } catch (e: XMPPException) {
        Logger.getLogger("Main").log(Level.SEVERE, "Error trying to connect.", e)
        Thread.sleep(1_000)
        runClient(isRunning)
    }
}

fun stopClient(isRunning: MutableState<Boolean>) {
    try {
        CcsClient.instance.disconnect { isRunning.value = !it }
    } catch (e: XMPPException) {
        Logger.getLogger("Main").log(Level.SEVERE, "Error trying to disconnect.", e)
        Thread.sleep(1_000)
        stopClient(isRunning)
    }
}
