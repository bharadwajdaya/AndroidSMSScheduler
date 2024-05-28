package com.vavyIndia.sms
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "smsScheduler.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MESSAGES = "messages"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_SUBJECT = "subject"
        private const val COLUMN_BODY = "body"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createMessagesTable = ("CREATE TABLE $TABLE_MESSAGES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_SUBJECT TEXT, "
                + "$COLUMN_BODY TEXT)")
        db.execSQL(createMessagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    fun addMessage(subject: String, body: String) {
        val values = ContentValues()
        values.put(COLUMN_SUBJECT, subject)
        values.put(COLUMN_BODY, body)

        val db = this.writableDatabase
        db.insert(TABLE_MESSAGES, null, values)
        db.close()
    }

    fun getAllMessages(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_MESSAGES", null)
    }

    fun deleteMessage(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_MESSAGES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
