package mg.business.ikonnectmobile.data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context

class DatabaseHelper(context: Context, dbConfig: DbConfig) : SQLiteOpenHelper(context, dbConfig.dbName, null, dbConfig.dbVersion) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USER_TABLE = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOM TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_LAST_LOGIN DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """
        db.execSQL(CREATE_USER_TABLE)

        val CREATE_REFRESH_TOKEN_TABLE = """
        CREATE TABLE $TABLE_REFRESH_TOKEN (
            $COLUMN_TOKEN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USER_ID INTEGER,
            $COLUMN_TOKEN TEXT,
            $COLUMN_EXPIRATION DATETIME,
            $COLUMN_USED BOOLEAN DEFAULT 0,
            FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID)
        )
    """
        db.execSQL(CREATE_REFRESH_TOKEN_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REFRESH_TOKEN")
        onCreate(db)
    }

    companion object {
        const val TABLE_USER = "utilisateur"
        const val COLUMN_ID = "id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_LAST_LOGIN = "last_login"

        const val TABLE_REFRESH_TOKEN = "refreshtoken"
        const val COLUMN_TOKEN_ID = "id_refresh"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_TOKEN = "refresh_token"
        const val COLUMN_EXPIRATION = "expiration"
        const val COLUMN_USED = "deja_utilisé"
    }

    data class DbConfig(val dbName: String, val dbVersion: Int)
}
