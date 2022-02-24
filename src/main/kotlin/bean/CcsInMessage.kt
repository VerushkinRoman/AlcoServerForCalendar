package bean

/**
 * Represents an incoming message from FCM CCS
 */
data class CcsInMessage(
    var from: String, // Sender registration ID
    var category: String, // Sender app's package
    var messageId: String,  // Unique id for this message
    var dataPayload: Map<String, String> // Payload data. A String in JSON format
)