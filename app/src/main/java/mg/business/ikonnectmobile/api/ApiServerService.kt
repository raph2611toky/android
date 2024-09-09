package mg.business.ikonnectmobile.api

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.ktor.serialization.kotlinx.json.json
import api.user.UserRoutes
import android.app.Notification
import android.app.NotificationChannel
import java.net.ServerSocket
import java.io.IOException
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.R
import java.net.NetworkInterface
import java.net.InetAddress
import android.util.Log
import kotlinx.serialization.json.Json


class ApiServerService : Service() {

    private var IP_ADDR: String? = null
    private val PORT = 8000

    private val server by lazy {
        embeddedServer(Netty, port = PORT, host = IP_ADDR ?: "0.0.0.0") {
            module()
        }
    }

    private var isServerRunning = false


    // Fonction pour récupérer l'adresse IP locale
    fun getLocalIpAddress(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    // Exclure les adresses de bouclage et n'inclure que les adresses IPv4
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val ipAddress = address.hostAddress
                        if (ipAddress.indexOf(':') < 0) {
                            return ipAddress
                        }
                    }
                }
            }
            null
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("ApiServerService", "Erreur lors de la récupération de l'adresse IP : ${ex.message}")
            null
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        IP_ADDR = getLocalIpAddress()
        if (!isServerRunning && !isPortInUse(PORT)) {
            startForeground(1, createNotification())
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.i("ApiServerService", "Démarrage du serveur API sur $IP_ADDR:$PORT...")
                    server.start(wait = false)
                    isServerRunning = true
                    Log.i("ApiServerService", "Le serveur est en cours d'exécution sur $IP_ADDR:$PORT")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("ApiServerService", "Erreur lors du démarrage du serveur : ${e.message}")
                    stopSelf()
                }
            }
        } else {
            Log.i("ApiServerService", "Le serveur est déjà en cours d'exécution ou le port est occupé.")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            Log.i("ApiServerService", "Le service est en train de s'arrêter.")
            if (isServerRunning) {
                server.stop(1000, 1000)
                isServerRunning = false
                Log.i("ApiServerService", "Le serveur a été arrêté avec succès.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ApiServerService", "Erreur lors de l'arrêt du serveur : ${e.message}")
        } finally {
           /// super.onDestroy()
            Log.d("ApiServerService", "Super should stoped here...")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Vérification si le port est déjà utilisé
    private fun isPortInUse(port: Int): Boolean {
        return try {
            ServerSocket(port).close()
            false
        } catch (e: IOException) {
            Log.e("ApiServerService", "Le port $port est déjà en cours d'utilisation : ${e.message}")
            true
        }
    }

    private fun createNotification(): Notification {
        return try {
            val channelId = "api_server_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = "API Server"
                val channelDescription = "Notification for API server running in the foreground."
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                }

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            Log.d("ApiServerService", "Notification creation.....")

            NotificationCompat.Builder(this, channelId)
                .setContentTitle("API Server")
                .setContentText("The API server is running.")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ApiServerService", "Erreur lors de la création de la notification : ${e.message}")
            NotificationCompat.Builder(this, "fallback_channel")
                .setContentTitle("API Server Error")
                .setContentText("Failed to create notification.")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
        }
    }

    private fun Application.module() {
        try {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            val dbConfig = DatabaseHelper.DbConfig("ikonnectarea.db", 1)
            val databaseHelper = DatabaseHelper(this@ApiServerService, dbConfig)
            routing {
                UserRoutes(databaseHelper)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ApiServerService", "Erreur lors de la configuration des routes : ${e.message}")
        }
    }
}
