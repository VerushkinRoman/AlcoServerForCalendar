import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.application
import org.jivesoftware.smack.XMPPException
import presentation.ServerGUI
import server.CcsClient
import server_daemon.ServerDaemon
import java.io.File
import java.io.InputStream
import java.util.logging.Level
import java.util.logging.Logger

fun main() = application {

    val ccsClient = prepareCcsClient()
    val isServerRunning by ccsClient.isConnected.collectAsState()
    val serverDaemon = ServerDaemon(
        isServerRunning = { isServerRunning },
        startServer = { connect() }
    )

    ServerGUI(
        connect = {
            serverDaemon.startMonitoring()
            connect()
        },
        disconnect = {
            serverDaemon.stopMonitoring()
            disconnect()
        },
        isRunning = { isServerRunning }
    )

    connect()
}

private fun prepareCcsClient(): CcsClient {
    val inputStream: InputStream = File("AlcoServerData.txt").inputStream()
    val lineList = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    val fcmProjectSenderId = lineList.find { it.contains("AppID") }?.substringAfterLast(" ")
    val fcmServerKey = lineList.find { it.contains("ServerKey") }?.substringAfterLast(" ")
    inputStream.close()
    if (fcmProjectSenderId.isNullOrEmpty() || fcmServerKey.isNullOrEmpty()) throw RuntimeException("Should set AppID & ServerKey")

    CcsClient.prepareClient(fcmProjectSenderId, fcmServerKey, false)

    return CcsClient.instance
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