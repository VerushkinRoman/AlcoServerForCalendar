package service.impl

import bean.CcsInMessage
import service.PayloadProcessor

/**
 * Handles a user registration request
 */
class RegisterProcessor : PayloadProcessor {
    override fun handleMessage(msg: CcsInMessage) {
        // TODO: handle the user registration. Keep in mind that a user name can
        // have more reg IDs associated. The messages IDs should be unique.
    }
}