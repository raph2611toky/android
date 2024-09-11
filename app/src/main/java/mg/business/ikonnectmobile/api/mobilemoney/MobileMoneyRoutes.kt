package mg.business.ikonnectmobile.api.mobilemoney

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import android.util.Log
import mg.business.ikonnectmobile.data.model.PayementVerifyRequest
import io.ktor.server.auth.*
import mg.business.ikonnectmobile.data.DatabaseHelper

fun Route.MobileMoneyRoutes(mvolaController: MvolaController, databaseHelper: DatabaseHelper) {

    val messageDao = MessageDao(databaseHelper)

    authenticate("auth-jwt") {
        post("/mobilemoney/filter") {
            try {
                Log.d("Routes", "POST /mobilemoney/filter")
                val paymentVerify = call.receive<PayementVerifyRequest>()
                val reference = paymentVerify.reference
                val numero = paymentVerify.numero
                val date = paymentVerify.date

                messageDao.open()
                val existingMessages = messageDao.getAllMessages()
                val ref = "Ref: " + reference
                val depot = "1/2 Depot reussi : "
                val recu = " Ar recu"
                val messageAlreadyExists = existingMessages.any {
                    it.senderAddress.contains("MVOLA", ignoreCase = true) &&
                            it.body.contains(ref) &&
                            it.body.contains(numero) &&
                            it.body.contains(date) &&
                            (it.body.contains(depot) || it.body.contains(recu))
                }

                if (messageAlreadyExists) {
                    messageDao.close()
                    call.respond(HttpStatusCode.BadRequest, "Ce message a déjà été utilisé auparavant.")
                } else {
                    val filteredMessages = mvolaController.getFilteredMvolaMessages(reference, numero, date)

                    filteredMessages.forEach { messageDao.saveMessage(it) }
                    messageDao.close()
                    call.respond(HttpStatusCode.OK, filteredMessages)
                }
            } catch (e: Exception) {
                Log.e("Routes", "Erreur lors de la verification de message: $e")
                call.respond(HttpStatusCode.BadRequest, "Erreur lors de la verification de message")
            }
        }
    }
    get("/mobilemoney/blacklist"){
        try{
            messageDao.open()
            val messages = messageDao.getAllMessages()
            messageDao.close()
            call.respond(HttpStatusCode.OK, messages)
        }catch (e:Exception){
            Log.e("Routes", "erreur lors de la récupérations de la black liste des messages: $e")
            call.respond(HttpStatusCode.InternalServerError, "erreur lors de la récupérations de la black liste des messages")
        }
    }
}
