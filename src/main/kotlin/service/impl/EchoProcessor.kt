package service.impl

import bean.CcsInMessage
import bean.CcsOutMessage
import server.CcsClient
import server.MessageHelper
import service.PayloadProcessor
import util.Util

/**
 * Handles an echo request
 */
class EchoProcessor : PayloadProcessor {
    override fun handleMessage(msg: CcsInMessage) {
        val client: CcsClient = CcsClient.instance
        val messageId: String = Util.getUniqueMessageId()
        val to: String = msg.from

        // Send the incoming message to the device that made the request
        val outMessage = CcsOutMessage(to, messageId, msg.dataPayload)
        val jsonRequest: String = MessageHelper.createJsonOutMessage(outMessage)
        client.send(jsonRequest)
    }
}