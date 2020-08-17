package com.cahum.mentor.control

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cahum.mentor.MenuActivity
import com.cahum.mentor.R
import com.cahum.mentor.modelo.Cliente
import com.cahum.mentor.modelo.Mentor
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val tag = "FirebaseMessagingService"
    private var mentor: Mentor? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    override fun onNewToken(token: String) {
        val usuario = FirebaseAuth.getInstance().currentUser?:return
        val ref = FirebaseDatabase.getInstance().getReference("/mentores/${usuario!!.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    mentor = dataSnapshot.getValue(Mentor::class.java)
                    mentor!!.tokenNotificacion = token
                    actualizarToken()
                }
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun actualizarToken() {
        val ref = FirebaseDatabase.getInstance().getReference("/mentores")
        ref.child(mentor!!.uid).setValue(mentor)
    }

    fun conseguirToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(tag, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token ?: return@OnCompleteListener
                // Log and toast
                onNewToken(token)
            })
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}