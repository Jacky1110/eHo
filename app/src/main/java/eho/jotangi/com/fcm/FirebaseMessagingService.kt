package eho.jotangi.com.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.e("From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Timber.e("Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Timber.e("Refreshed token: $token")
    }
}
