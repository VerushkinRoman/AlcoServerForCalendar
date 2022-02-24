package bean

/**
 * Represents an outgoing message to FCM CCS
 */
data class CcsOutMessage(
    var to: String, // Sender registration ID
    var messageId: String, // Unique id for this message
    var dataPayload: Map<String, String>, // Payload data. A String in JSON format
    var condition: String? = null, // Condition that determines the message target
    var collapseKey: String? = null, // Identifies a group of messages
    var priority: String? = null, // Priority of the message
    var isContentAvailable: Boolean? = null, // Flag to wake client devices
    var timeToLive: Int? = null, // Time to live
    var isDeliveryReceiptRequested: Boolean? = null, // Flag to request confirmation of message delivery
    var isDryRun: Boolean? = null, // Test request without sending a message
    var notificationPayload: Map<String, String>? = null, // Payload notification. A String in JSON format
)