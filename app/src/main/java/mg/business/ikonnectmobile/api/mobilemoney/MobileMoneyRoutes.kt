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

fun Route.MobileMoneyRoutes(mobileMoneyController: MobileMoneyController, databaseHelper: DatabaseHelper) {

    val messageDao = MessageDao(databaseHelper)

    route("/mobilemoney"){
        authenticate("auth-jwt") {
            post("/filter") {
                try {
                    Log.d("Routes", "POST /mobilemoney/filter")
                    val paymentVerify = call.receive<PayementVerifyRequest>()
                    val reference = paymentVerify.reference
                    val numero = paymentVerify.numero
                    val date = paymentVerify.date // format YYYY-mm-dd

                    val filteredMessages = mobileMoneyController.getFilteredMessages(reference, numero, date, messageDao)

                    filteredMessages.forEach { messageDao.saveMessage(it) }

                    call.respond(HttpStatusCode.OK, filteredMessages)
                } catch (e: IllegalArgumentException) {
                    Log.e("Routes", "Message déjà utilisé: $e")
                    call.respond(HttpStatusCode.BadRequest, "Ce message a déjà été utilisé auparavant.")
                } catch (e: Exception) {
                    Log.e("Routes", "Erreur lors de la vérification du message: $e")
                    call.respond(HttpStatusCode.BadRequest, "Erreur lors de la vérification du message")
                } finally {
                    messageDao.close()
                }
            }

            get("/blacklist"){
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
    }
}
