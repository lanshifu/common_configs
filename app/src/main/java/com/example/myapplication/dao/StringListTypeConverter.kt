package com.example.myapplication.dao

import androidx.room.TypeConverter

class StringListTypeConverter {
    @TypeConverter
    fun listToString(list: List<String>): String {
        return list.joinToString(", ")
    }
    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.split(", ")
    }
}