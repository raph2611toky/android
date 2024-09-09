package api.user

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Utilisateur
import mg.business.ikonnectmobile.data.model.LoginRequest
import android.util.Log

fun Route.UserRoutes(databaseHelper: DatabaseHelper) {
    val userDao = UserDao(databaseHelper)

    route("/users") {

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

        get("/hello") {
            try {
                call.respond(HttpStatusCode.OK, "Hello, World!")
            } catch (e: Exception) {
                Log.e("UserRoutes", "Erreur lors de l'accès à /hello: $e")
                call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue.")
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

        post("/login") {
            try {
                Log.i("UserRoutes","POST /users/login")
                val loginRequest = call.receive<LoginRequest>()
                userDao.open()
                val isValidUser = userDao.verifyLogin(loginRequest.nom, loginRequest.password)
                userDao.close()
                if (isValidUser) {
                    call.respond(HttpStatusCode.OK, "Connexion réussie")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Nom d'utilisateur ou mot de passe incorrect.")
                }
            } catch (e: Exception) {
                Log.e("UserRoutes", "Erreur lors de la connexion de l'utilisateur: $e")
                call.respond(HttpStatusCode.InternalServerError, "Une erreur est survenue lors de la connexion de l'utilisateur.")
            }
        }

    }
}
