package com.gpproject.adhera.treatment.todo_list.tododb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [_root_ide_package_.com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(_root_ide_package_.com.gpproject.adhera.treatment.todo_list.tododb.TaskConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val taskDao: com.gpproject.adhera.treatment.todo_list.tododb.TaskDao

    companion object {
        @Volatile
        private var INSTANCE: com.gpproject.adhera.treatment.todo_list.tododb.AppDatabase? = null

        fun getDatabase(context: Context): com.gpproject.adhera.treatment.todo_list.tododb.AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    _root_ide_package_.com.gpproject.adhera.treatment.todo_list.tododb.AppDatabase::class.java,
                    "adhera_database" // اسم ملف الداتابيز في الميموري
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}