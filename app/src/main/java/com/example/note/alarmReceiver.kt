package com.example.note

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class alarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context, p1: Intent) {
        val db = DB(p0)
        val id = p1.getIntExtra("ID", 0)
        val note_id = p1.getIntExtra("note_id", 0)
        var tmpstring = ""
        val tmpproject = db.getproject(id)
        val tmpnote = db.get_note(id, note_id)

        if(tmpproject.need_password == 1){
            tmpstring = tmpproject.name.toString()
        }
        else{
            tmpstring = tmpproject.name + " " + tmpnote.title
        }
        val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("notice", "notice", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(p0, "notice")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("提醒")
                .setStyle(NotificationCompat.BigTextStyle().bigText("您設定的 $tmpstring 的時間到了"))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .build()

        manager.notify(note_id, notification)
        db.update_after_alarm(id, note_id)
    }
}