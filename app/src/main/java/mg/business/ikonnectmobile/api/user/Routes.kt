package api.user

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Utilisateur
import mg.business.ikonnectmobile.data.model.AuthResponse
import android.util.Log
import mg.business.ikonnectmobile.utils.JwtConfig
import io.ktor.server.auth.*

fun Route.Routes(databaseHelper: DatabaseHelper) {
    val userDao = UserDao(databaseHelper)

    route("/users") {
        authenticate("auth-jwt"){
            get("/list") {
                try {
                    Log.i("UserRoutes","GET /users/list")
                    userDao.open()
                    val users = userDao.getAllUsers()
                    userDao.close()
                    call.respond(HttpStatusCode.OK, users)
                } catch (e: Exception) {
                    Log.e("UserRoutes", "Erreur lors de la récupération de la liste des utilisateurs: $e")
                    call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la récupération des utilisateurs.")
                }
            }

            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        userDao.open()
                        val user = userDao.getUserById(id)
                        userDao.close()
                        if (user != null) {
                            call.respond(HttpStatusCode.OK, user)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Utilisateur non trouvé.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "ID invalide.")
                    }
                } catch (e: Exception) {
                    Log.e("UserRoutes", "Erreur lors de la récupération de l'utilisateur $e")
                    call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la récupération de l'utilisateur.")
                }
            }

            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        val user = call.receive<Utilisateur>()
                        userDao.open()
                        val updated = userDao.updateUser(user.copy(id = id))
                        userDao.close()
                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Utilisateur mis à jour avec succès.")
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, "Échec de la mise à jour de l'utilisateur.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "ID invalide.")
                    }
                } catch (e: Exception) {
                    Log.e("UserRoutes", "Erreur lors de la mise à jour de l'utilisateur $e")
                    call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la mise à jour de l'utilisateur.")
                }
            }

            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        userDao.open()
                        val deleted = userDao.deleteUser(id)
                        userDao.close()
                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Utilisateur supprimé avec succès.")
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, "Échec de la suppression de l'utilisateur.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "ID invalide.")
                    }
                } catch (e: Exception) {
                    Log.e("UserRoutes", "Erreur lors de la suppression de l'utilisateur: $e")
                    call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la suppression de l'utilisateur.")
                }
            }
        }

        post("/") {
            try {
                Log.i("UserRoutes","POST /users/")
                val user = call.receive<Utilisateur>()
                userDao.open()
                val id = userDao.addUser(user)
                userDao.close()
                if (id > 0) {
                    call.respond(HttpStatusCode.Created, "Utilisateur créé avec l'ID: $id")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Échec de la création de l'utilisateur.")
                }
            } catch (e: Exception) {
                Log.e("UserRoutes", "Erreur lors de la création de l'utilisateur: $e")
                call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la création de l'utilisateur.")
            }
        }

        post("/login") {
            try {
                Log.i("UserRoutes", "POST /users/login")
                val loginRequest = call.receive<Map<String, String>>()
                val username = loginRequest["username"] ?: ""
                val password = loginRequest["password"] ?: ""

                userDao.open()
                val user = userDao.verifyLogin(username, password)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Nom d'utilisateur ou mot de passe incorrect.")
                    return@post
                }
                Log.i("UserRoutes","User logged in..")

                val accessToken = JwtConfig.makeAccessToken(user.nom)
                val refreshToken = JwtConfig.makeRefreshToken(user.id!!)

                userDao.addRefreshToken(user.id, refreshToken.token, refreshToken.expiration)

                call.respond(HttpStatusCode.OK, AuthResponse(
                    accessToken,
                    refreshToken.token
                ))
            } catch (e: Exception) {
                Log.d("UserRoutes","Erreur de connextion: $e")
                call.respond(HttpStatusCode.Unauthorized, "Erreur de connexion.")
            }
        }

    }

    route("/token"){
        authenticate("auth-jwt"){
            post("/refresh") {
                try {
                    Log.d("UserRoutes", "POST /token/refresh")
                    val tokenRequest = call.receive<Map<String, String>>()
                    val refreshToken = tokenRequest["refreshToken"]

                    if (refreshToken == null || !JwtConfig.isRefreshToken(refreshToken)) {
                        call.respond(HttpStatusCode.BadRequest, "Refresh token invalide.")
                        return@post
                    }
                    userDao.open()

                    if (userDao.isTokenUsed(refreshToken)) {
                        call.respond(HttpStatusCode.Unauthorized, "Le refresh token a déjà été utilisé.")
                        return@post
                    }

                    val decodedJWT = JwtConfig.verifier.verify(refreshToken)
                    val userId = decodedJWT.getClaim("user_id").asInt()

                    val user = userDao.getUserById(userId)

                    if (user == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Utilisateur non trouvé.")
                        return@post
                    }

                    userDao.markTokenAsUsed(refreshToken)
                    val newAccessToken = JwtConfig.makeAccessToken(user.nom)
                    val newRefreshTokenWithExp = JwtConfig.makeRefreshToken(userId)
                    userDao.addRefreshToken(user.id!!, newRefreshTokenWithExp.token, newRefreshTokenWithExp.expiration)
                    userDao.close()

                    Log.d("UserRoutes", "newAccesToken : $newAccessToken, \nrefreshToken: $newRefreshTokenWithExp")
                    call.respond(HttpStatusCode.OK, mapOf(
                        "accessToken" to newAccessToken,
                        "refreshToken" to newRefreshTokenWithExp.token
                    ))
                } catch (e: Exception) {
                    Log.d("UserRoutes", "Impossible de rafraichire le token: $e")
                    call.respond(HttpStatusCode.Unauthorized, "Impossible de rafraîchir le token.")
                }
            }
        }
    }
}
