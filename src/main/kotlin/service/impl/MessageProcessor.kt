package service.impl

import bean.CcsInMessage
import bean.CcsOutMessage
import server.CcsClient
import server.MessageHelper
import service.PayloadProcessor
import util.Util

/**
 * Handles an upstream message request
 */
class MessageProcessor : PayloadProcessor {
    override fun handleMessage(msg: CcsInMessage) {
        val client: CcsClient = CcsClient.instance
        val messageId: String = Util.getUniqueMessageId()
        val to: String? = msg.dataPayload[Util.PAYLOAD_ATTRIBUTE_RECIPIENT]

        to?.let {
            val messageToSend: MutableMap<String, String> = msg.dataPayload.toMutableMap()
            messageToSend.remove(Util.PAYLOAD_ATTRIBUTE_RECIPIENT)
            messageToSend.remove(Util.PAYLOAD_ATTRIBUTE_ACTION)
            val outMessage = CcsOutMessage(it, messageId, messageToSend)
            val jsonRequest: String = MessageHelper.createJsonOutMessage(outMessage)
            client.send(jsonRequest)
        }
    }
}