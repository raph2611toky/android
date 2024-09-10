package mg.business.ikonnectmobile.api.mobilemoney

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import android.util.Log
import mg.business.ikonnectmobile.data.model.PayementVerifyRequest
import io.ktor.server.auth.*

fun Route.MobileMoneyRoutes(mvolaController: MvolaController) {

    authenticate("auth-jwt") {
        post("/mobilemoney/filter") {
            try {
                Log.d("Routes", "POST /mobilemoney/filter")
                val paymentVerify = call.receive<PayementVerifyRequest>()
                val reference = paymentVerify.reference
                val numero = paymentVerify.numero
                val date = paymentVerify.date

                val filteredMessages = mvolaController.getFilteredMvolaMessages(reference, numero, date)

                // Sérialiser la liste filtrée et la renvoyer
                call.respond(HttpStatusCode.OK, filteredMessages)
            } catch (e: Exception) {
                Log.e("Routes", "Erreur lors de la verification de message: $e")
                call.respond(HttpStatusCode.BadRequest, "Erreur lors de la verification de message")
            }
        }
    }
}
