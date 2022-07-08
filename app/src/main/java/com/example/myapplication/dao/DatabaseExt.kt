package com.example.myapplication.dao

import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase

fun SupportSQLiteDatabase.isTableExits(tableName: String): Boolean {
    val cursor = query("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name=?", arrayOf(tableName))
    return try {
        cursor.count > 0
    } catch (e: Exception) {
        Log.e("sqlite", "isTableExits $tableName failed, ", e)
        false
    } finally {
        cursor.close()
    }
}