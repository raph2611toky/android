package api.user

import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Utilisateur
import mg.business.ikonnectmobile.data.model.RefreshToken
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import android.util.Log

class UserDao(private val dbHelper: DatabaseHelper) {
    private var database: SQLiteDatabase? = null

    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        database?.close()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun addUser(user: Utilisateur): Long {
        val hashedPassword = hashPassword(user.password)
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM, user.nom)
            put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword)
        }
        return database!!.insert(DatabaseHelper.TABLE_USER, null, values)
    }

    fun getUserById(id: Int): Utilisateur? {
        val cursor = database!!.query(
            DatabaseHelper.TABLE_USER, null, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()), null, null, null
        )
        return if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nomIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOM)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            Utilisateur(
                id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                createdAt = if (createdAtIndex >= 0) cursor.getString(createdAtIndex) else null,
                lastLogin = if (lastLoginIndex >= 0) cursor.getString(lastLoginIndex) else null
            ).also {
                cursor.close()
            }
        } else {
            cursor.close()
            null
        }
    }

    fun getUserByUsername(username: String): Utilisateur? {
        val cursor = database!!.query(
            DatabaseHelper.TABLE_USER, null, "${DatabaseHelper.COLUMN_NOM} = ?", arrayOf(username), null, null, null
        )
        return if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nomIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOM)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            Utilisateur(
                id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                createdAt = if (createdAtIndex >= 0) cursor.getString(createdAtIndex) else null,
                lastLogin = if (lastLoginIndex >= 0) cursor.getString(lastLoginIndex) else null
            ).also {
                cursor.close()
            }
        } else {
            cursor.close()
            null
        }
    }

    fun getAllUsers(): List<Utilisateur> {
        val users = mutableListOf<Utilisateur>()
        val cursor = database!!.query(DatabaseHelper.TABLE_USER, null, null, null, null, null, null)

        // Vérification si le curseur est vide
        if (cursor.count == 0) {
            cursor.close()
            return users // Retourne une liste vide
        }

        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nomIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOM)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            users.add(
                Utilisateur(
                    id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                    nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                    password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                    createdAt = if (createdAtIndex >= 0) cursor.getString(createdAtIndex) else null,
                    lastLogin = if (lastLoginIndex >= 0) cursor.getString(lastLoginIndex) else null
                )
            )
        }
        cursor.close()
        return users
    }

    fun updateUser(user: Utilisateur): Int {
        val hashedPassword = hashPassword(user.password)
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM, user.nom)
            put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword)
            put(DatabaseHelper.COLUMN_LAST_LOGIN, "CURRENT_TIMESTAMP")
        }
        return database!!.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(user.id.toString()))
    }

    fun verifyLogin(nom: String, password: String): Utilisateur? {
        val hashedPassword = hashPassword(password)
        Log.d("UserRoutes", "hashpassword: $hashedPassword")
        val cursor = database!!.query(
            DatabaseHelper.TABLE_USER, null, "${DatabaseHelper.COLUMN_NOM} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(nom, hashedPassword), null, null, null
        )

        return if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nomIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOM)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            Utilisateur(
                id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                createdAt = if (createdAtIndex >= 0) cursor.getString(createdAtIndex) else null,
                lastLogin = if (lastLoginIndex >= 0) cursor.getString(lastLoginIndex) else null
            ).also {
                cursor.close()
            }
        } else {
            Log.d("UserRoutes","cursor: $cursor")
            cursor.close()
            null
        }
    }

    fun deleteUser(id: Int): Int {
        return database!!.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    fun addRefreshToken(userId: Int, token: String, expiration: Date): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val expirationString = dateFormat.format(expiration)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_ID, userId)
            put(DatabaseHelper.COLUMN_TOKEN, token)
            put(DatabaseHelper.COLUMN_EXPIRATION, expirationString)
        }
        return database!!.insert(DatabaseHelper.TABLE_REFRESH_TOKEN, null, values)
    }

    fun isTokenUsed(token: String): Boolean {
        val cursor = database!!.query(
            DatabaseHelper.TABLE_REFRESH_TOKEN, null,
            "${DatabaseHelper.COLUMN_TOKEN} = ? AND ${DatabaseHelper.COLUMN_USED} = 0",
            arrayOf(token), null, null, null
        )
        val isUsed = cursor.count == 0
        cursor.close()
        return isUsed
    }

    fun markTokenAsUsed(token: String) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USED, 1)
        }
        database!!.update(DatabaseHelper.TABLE_REFRESH_TOKEN, values, "${DatabaseHelper.COLUMN_TOKEN} = ?", arrayOf(token))
    }

    fun getAllRefreshTokens(): List<RefreshToken> {
        val refreshTokens = mutableListOf<RefreshToken>()
        val cursor = database!!.query(DatabaseHelper.TABLE_REFRESH_TOKEN, null, null, null, null, null, null)

        if (cursor.count == 0) {
            cursor.close()
            return refreshTokens
        }

        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN_ID)
            val userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)
            val tokenIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN)
            val expirationIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPIRATION)
            val usedIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USED)

            refreshTokens.add(
                RefreshToken(
                    id_refresh = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                    user_id = if (userIdIndex >= 0) cursor.getInt(userIdIndex) else null,
                    refresh_token = if (tokenIndex >= 0) cursor.getString(tokenIndex) else "",
                    expiration = if (expirationIndex >= 0) cursor.getString(expirationIndex) else null,
                    already_used = if (usedIndex >= 0) cursor.getInt(usedIndex) == 1 else null
                )
            )
        }
        cursor.close()
        return refreshTokens
    }

}
