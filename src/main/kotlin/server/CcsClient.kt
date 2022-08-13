package server

import bean.CcsInMessage
import bean.CcsOutMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jivesoftware.smack.*
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode
import org.jivesoftware.smack.filter.PacketTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Packet
import org.jivesoftware.smack.packet.PacketExtension
import org.jivesoftware.smack.provider.PacketExtensionProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.json.simple.JSONValue
import org.json.simple.parser.ParseException
import org.xmlpull.v1.XmlPullParser
import service.PayloadProcessor
import util.Util
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.SSLSocketFactory

/**
 * Sample Smack implementation of a client for FCM Cloud Connection Server. Most
 * of it has been taken more or less verbatim from Google's documentation:
 * https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref
 */

@Suppress("unused")
class CcsClient private constructor() : PacketListener {
    private var connection: XMPPConnection? = null
    private var config: ConnectionConfiguration? = null
    private var mApiKey: String? = null
    private var mProjectId: String? = null
    private var mDebuggable = false
    private var fcmServerUsername: String? = null
    private var isManualStop: Boolean = false

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val listener: ConnectionListener = object : ConnectionListener {
        override fun reconnectionSuccessful() {
            logger.log(Level.INFO, "Reconnection successful ...")
            // TODO: handle the reconnecting successful
        }

        override fun reconnectionFailed(e: Exception) {
            logger.log(Level.INFO, "Reconnection failed: ", e.message)
            _isRunning.value = false
            Thread.sleep(1000)
            connect()
        }

        override fun reconnectingIn(seconds: Int) {
            logger.log(Level.INFO, "Reconnecting in %d secs", seconds)
            // TODO: handle the reconnecting in
        }

        override fun connectionClosedOnError(e: Exception) {
            logger.log(Level.INFO, "Connection closed on error")
            _isRunning.value = false
            Thread.sleep(1000)
            connect()
        }

        override fun connectionClosed() {
            logger.log(Level.INFO, "Connection closed")
            _isRunning.value = false
            if (!isManualStop) {
                Thread.sleep(1000)
                connect()
            }
        }
    }

    private constructor(projectId: String, apiKey: String, debuggable: Boolean) : this() {
        mApiKey = apiKey
        mProjectId = projectId
        mDebuggable = debuggable
        fcmServerUsername = mProjectId + "@" + Util.FCM_SERVER_CONNECTION
    }

    init {
        // Add FcmPacketExtension
        ProviderManager.getInstance().addExtensionProvider(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE,
            object : PacketExtensionProvider {
                @Throws(Exception::class)
                override fun parseExtension(parser: XmlPullParser): PacketExtension {
                    val json: String = parser.nextText()
                    return FcmPacketExtension(json)
                }
            })
    }

    /**
     * Connects to FCM Cloud Connection Server using the supplied credentials
     */
    @Throws(XMPPException::class)
    fun connect() {
        isManualStop = false
        connection?.removeConnectionListener(listener)
        config = ConnectionConfiguration(Util.FCM_SERVER, Util.FCM_PORT).apply {
            securityMode = SecurityMode.enabled
            isReconnectionAllowed = true
            isRosterLoadedAtLogin = false
            setSendPresence(false)
            socketFactory = SSLSocketFactory.getDefault()
            // Launch a window with info about packets sent and received
            isDebuggerEnabled = mDebuggable
        }
        connection = XMPPConnection(config)
        connection?.connect()
        connection?.addConnectionListener(listener)

        // Handle incoming packets (the class implements the PacketListener)
        connection?.addPacketListener(this, PacketTypeFilter(Message::class.java))

        // Log all outgoing packets
        connection?.addPacketInterceptor(
            { packet -> logger.log(Level.INFO, "Sent: {0}", packet.toXML()) },
            PacketTypeFilter(Message::class.java)
        )
        connection?.login(fcmServerUsername, mApiKey)
        _isRunning.value = true
        logger.log(Level.INFO, "Logged in: $fcmServerUsername")
    }

    fun disconnect(isManualStop: Boolean = false) {
        this.isManualStop = isManualStop
        connection?.disconnect()
        _isRunning.value = false
    }

//    fun reconnect() {
//        // Try to connect again using exponential back-off!
//    }

    /**
     * Handles incoming messages
     */
    override fun processPacket(packet: Packet) {
        logger.log(Level.INFO, "Received: " + packet.toXML())
        val incomingMessage = packet as Message
        val fcmPacket: FcmPacketExtension = incomingMessage.getExtension(Util.FCM_NAMESPACE) as FcmPacketExtension
        val json: String = fcmPacket.json
        try {
            @Suppress("UNCHECKED_CAST")
            val jsonMap = JSONValue.parseWithException(json) as Map<String, Any>
            val messageType = jsonMap["message_type"]
            if (messageType == null) {
                val inMessage: CcsInMessage = MessageHelper.createCcsInMessage(jsonMap)
                handleUpstreamMessage(inMessage) // normal upstream message
                return
            }
            when (messageType.toString()) {
                "ack" -> handleAckReceipt(jsonMap)
                "nack" -> handleNackReceipt(jsonMap)
                "receipt" -> handleDeliveryReceipt(jsonMap)
                "control" -> handleControlMessage(jsonMap)
                else -> logger.log(
                    Level.INFO,
                    "Received unknown FCM message type: $messageType"
                )
            }
        } catch (e: ParseException) {
            logger.log(Level.INFO, "Error parsing JSON: $json", e.message)
        }
    }

    /**
     * Handles an upstream message from a device client through FCM
     */
    private fun handleUpstreamMessage(inMessage: CcsInMessage) {
        val action: String? = inMessage.dataPayload[Util.PAYLOAD_ATTRIBUTE_ACTION]
        if (action != null) {
            val processor: PayloadProcessor = ProcessorFactory.getProcessor(action)
            processor.handleMessage(inMessage)
        }

        // Send ACK to FCM
        val ack: String = MessageHelper.createJsonAck(inMessage.from, inMessage.messageId)
        send(ack)
    }

    /**
     * Handles an ACK message from FCM
     */
    private fun handleAckReceipt(@Suppress("UNUSED_PARAMETER") jsonMap: Map<String, Any>) {
        // TODO: handle the ACK in the proper way
    }

    /**
     * Handles a NACK message from FCM
     */
    private fun handleNackReceipt(jsonMap: Map<String, Any>) {
        val errorCode = jsonMap["error"] as String?
        if (errorCode == null) {
            logger.log(Level.INFO, "Received null FCM Error Code")
            return
        }
        when (errorCode) {
            "INVALID_JSON" -> handleUnrecoverableFailure(jsonMap)
            "BAD_REGISTRATION" -> handleUnrecoverableFailure(jsonMap)
            "DEVICE_UNREGISTERED" -> handleUnrecoverableFailure(jsonMap)
            "BAD_ACK" -> handleUnrecoverableFailure(jsonMap)
            "SERVICE_UNAVAILABLE" -> handleServerFailure(jsonMap)
            "INTERNAL_SERVER_ERROR" -> handleServerFailure(jsonMap)
            "DEVICE_MESSAGE_RATE_EXCEEDED" -> handleUnrecoverableFailure(jsonMap)
            "TOPICS_MESSAGE_RATE_EXCEEDED" -> handleUnrecoverableFailure(jsonMap)
            "CONNECTION_DRAINING" -> handleConnectionDrainingFailure()
            else -> logger.log(
                Level.INFO,
                "Received unknown FCM Error Code: $errorCode"
            )
        }
    }

    /**
     * Handles a Delivery Receipt message from FCM (when a device confirms that
     * it received a particular message)
     */
    private fun handleDeliveryReceipt(@Suppress("UNUSED_PARAMETER") jsonMap: Map<String, Any>) {
        // TODO: handle the delivery receipt
    }

    /**
     * Handles a Control message from FCM
     */
    private fun handleControlMessage(jsonMap: Map<String, Any>) {
        // TODO: handle the control message
        val controlType = jsonMap["control_type"] as String?
        if (controlType == "CONNECTION_DRAINING") {
            handleConnectionDrainingFailure()
        } else {
            logger.log(
                Level.INFO,
                "Received unknown FCM Control message: $controlType"
            )
        }
    }

    private fun handleServerFailure(jsonMap: Map<String, Any>) {
        // TODO: Resend the message
        logger.log(Level.INFO, "Server error: " + jsonMap["error"] + " -> " + jsonMap["error_description"])
    }

    private fun handleUnrecoverableFailure(jsonMap: Map<String, Any>) {
        // TODO: handle the unrecoverable failure
        logger.log(
            Level.INFO,
            "Unrecoverable error: " + jsonMap["error"] + " -> " + jsonMap["error_description"]
        )
    }

    private fun handleConnectionDrainingFailure() {
        // TODO: handle the connection draining failure. Force reconnect?
        logger.log(Level.INFO, "FCM Connection is draining! Initiating reconnection ...")
    }

    /**
     * Sends a downstream message to FCM
     */
    fun send(jsonRequest: String?) {
        // TODO: Resend the message using exponential back-off!
        if (jsonRequest == null) return
        val request: Packet = FcmPacketExtension(jsonRequest).toPacket()
        connection?.sendPacket(request)
    }

    /**
     * Sends a message to multiple recipients (list). Kind of like the old HTTP
     * message with the list of regIds in the "registration_ids" field.
     */
    fun sendBroadcast(outMessage: CcsOutMessage?, recipients: List<String>) {
        if (outMessage == null) return
        val map: MutableMap<String?, Any?> = MessageHelper.createAttributeMap(outMessage).toMutableMap()
        for (toRegId in recipients) {
            val messageId: String = Util.getUniqueMessageId()
            map["message_id"] = messageId
            map["to"] = toRegId
            val jsonRequest: String = MessageHelper.createJsonMessage(map)
            send(jsonRequest)
        }
    }

    companion object {
        val logger: Logger = Logger.getLogger(CcsClient::class.java.name)
        private var sInstance: CcsClient? = null
        val instance: CcsClient
            get() {
                checkNotNull(sInstance) { "You have to prepare the client first" }
                return sInstance as CcsClient
            }

        fun prepareClient(projectId: String, apiKey: String, debuggable: Boolean): CcsClient? {
            synchronized(CcsClient::class.java) {
                if (sInstance == null) {
                    sInstance = CcsClient(projectId, apiKey, debuggable)
                }
            }
            return sInstance
        }
    }
}
