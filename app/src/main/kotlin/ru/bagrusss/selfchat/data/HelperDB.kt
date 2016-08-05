package ru.bagrusss.selfchat.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.defaultSharedPreferences
import ru.bagrusss.selfchat.network.json.Msg

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

        private var baseUrl: String? = null

        fun getInstance(cont: Context): HelperDB {
            var localInstance = mInstance
            if (localInstance == null) {
                synchronized(HelperDB::class.java) {
                    localInstance = mInstance
                    if (localInstance == null) {
                        localInstance = HelperDB(cont)
                        mInstance = localInstance
                        baseUrl = cont.defaultSharedPreferences.getString("server", "")
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

    private val CREATE_TABLE_MESSAGES = """
        CREATE TABLE $TABLE_MESSAGES (
        $ID INTEGER PRIMARY KEY,
        $TYPE INTEGER,
        $DATA TEXT,
        $TIME TEXT)
        """

    private val INSERT = """
        INSERT OR IGNORE INTO $TABLE_MESSAGES ($ID, $TYPE, $DATA, $TIME) VALUES (?,?,?,?)
        """

    init {
        mDB = writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_MESSAGES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(id: Long, data: String, date: String, time: String, type: Int) {
        mDB!!.query(TABLE_MESSAGES, arrayOf(ID),
                "$TYPE=? and $DATA=?",
                arrayOf(TYPE_DATE.toString(), date), null, null, null)
                .use {
                    if (!it.moveToFirst())
                        insertDate(date, id-10)
                }
        val cv = ContentValues()
        cv.put(ID, id)
        cv.put(DATA, data)
        cv.put(TIME, time)
        cv.put(TYPE, type)
        mDB!!.insert(TABLE_MESSAGES, null, cv)
    }

    private fun insertDate(date: String, id: Long) {
        val cv = ContentValues()
        cv.put(DATA, date)
        cv.put(TYPE, TYPE_DATE)
        cv.put(ID, id)
        mDB!!.insert(TABLE_MESSAGES, null, cv)
    }

    fun getAllMessages(): Cursor {
        return mDB!!.query(TABLE_MESSAGES, null, null, null, null, null, null)
    }

    fun getLastMessageId(): Long {
        mDB!!.query(TABLE_MESSAGES, arrayOf(ID), null, null, null, null, null).use {
            if (it.moveToLast())
                return it.getLong(0)
        }
        return 0
    }

    fun insertMessages(msgs: List<Msg>) {
        val insert = mDB!!.compileStatement(INSERT)
        insert.use {
            for (msg in msgs) {
                mDB!!.query(TABLE_MESSAGES, arrayOf(ID), "$TYPE=? and $DATA=?",
                        arrayOf(TYPE_DATE.toString(), msg.date), null, null, null)
                        .use {
                            if (!it.moveToFirst()) {
                                insert.bindLong(1, msg.timestamp - 10)
                                insert.bindLong(2, TYPE_DATE.toLong())
                                insert.bindString(3, msg.date)
                                insert.bindString(4, msg.time)
                                insert.executeInsert()
                            }
                        }
                insert.bindLong(1, msg.timestamp)
                insert.bindLong(2, msg.type.toLong())
                val data: String
                if (msg.type == TYPE_IMAGE)
                    data = baseUrl + msg.data
                else data = msg.data
                insert.bindString(3, data)
                insert.bindString(4, msg.time)
                insert.executeInsert()
            }
        }
    }

}
