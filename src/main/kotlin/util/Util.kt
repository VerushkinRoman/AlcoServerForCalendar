package util

import java.util.*

/**
 * Util class for constants and generic methods
 */
object Util {
    // For the FCM connection
    const val FCM_SERVER = "fcm-xmpp.googleapis.com"
    const val FCM_PORT = 5236
    const val FCM_ELEMENT_NAME = "gcm"
    const val FCM_NAMESPACE = "google:mobile:data"
    const val FCM_SERVER_CONNECTION = "gcm.googleapis.com"

    // For the processor factory
    const val BACKEND_ACTION_REGISTER = "REGISTER"
    const val BACKEND_ACTION_ECHO = "ECHO"
    const val BACKEND_ACTION_MESSAGE = "MESSAGE"

    // For the app common payload message attributes (android - xmpp server)
    const val PAYLOAD_ATTRIBUTE_ACTION = "action"
    const val PAYLOAD_ATTRIBUTE_RECIPIENT = "recipient"

    /**
     * Returns a random message id to uniquely identify a message
     */
    fun getUniqueMessageId(): String = "m-" + UUID.randomUUID().toString()
}
