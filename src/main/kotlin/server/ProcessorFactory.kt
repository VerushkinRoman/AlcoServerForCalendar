package server

import service.PayloadProcessor
import service.impl.EchoProcessor
import service.impl.MessageProcessor
import service.impl.RegisterProcessor
import util.Util

/**
 * Manages the creation of different payload processors based on the desired
 * action
 */
object ProcessorFactory {
    fun getProcessor(action: String?): PayloadProcessor {
        checkNotNull(action) { "ProcessorFactory: Action must not be null! Options: 'REGISTER', 'ECHO', 'MESSAGE'" }
        return when (action) {
            Util.BACKEND_ACTION_REGISTER -> RegisterProcessor()
            Util.BACKEND_ACTION_ECHO -> EchoProcessor()
            Util.BACKEND_ACTION_MESSAGE -> MessageProcessor()
            else -> throw IllegalStateException("ProcessorFactory: Unknown action: $action. Options: 'REGISTER', 'ECHO', 'MESSAGE'")
        }
    }
}