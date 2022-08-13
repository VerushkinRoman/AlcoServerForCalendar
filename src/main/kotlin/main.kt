import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Window(
        onCloseRequest = { isVisible = false },
        state = rememberWindowState(size = DpSize(250.dp, 100.dp)),
        visible = isVisible,
        title = "AlcoServer",
        icon = painterResource("logo.png")
    ) {
        MaterialTheme {
            LaunchedEffect(true) { connect() }
            ButtonPanel(modifier = Modifier.fillMaxSize())
        }
    }

    Tray(
        icon = painterResource("logo.png"),
        tooltip = "AlcoServer",
        onAction = { isVisible = true },
        menu = {
            Item("Exit", onClick = ::exitApplication)
        },
    )
}

@Suppress("FunctionName")
@Composable
private fun ButtonPanel(modifier: Modifier) {

    val isRunning by CcsClient.instance.isConnected.collectAsState()

    Row(
        modifier = modifier
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isRunning) Color.Green else Color.White),
            border = if (isRunning) null else BorderStroke(Dp.Hairline, Color.Black),
            onClick = { if (!isRunning) connect() },
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
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            onClick = { disconnect() },
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

private fun disconnect() {
    try {
        CcsClient.instance.disconnect(isManualStop = true)
    } catch (e: XMPPException) {
        Logger.getLogger("Main").log(Level.SEVERE, "Error trying to disconnect.", e)
    }
}

private fun connect() {
    try {
        CcsClient.instance.connect()
    } catch (e: XMPPException) {
        Logger.getLogger("Main").log(Level.SEVERE, "Error trying to connect.", e)
    }
}