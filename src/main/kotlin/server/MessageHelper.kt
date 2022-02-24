package server

import bean.CcsInMessage
import bean.CcsOutMessage
import org.json.simple.JSONValue


/**
 * Helper for the transformation of JSON messages to attribute maps and vice
 * versa in the XMPP Server
 */
object MessageHelper {
    /**
     * Creates a JSON from an FCM outgoing message attributes
     */
    fun createJsonOutMessage(outMessage: CcsOutMessage): String {
        return createJsonMessage(createAttributeMap(outMessage))
    }

    /**
     * Creates a JSON encoded ACK message for a received upstream message
     */
    fun createJsonAck(to: String?, messageId: String?): String {
        val map: MutableMap<String?, Any?> = HashMap()
        map["message_type"] = "ack"
        map["to"] = to
        map["message_id"] = messageId
        return createJsonMessage(map)
    }

    fun createJsonMessage(jsonMap: Map<String?, Any?>?): String {
        return JSONValue.toJSONString(jsonMap)
    }

    /**
     * Creates a MAP from an FCM outgoing message attributes
     */
    fun createAttributeMap(msg: CcsOutMessage): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = HashMap()
        map["to"] = msg.to
        map["message_id"] = msg.messageId
        map["data"] = msg.dataPayload
        msg.condition?.let { map["condition"] = it }
        msg.collapseKey?.let { map["collapse_key"] = it }
        msg.priority?.let { map["priority"] = it }
        msg.isContentAvailable?.let { map["content_available"] = true }
        msg.timeToLive?.let { map["time_to_live"] = it }
        msg.isDeliveryReceiptRequested?.let { map["delivery_receipt_requested"] = true }
        msg.isDryRun?.let { map["dry_run"] = true }
        msg.notificationPayload.let { map["notification"] = it }
        return map
    }

    /**
     * Creates an incoming message according the bean
     */
    fun createCcsInMessage(jsonMap: Map<String, Any>): CcsInMessage {
        val from = jsonMap["from"].toString()
        // Package name of the application that sent this message
        val category = jsonMap["category"].toString()
        // Unique id of this message
        val messageId = jsonMap["message_id"].toString()
        val dataPayload = jsonMap["data"] as Map<String, String>
        return CcsInMessage(from, category, messageId, dataPayload)
    }
}