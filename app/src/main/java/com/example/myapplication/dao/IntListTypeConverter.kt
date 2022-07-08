package com.example.myapplication.dao

import androidx.room.TypeConverter

/**
 * company:52TT
 * data:2018/11/15
 * auth:lewis_v
 */
class IntListTypeConverter {
    @TypeConverter
    fun stringToList(string: String):List<Int>{
        val list: MutableList<Int> = ArrayList()
        for (num in string.split(",")){
            if (num.isNotEmpty()){
                list.add(num.toInt())
            }
        }
        return list
    }

    @TypeConverter
    fun listToIntList(list: List<Int>): String{
        return list.joinToString(",")
    }
}