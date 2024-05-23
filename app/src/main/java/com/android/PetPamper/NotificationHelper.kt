import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.PetPamper.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "your_channel_id"  // Consistent Channel ID
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "My channel name"
            val descriptionText = "My channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this drawable resource exists
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            with(NotificationManagerCompat.from(context)) {
                // Generate a unique ID for each notification
                val notificationId = System.currentTimeMillis().toInt()
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle the security exception or log it
            Log.e("NotificationHelper", "Security Exception: Unable to send notification", e)
        }
    }

}
