package com.example.cmft

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cmft.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainActivity : AppCompatActivity() {
    private val channelld = "cmft"
    private val PERMISSIONS_REQUEST = 1 // 권한 요청 레벨
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        super.onCreate(savedInstanceState)

        val permissionlist = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 알림 권한
            permissionlist.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 권한 체크 하고 사용자로 부터 권한 받아오기
        if (!checkpermission(this, permissionlist.toTypedArray())) {
            requestPermissions(permissionlist.toTypedArray(), PERMISSIONS_REQUEST)
        } else {
            appStart()
        }
        /* 권한 확인 끝 */
    }

    // 실제 앱 실행 함수
    private fun appStart() {
        val client = OkHttpClient()

        val request = Request.Builder().url("ws://192.168.4.1:80").build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("소켓", "연결완료")
                super.onOpen(webSocket, response)
                runOnUiThread {
                    Toast.makeText(applicationContext, "연결완료", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("소켓", "메시지: $text")
                showNotification(R.string.app_name.toString(), "침입을 감지했습니다!")
                super.onMessage(webSocket, text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("소켓", "연결종료")
                super.onClosing(webSocket, code, reason)
                runOnUiThread {
                    Toast.makeText(applicationContext, "연결 종료", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("소켓", t.message!!)
                super.onFailure(webSocket, t, response)
                runOnUiThread {
                    Toast.makeText(applicationContext, "연결 실패: $t", Toast.LENGTH_SHORT).show()
                }
            }
        }

        var webSocket: WebSocket? = null

        binding!!.button.setOnClickListener {
            try {
                webSocket = client.newWebSocket(request, listener)
                client.dispatcher.executorService.shutdown()
            } catch (_: Exception) {

            }
        }

        binding!!.settingbt.setOnClickListener {
            try {
                if (webSocket == null) {
                    Toast.makeText(applicationContext, "먼저 서버연결을 실행해 주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    webSocket!!.send("1")
                }
            } catch (_: Exception) {

            }
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "name"
            val description = "description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelld, name, importance)
            channel.description = description

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 알림 보내기 설정
    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, text: String) {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelld)
            .setSmallIcon(R.drawable.ic_launcher_background) // Add your own icon here
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            appStart()
        } else {
            // 하나라도 수락안하면 다시 할수 있게
            requestPermissions(permissions, PERMISSIONS_REQUEST)
            Toast.makeText(this, "권한을 수락해야 합니다!", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 확인 하는 함수
    private fun checkpermission(context: Context, list: Array<String>): Boolean {
        for (permission in list) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}