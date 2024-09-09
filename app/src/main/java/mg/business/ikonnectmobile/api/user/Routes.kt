package api.user

import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import android.util.Log

fun Route.Routes() {
    try {
        get("/hello") {
            Log.d("Routes", "Handling GET /hello/world")
            call.respond("Hello, World!")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("Routes", "Erreur lors de la gestion de la route /hello : ${e.message}")
    }
}
