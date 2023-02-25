package com.alpharays.workshops.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson

class WorkShopTypeConverter {
    @TypeConverter
    fun fromWorkshop(workshop: Workshop): String {
        val gson = Gson()
        return gson.toJson(workshop)
    }

    @TypeConverter
    fun toWorkshop(json: String): Workshop {
        val gson = Gson()
        return gson.fromJson(json, Workshop::class.java)
    }
}
