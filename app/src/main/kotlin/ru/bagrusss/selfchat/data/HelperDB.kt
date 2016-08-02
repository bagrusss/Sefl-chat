package ru.bagrusss.selfchat.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.bagrusss.selfchat.util.getDate

/**
 * Created by bagrusss.
 */

class HelperDB private constructor(context: Context) : SQLiteOpenHelper(context, HelperDB.DB_NAME, null, HelperDB.DB_VERSION) {

    companion object {

        private var mInstance: HelperDB? = null
        private var mDB: SQLiteDatabase? = null

        val DB_NAME = "selfchat.db"
        val DB_VERSION = 1

        val TABLE_MESSAGES = "messages"

        val ID = "_id"
        val TYPE = "type"
        val DATA = "dt"
        val TIME = "tm"

        val TYPE_TEXT = 1
        val TYPE_IMAGE = 2
        val TYPE_DATE = 3

        private val CREATE_TABLE_MESSAGES = """
        CREATE TABLE $TABLE_MESSAGES (
        $ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $TYPE INTEGER,
        $DATA TEXT,
        $TIME TEXT)
        """

        fun getInstance(cont: Context): HelperDB {
            var localInstance = mInstance
            if (localInstance == null) {
                synchronized(HelperDB::class.java) {
                    localInstance = mInstance
                    if (localInstance == null) {
                        localInstance = HelperDB(cont)
                        mInstance = localInstance
                    }
                }
            }
            return localInstance!!
        }

        fun closeDB() {
            if (mDB != null && mDB!!.isOpen)
                mDB!!.close()
            mInstance = null
        }
    }

    init {
        mDB = writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_MESSAGES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(data: String, time: String, type: Int) {
        val date = getDate()
        val c = mDB!!.query(TABLE_MESSAGES, arrayOf(ID),
                "$TYPE=? and $DATA=?",
                arrayOf(TYPE_DATE.toString(), date), null, null, null)
        if (!c.moveToFirst())
            insertDate(date)
        c.close()
        val cv = ContentValues()
        cv.put(DATA, data)
        cv.put(TIME, time)
        cv.put(TYPE, type)
        mDB!!.insert(TABLE_MESSAGES, null, cv)
    }

    private fun insertDate(date: String) {
        val cv = ContentValues()
        cv.put(DATA, date)
        cv.put(TYPE, TYPE_DATE)
        mDB!!.insert(TABLE_MESSAGES, null, cv)
    }

    fun getAllMessages(): Cursor {
        return mDB!!.query(TABLE_MESSAGES, null, null, null, null, null, null)
    }

}
