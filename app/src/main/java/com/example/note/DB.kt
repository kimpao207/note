package com.example.note

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VER) {

    override fun onCreate(p0: SQLiteDatabase?) {

        val project_table_query = ("CREATE TABLE $project_table($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_NAME TEXT,$COL_NEED_PASSWORD INTEGER,$COL_PASSWORD TEXT)")
        p0!!.execSQL(project_table_query)
        val note_table_query = ("CREATE TABLE $note_table($COL_ID INTEGER,$COL_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TITLE TEXT,$COL_NEED_NOTICE INTEGER," +
                "$COL_NOTICE_DATE TEXT,$COL_NOTICE_TIME TEXT,$COL_CONTENT TEXT)")
        p0.execSQL(note_table_query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS $project_table")
        p0.execSQL("DROP TABLE IF EXISTS $note_table")
        onCreate(p0)
    }

    //project
    val allproject: List<project>
        @SuppressLint("Recycle")
        get() {
            val lstproject = ArrayList<project>()
            val selectQuery = "SELECT * FROM $project_table"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToLast()){
                do {
                    val project = project()
                    project.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    project.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    project.need_password = cursor.getInt(cursor.getColumnIndex(COL_NEED_PASSWORD))
                    project.password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD))

                    lstproject.add(project)
                }while (cursor.moveToPrevious())
            }
            db.close()
            return lstproject
        }

    fun findproject(name: String): List<project>{
        val lstproject = ArrayList<project>()
        val selectQuery = "SELECT * FROM $project_table WHERE name like ?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf("%$name%"))
        if(cursor.moveToLast()){
            do {
                val project = project()
                project.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                project.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                project.need_password = cursor.getInt(cursor.getColumnIndex(COL_NEED_PASSWORD))
                project.password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD))

                lstproject.add(project)
            }while (cursor.moveToPrevious())
        }
        db.close()
        return lstproject
    }

    fun getproject(id: Int): project {
        val db = this.writableDatabase
        val tmpproject = project()
        var cursor = db.rawQuery("SELECT * FROM $project_table WHERE id = $id", null)
        cursor.moveToFirst()
        if(cursor.count > 0){
            tmpproject.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
            tmpproject.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
            tmpproject.need_password = cursor.getInt(cursor.getColumnIndex(COL_NEED_PASSWORD))
            tmpproject.password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD))
        }
        db.close()
        return tmpproject
    }

    fun addproject(project: project){
        val db = this.writableDatabase
        val value = ContentValues()
        value.put(COL_NAME, project.name)
        value.put(COL_NEED_PASSWORD, project.need_password)
        if(project.need_password == 1)
            value.put(COL_PASSWORD, project.password)
        else
            value.put(COL_PASSWORD, "no password")

        db.insert(project_table, null, value)
        db.close()
    }

    fun updateproject(project: project){
        val db = this.writableDatabase
        val value = ContentValues()
        value.put(COL_NAME, project.name)
        value.put(COL_NEED_PASSWORD, project.need_password)
        if(project.need_password == 1)
            value.put(COL_PASSWORD, project.password)
        else
            value.put(COL_PASSWORD, "no password")

        db.update(project_table, value, "$COL_ID = ?", arrayOf(project.id.toString()))
        db.close()
    }

    fun deleteproject(id: Int){
        val db = this.writableDatabase
        db.delete(project_table,"$COL_ID = ?", arrayOf(id.toString()))
        db.delete(note_table,"$COL_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun checkrepeatname(name: String, num: Int): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $project_table WHERE name = '$name'", null)
        return cursor.count > num
    }

    //note
    fun allnote(id: Int): ArrayList<note> {
        val total_note = ArrayList<note>()
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id", null)
        if(cursor.moveToLast()){
            do {
                val note = note()
                note.id = id
                note.note_id = cursor.getInt(cursor.getColumnIndex(COL_NOTE_ID))
                note.title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
                note.need_notice = cursor.getInt(cursor.getColumnIndex(COL_NEED_NOTICE))
                note.notice_date = cursor.getString(cursor.getColumnIndex(COL_NOTICE_DATE))
                note.notice_time = cursor.getString(cursor.getColumnIndex(COL_NOTICE_TIME))
                note.content = cursor.getString(cursor.getColumnIndex(COL_CONTENT))

                total_note.add(note)
            }while (cursor.moveToPrevious())
        }
        db.close()
        return total_note
    }

    fun findnote(id: Int, name: String): ArrayList<note> {
        val total_note = ArrayList<note>()
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND title like ?", arrayOf("%$name%"))
        if(cursor.moveToLast()){
            do {
                val note = note()
                note.id = id
                note.note_id = cursor.getInt(cursor.getColumnIndex(COL_NOTE_ID))
                note.title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
                note.need_notice = cursor.getInt(cursor.getColumnIndex(COL_NEED_NOTICE))
                note.notice_date = cursor.getString(cursor.getColumnIndex(COL_NOTICE_DATE))
                note.notice_time = cursor.getString(cursor.getColumnIndex(COL_NOTICE_TIME))
                note.content = cursor.getString(cursor.getColumnIndex(COL_CONTENT))

                total_note.add(note)
            }while (cursor.moveToPrevious())
        }
        db.close()
        return total_note
    }

    fun add_note(note: note){
        val db = this.writableDatabase
        val value = ContentValues()
        value.put(COL_ID, note.id)
        value.put(COL_TITLE, note.title)
        value.put(COL_NEED_NOTICE, note.need_notice)
        value.put(COL_NOTICE_DATE, note.notice_date)
        value.put(COL_NOTICE_TIME, note.notice_time)
        value.put(COL_CONTENT, note.content)

        db.insert(note_table, null, value)
        db.close()
    }

    fun update_note(note: note){
        val db = this.writableDatabase
        val value = ContentValues()
        value.put(COL_ID, note.id)
        value.put(COL_NOTE_ID, note.note_id)
        value.put(COL_TITLE, note.title)
        value.put(COL_NEED_NOTICE, note.need_notice)
        value.put(COL_NOTICE_DATE, note.notice_date)
        value.put(COL_NOTICE_TIME, note.notice_time)
        value.put(COL_CONTENT, note.content)

        db.update(note_table, value, "$COL_ID = ? AND $COL_NOTE_ID = ?", arrayOf(note.id.toString(), note.note_id.toString()))
        db.close()
    }

    fun delete_note(id: Int, note_id: Int){
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND note_id = $note_id", null)
        if(cursor.count in 1..1)
            db.delete(note_table,"$COL_ID = ? AND $COL_NOTE_ID = ?", arrayOf(id.toString(), note_id.toString()))
        db.close()
    }

    fun update_after_alarm(id: Int, note_id: Int){
        val db = this.writableDatabase
        val value = ContentValues()
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND note_id = $note_id", null)
        if(cursor.count > 0) {
            cursor.moveToFirst()
            value.put(COL_ID, id)
            value.put(COL_NOTE_ID, note_id)
            value.put(COL_TITLE, cursor.getString(cursor.getColumnIndex(COL_TITLE)))
            value.put(COL_NEED_NOTICE, 0)
            value.put(COL_NOTICE_DATE, "無")
            value.put(COL_NOTICE_TIME, "無")
            value.put(COL_CONTENT, cursor.getString(cursor.getColumnIndex(COL_CONTENT)))
        }
        cursor.close()

        db.update(note_table, value, "$COL_ID = ? AND $COL_NOTE_ID = ?", arrayOf(id.toString(), note_id.toString()))
        db.close()
    }

    fun get_note(id: Int, note_id: Int): note{
        val db = this.writableDatabase
        val tmpnote = note()
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND note_id = $note_id", null)
        cursor.moveToFirst()
        if(cursor.count > 0){
            tmpnote.id = id
            tmpnote.note_id = note_id
            tmpnote.title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
            tmpnote.need_notice = cursor.getInt(cursor.getColumnIndex(COL_NEED_NOTICE))
            tmpnote.notice_date = cursor.getString(cursor.getColumnIndex(COL_NOTICE_DATE))
            tmpnote.notice_time = cursor.getString(cursor.getColumnIndex(COL_NOTICE_TIME))
            tmpnote.content = cursor.getString(cursor.getColumnIndex(COL_CONTENT))
        }
        return tmpnote
    }

    fun get_note_id(id: Int, name: String): Int{
        val db = this.writableDatabase
        val tmpnote = note()
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND title = '${name}'", null)
        cursor.moveToFirst()
        if(cursor.count > 0){
            return  cursor.getInt(cursor.getColumnIndex(COL_NOTE_ID))
        }
        else
            return 0
    }

    fun checkrepeattitle(id: Int, title: String, num: Int): Boolean{
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $note_table WHERE id = $id AND title = '${title}'", null)
        return cursor.count > num
    }


    companion object{
        private val DATABASE_NAME = "record.db"
        private val DATABASE_VER = 1

        private val project_table = "project"
        private val COL_ID = "id"
        private val COL_NAME = "name"
        private val COL_NEED_PASSWORD = "need_password"
        private val COL_PASSWORD = "password"

        private val note_table = "note"
        //COL_ID
        private val COL_NOTE_ID = "note_id"
        private val COL_TITLE = "title"
        private val COL_NEED_NOTICE = "notice"

        private val COL_NOTICE_DATE = "notice_date"
        private val COL_NOTICE_TIME = "notice_time"
        private val COL_CONTENT = "content"
    }
}