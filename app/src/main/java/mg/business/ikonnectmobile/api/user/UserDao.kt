package api.user

import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Utilisateur
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.security.MessageDigest

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

    fun verifyLogin(nom: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        val cursor = database!!.query(
            DatabaseHelper.TABLE_USER, null, "${DatabaseHelper.COLUMN_NOM} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(nom, hashedPassword), null, null, null
        )
        return cursor.moveToFirst().also {
            cursor.close()
        }
    }

    fun deleteUser(id: Int): Int {
        return database!!.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }
}
