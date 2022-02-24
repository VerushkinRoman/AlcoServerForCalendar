package server

import org.jivesoftware.smack.packet.DefaultPacketExtension
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Packet
import util.Util

/**
 * XMPP Packet Extension for FCM Cloud Connection Server
 */
data class FcmPacketExtension(val json: String) : DefaultPacketExtension(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE) {

    override fun toXML(): String {
        // TODO: Do we need to scape the json? StringUtils.escapeForXML(json)
        return java.lang.String.format(
            "<%s xmlns=\"%s\">%s</%s>", Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE, json,
            Util.FCM_ELEMENT_NAME
        )
    }

    fun toPacket(): Packet {
        val message = Message()
        message.addExtension(this)
        return message
    }
}