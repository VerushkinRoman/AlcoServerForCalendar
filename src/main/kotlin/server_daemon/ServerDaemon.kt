package server_daemon

import kotlinx.coroutines.*

class ServerDaemon(
    private val isServerRunning: () -> Boolean,
    private val startServer: () -> Unit
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitoringJob: Job? = null

    fun startMonitoring() {
        stopMonitoring()
        monitoringJob = scope.launch {
            while (true) {
                delay(60_000)
                if (!isServerRunning()) startServer()
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
    }
}