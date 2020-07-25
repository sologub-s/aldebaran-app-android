package com.example.aldebaran_rc

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import com.example.aldebaran_rc.enums.IntentActions
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ControllerPullService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    val serviceId = 1

    private lateinit var threadPull : Thread

    override fun onBind(intent: Intent?): IBinder? {
        log("ControllerPullService : onBind")
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        log("ControllerPullService : onStart")

        super.onStart(intent, startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("ControllerPullService : onStartCommand")

        if (intent == null) {
            log("ControllerPullService : onStartCommand : null intent")
            return START_STICKY
        }

        val action = intent.action

        //action = Actions.START.name

        log("ControllerPullService : onStartCommand : action : $action")

        when (action) {
            Actions.START.name -> startService()
            Actions.STOP.name -> stopService()
            //else -> startService()
            else -> {
                log("ControllerPullService : onStartCommand : unknown action")
                log("ControllerPullService : onStartCommand : starting service by default")
                startService()
            }
        }

        /*
        threadPull = Thread(Runnable {
            while (true) {
                Thread.sleep(10000)
                log("ControllerPullService 10000")
            }
        }).also { thread ->
            thread.start()
        }

         */

        return super.onStartCommand(intent, flags, startId)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                it.action?.also { action ->
                    when(action) {
                        "received" -> {}
                        "received2" -> {}
                        "received3" -> {}
                    }

                }
            }
        }
    }

    private fun pullFromController() {
        log("ControllerPullService : pullFromController")

        // Logic starts

        Intent(IntentActions.PULL_RECEIVED.name).also { intent ->

            intent.putExtra("status_:_gid", Random.nextBoolean())

            sendBroadcast(intent)
        }

        // Logic ends

        //sendBroadcast(Intent("received"))

        /*
        val intentFilter = IntentFilter()
        intentFilter.addAction("received")
        intentFilter.addAction("received")
        intentFilter.addAction("received")
        intentFilter.addAction("received")
        intentFilter.addAction("received")

        registerReceiver(receiver, intentFilter)
         */

        log("ControllerPullService : pullFromController : bus pulled")

    }

    private fun pingFakeServer() {

        log("ControllerPullService : pingFakeServer")

        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
        val gmtTime = df.format(Date())

        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        val json =
            """
                {
                    "deviceId": "$deviceId",
                    "createdAt": "$gmtTime"
                }
            """
        try {
            Fuel.post("https://jsonplaceholder.typicode.com/posts")
                .jsonBody(json)
                .response { _, _, result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        log("ControllerPullService : [response bytes] : ${String(bytes)}")
                    } else {
                        log("ControllerPullService : [response error] : ${error?.message}")
                    }
                }
        } catch (e: Exception) {
            log("ControllerPullService : Error making the request: ${e.message}")
        }
    }

    override fun onCreate() {

        super.onCreate()

        log("ControllerPullService : onCreate")
        var notification = createNotification()
        startForeground(serviceId, notification)
    }

    override fun onDestroy() {
        log("ControllerPullService : onDestroy")
        super.onDestroy()
    }

    private fun startService() {
        if (isServiceStarted) return
        log("ControllerPullService : startService")
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ControllerPullService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        /*
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pingFakeServer()
                }
                delay(5000)
            }
            log("ControllerPullService : loop end for pingFakeServer")
        }
         */

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pullFromController()
                }
                delay(1000)
            }
            log("ControllerPullService : loop end for pullI2CBus")
        }
    }

    private fun stopService() {
        log("ControllerPullService : stopService")
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("ControllerPullService : stopService : Service stopped without being started : ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "ControllerPullService Channel"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            val channel = NotificationChannel(
                notificationChannelId,
                "ControllerPullService Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "ControllerPullService Channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("ControllerPullService")
            .setContentText("This is your favorite ControllerPullService Channel working")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("ControllerPullService Channel Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
}


