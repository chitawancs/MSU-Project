package com.example.care

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import android.os.Vibrator
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){
    companion object {
        lateinit var  notificationManager: NotificationManager
        lateinit var  notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        private  val channelId = "com.example.care"
        private val descriptor = "Test"
        lateinit var dbHandler2: DBHandler
        val NOTIFI_TABLE_NAME = "notifi"
        val COLUMN_BINK = "bink"
        val db = FirebaseFirestore.getInstance()
    }
    var trun : Int = 0
    var trun2 : Int = 0
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHandler2 = DBHandler(this, null, null, 1)
        btnMain.setImageResource(R.drawable.b1)
        val deviceID = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
        textView7.setText("Android ID : "+deviceID)

        btnMain.setOnClickListener {
            btnMain.setImageResource(R.drawable.b2)
            val intent_mainpage = Intent(this, ListActivity::class.java)
            startActivity(intent_mainpage)
        }
        btnSet.setOnClickListener {
            btnSet.setImageResource(R.drawable.s2)
            val intent_mainpage = Intent(this, regActivity::class.java)
            startActivity(intent_mainpage)
        }
        btnEx.setOnClickListener {
            btnEx.setImageResource(R.drawable.e2)
            finish()
        }
        val addRef = db.collection("users")
        addRef.addSnapshotListener { snapshots, e ->

            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }
            if (trun2 == 0) {
                trun2 = trun2+1
            }else {
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val new_users = dc.document.id
                        Log.d("newmeta", new_users)
                        val docRef = db.collection("users").document(new_users)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val tkID_c: String = document.get("tkID").toString()
                                    val adID_c: String = document.get("adID").toString()
                                    Log.d("ADID", adID_c)

                                    if(adID_c == deviceID){
                                        Log.d("YES", "YES NEW REG")
                                        showNewReg(tkID_c,adID_c,99)
                                    }
                                    else{
                                        Log.d("NO", "NO NEW REG")
                                    }
                                }
                                else{
                                    Log.d("TAG", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }
                    }
                }
            }
        }
        checkSnapshot()
        }

    override fun onResume(){
        btnMain.setImageResource(R.drawable.b1)
        btnSet.setImageResource(R.drawable.s1)
        super.onResume()
    }

    private fun checkBink(bink: Int){
        val binkindex = bink - 1
        val qry = "SELECT * FROM ${NOTIFI_TABLE_NAME} WHERE ${COLUMN_BINK} = $binkindex"
        val db2 = dbHandler2.readableDatabase
        val cursor = db2.rawQuery(qry, null)
        if (cursor.count == 0) {
//            Toast.makeText(this, "NOT RD ", Toast.LENGTH_SHORT).show()
        } else {
            cursor.moveToFirst()
            val name_n = cursor.getString(1)
            val bink_n = cursor.getString(2)
            val alert_n = cursor.getString(3)
            val sound_n = cursor.getString(4)
            val message_n = cursor.getString(5)

            if (alert_n.toInt() == 1){
                showNotificationNoSound(name_n,message_n,bink_n.toInt())
            }
            else if(alert_n.toInt() == 2){
                showNotificationOnlySound(name_n,message_n,sound_n.toInt(),bink_n.toInt())
            }
            else if(alert_n.toInt() == 0){
                showNotification(name_n,message_n,sound_n.toInt(),bink_n.toInt())
            }
        }
        cursor.close()
        db2.close()
    }

    private fun checkSnapshot(){
        val deviceID = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
        val docRef = db.document("bink/binkdata")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "data: ${snapshot.data}")
                val docRef = db.collection("bink").document("binkdata")
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val bink_c: Int = document.get("bink_count").toString().toInt()
                            val tkid_c: String = document.get("tkID").toString()
                            Log.d("TAG", "BINK : " + bink_c)
                            Log.d("TAG", "tkID : " + tkid_c)
                            db.collection("users").whereEqualTo("tkID", tkid_c)
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        val adid_C = document.get("adID")
                                        Log.d("TAG", "adID: " + adid_C)
                                        if (adid_C == deviceID) {
                                            Log.d("Check device", "Yes this device" + adid_C)
                                            if (trun==0){
                                                trun = trun+1

                                            }else {
                                                checkBink(bink_c)
                                            }
                                        } else
                                            Log.d("Check device", "No" + adid_C)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("TAG", "Error getting documents: ", exception as Throwable?)
                        }
                    } else {
                    Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun showNotification(title: String, body: String,sound : Int,ch : Int){
        val sound_noti1 = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarmfrenzy)
        val sound_noti2 = Uri.parse("android.resource://" + packageName + "/" + R.raw.arpeggio)
        val sound_noti3 = Uri.parse("android.resource://" + packageName + "/" + R.raw.office)
        val sound_noti4 = Uri.parse("android.resource://" + packageName + "/" + R.raw.springboard)
        val sound_noti5 = Uri.parse("android.resource://" + packageName + "/" + R.raw.youhavenewmessage)
        var statsound  : Uri

        if(sound== 0){
            statsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }else if(sound == 1){
            statsound = sound_noti1
        }else if(sound == 2){
            statsound = sound_noti2
        }else if(sound == 3){
            statsound = sound_noti3
        }else if(sound == 4){
            statsound = sound_noti4
        }else if(sound == 5){
            statsound = sound_noti5
        }else{
            statsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(this,LauncherActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            val contentView = RemoteViews(packageName,R.layout.notification_layout)
            contentView.setTextViewText(R.id.tv_title,title)
            contentView.setTextViewText(R.id.tv_content,body)
            notificationChannel = NotificationChannel(channelId, descriptor, IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            val pattern = longArrayOf(500, 1000, 500, 1000)
            v.vibrate(pattern,-1)

            builder  = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.eyecare))
                .setVibrate(longArrayOf(500, 1000, 500, 1000))
                .setAutoCancel(true)
                .setSound(statsound)
                .setContentIntent(pendingIntent)
            notificationManager.notify(ch, builder.build())
        }
        else {
            val bitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setVibrate(longArrayOf(500, 1000, 500, 1000))
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(statsound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(ch, notificationBuilder.build())
        }
    }

    private fun showNotificationNoSound(title: String, body: String,ch : Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(this, LauncherActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val contentView = RemoteViews(packageName, R.layout.notification_layout)
            contentView.setTextViewText(R.id.tv_title, title)
            contentView.setTextViewText(R.id.tv_content, body)
            notificationChannel = NotificationChannel(channelId, descriptor, IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            val pattern = longArrayOf(500, 1000, 500, 1000)
            v.vibrate(pattern,-1)
            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare))
                .setVibrate(longArrayOf(500, 1000, 500, 1000))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            notificationManager.notify(ch, builder.build())
        } else {
            val bitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setVibrate(longArrayOf(500, 1000, 500, 1000))
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(ch, notificationBuilder.build())
        }
    }

    private fun showNotificationOnlySound(title: String, body: String,sound : Int,ch: Int) {
        val sound_noti1 = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarmfrenzy)
        val sound_noti2 = Uri.parse("android.resource://" + packageName + "/" + R.raw.arpeggio)
        val sound_noti3 = Uri.parse("android.resource://" + packageName + "/" + R.raw.office)
        val sound_noti4 = Uri.parse("android.resource://" + packageName + "/" + R.raw.springboard)
        val sound_noti5 = Uri.parse("android.resource://" + packageName + "/" + R.raw.youhavenewmessage)
        var statsound: Uri

        if (sound == 0) {
            statsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } else if (sound == 1) {
            statsound = sound_noti1
        } else if (sound == 2) {
            statsound = sound_noti2
        } else if (sound == 3) {
            statsound = sound_noti3
        } else if (sound == 4) {
            statsound = sound_noti4
        } else if (sound == 5) {
            statsound = sound_noti5
        } else {
            statsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(this, LauncherActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val contentView = RemoteViews(packageName, R.layout.notification_layout)
            contentView.setTextViewText(R.id.tv_title, title)
            contentView.setTextViewText(R.id.tv_content, body)
            notificationChannel = NotificationChannel(channelId, descriptor, IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare))
                .setAutoCancel(true)
                .setSound(statsound)
                .setContentIntent(pendingIntent)
            notificationManager.notify(ch, builder.build())
        } else {

            val bitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_stat_notifications_active)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(statsound)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(ch, notificationBuilder.build())
        }
    }

    private fun showNewReg(tkid : String,adid : String,ch: Int){
        val statsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.eyecare)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_stat_notifications_active)
            .setVibrate(longArrayOf(500, 1000, 500, 1000))
            .setLargeIcon(bitmap)
            .setContentTitle("ทำการลงทะเบียนอุปกรณ์สำเร็จ")
            .setContentText("tkID: "+tkid)
            .setAutoCancel(true)
            .setSound(statsound)

            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(ch, notificationBuilder.build())

    }
}

