package com.alpharays.workshops.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alpharays.workshops.data.entities.User
import com.alpharays.workshops.data.entities.UserWorkshop
import com.alpharays.workshops.data.entities.Workshop


@Database(entities = [User::class, Workshop::class, UserWorkshop::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workshopDao(): WorkshopDao
    abstract fun userWorkshopDao(): UserWorkshopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
