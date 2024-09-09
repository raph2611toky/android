package api.user

import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Utilisateur
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class UserDao(private val dbHelper: DatabaseHelper) {
    private var database: SQLiteDatabase? = null

    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        database?.close()
    }

    fun addUser(user: Utilisateur): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM, user.nom)
            put(DatabaseHelper.COLUMN_EMAIL, user.email)
            put(DatabaseHelper.COLUMN_PASSWORD, user.password)
            put(DatabaseHelper.COLUMN_CIN, user.cin)
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
            val emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val cinIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CIN)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            Utilisateur(
                id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                email = if (emailIndex >= 0) cursor.getString(emailIndex) else "",
                password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                cin = if (cinIndex >= 0) cursor.getString(cinIndex) else "",
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
            val emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)
            val passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)
            val cinIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CIN)
            val createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)
            val lastLoginIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_LOGIN)

            users.add(
                Utilisateur(
                    id = if (idIndex >= 0) cursor.getInt(idIndex) else null,
                    nom = if (nomIndex >= 0) cursor.getString(nomIndex) else "",
                    email = if (emailIndex >= 0) cursor.getString(emailIndex) else "",
                    password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "",
                    cin = if (cinIndex >= 0) cursor.getString(cinIndex) else "",
                    createdAt = if (createdAtIndex >= 0) cursor.getString(createdAtIndex) else null,
                    lastLogin = if (lastLoginIndex >= 0) cursor.getString(lastLoginIndex) else null
                )
            )
        }
        cursor.close()
        return users
    }


    fun updateUser(user: Utilisateur): Int {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM, user.nom)
            put(DatabaseHelper.COLUMN_EMAIL, user.email)
            put(DatabaseHelper.COLUMN_PASSWORD, user.password)
            put(DatabaseHelper.COLUMN_CIN, user.cin)
            put(DatabaseHelper.COLUMN_LAST_LOGIN, "CURRENT_TIMESTAMP")
        }
        return database!!.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(user.id.toString()))
    }

    fun deleteUser(id: Int): Int {
        return database!!.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }
}
