package com.example.care
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.lang.Exception

class DBHandler(context: Context, name: String?, factory : SQLiteDatabase.CursorFactory?,version : Int):
    SQLiteOpenHelper(context, DATABASE_NAME,factory, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "mydatabase.db"
        private val DATABASE_VERSION = 1
        val NOTIFI_TABLE_NAME = "notifi"
        val COLUMN_NOTIFI_ID = "notifiid"
        val COLUMN_NOTIFI_NAME = "notifiname"
        val COLUMN_BINK = "bink"
        val COLUMN_STYLE_ALERT = "stylealert"
        val COLUMN_STYLE_SOUND = "stylesound"
        val COLUMN_MESSAGE = "message"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_NOTIFI_TABLE: String = ("CREATE TABLE $NOTIFI_TABLE_NAME (" +
                "$COLUMN_NOTIFI_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOTIFI_NAME TEXT," +
                "$COLUMN_BINK INTEGER DEFAULT 0," +
                "$COLUMN_STYLE_ALERT TEXT," +
                "$COLUMN_STYLE_SOUND TEXT," +
                "$COLUMN_MESSAGE)")
        db?.execSQL(CREATE_NOTIFI_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun getNotifis(mCtx: Context): ArrayList<Notifi> {
        val qry = "Select * From $NOTIFI_TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(qry, null)
        val notifis = ArrayList<Notifi>()
        if (cursor.count == 0)
            Toast.makeText(mCtx, "ไม่พบการแจ้งเตือน", Toast.LENGTH_SHORT).show() else {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val notifi = Notifi()
                notifi.notifiID = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFI_ID))
                notifi.notifiName = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFI_NAME))
                notifi.msg = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE))
                notifi.bink = cursor.getInt(cursor.getColumnIndex(COLUMN_BINK))
                notifi.stylealert = cursor.getInt(cursor.getColumnIndex(COLUMN_STYLE_ALERT))
                notifi.stylesound = cursor.getInt(cursor.getColumnIndex(COLUMN_STYLE_SOUND))
                notifis.add(notifi)
                cursor.moveToNext()
            }
            Toast.makeText(mCtx, "${cursor.count.toString()} การแจ้งเตือน", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        db.close()
        return notifis
    }

    fun addNotifi(mCtx: Context,notifi: Notifi){
        val value = ContentValues()
        value.put(COLUMN_NOTIFI_NAME,notifi.notifiName)
        value.put(COLUMN_BINK,notifi.bink)
        value.put(COLUMN_STYLE_ALERT,notifi.stylealert)
        value.put(COLUMN_STYLE_SOUND,notifi.stylesound)
        value.put(COLUMN_MESSAGE,notifi.msg)
        val db = this.writableDatabase
        try {
            db.insert(NOTIFI_TABLE_NAME,null,value)
            Toast.makeText(mCtx,"เพิ่มการแจ้งเตือนเรียบร้อย",Toast.LENGTH_SHORT).show()

        }catch (e : Exception){
            Toast.makeText(mCtx,e.message,Toast.LENGTH_SHORT).show()
        }
        db.close()

    }


    fun deleteNotifi(notifiID : Int) : Boolean{
        val qry = "Delete From $NOTIFI_TABLE_NAME where $COLUMN_NOTIFI_ID = $notifiID"
        val db  = this.writableDatabase
        var result : Boolean = false
        try {
            // val cursor = db.delete(CUSTOMERS_TABLE_NAME,"$COLUMN_CUSTOMERID = ?", arrayOf(customerID.toString()))
            val cursor = db.execSQL(qry)
            result = true

        }catch (e : Exception){
            Log.e(ContentValues.TAG,"Error Deleting")
        }
        db.close()
        return result
    }

    fun UpdateNotifi(id : String, notifiName : String, bink :  Int,alert : Int, sound : Int,Msg : String) : Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        var result : Boolean = false
        contentValues.put(COLUMN_NOTIFI_NAME, notifiName)
        contentValues.put(COLUMN_BINK, bink)
        contentValues.put(COLUMN_STYLE_ALERT,alert)
        contentValues.put(COLUMN_STYLE_SOUND,sound)
        contentValues.put(COLUMN_MESSAGE,Msg)
        try {
            db.update(NOTIFI_TABLE_NAME, contentValues,"$COLUMN_NOTIFI_ID = ?", arrayOf(id))
            result = true

        }catch (e : Exception){
            Log.e(ContentValues.TAG,e.message)
            result = true
        }
        db.close()
        return result
    }

}