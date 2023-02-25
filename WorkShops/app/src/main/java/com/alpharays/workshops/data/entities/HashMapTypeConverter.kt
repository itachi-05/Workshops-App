package com.alpharays.workshops.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HashMapTypeConverter {
    @TypeConverter
    fun fromHashMap(value: HashMap<String, String>): String {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    @TypeConverter
    fun toHashMap(value: String): HashMap<String, String> {
        val gson = Gson()
        val type = object : TypeToken<HashMap<String, String>>() {}.type
        return gson.fromJson(value, type)
    }
}
