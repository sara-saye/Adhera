package com.gpproject.adhera.treatment.todo_list.tododb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class],
    version = 3,
    exportSchema = false,
)
@TypeConverters(TaskConverters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // This migration handles the addition of the 'milestones' column in version 3.
                // If other fields were added, they should be included here.
                // Using fallbackToDestructiveMigration as a secondary safety measure.
                try {
                    db.execSQL("ALTER TABLE tasks ADD COLUMN milestones TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {
                    // Column might already exist or another error occurred
                }
            }
        }

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "adhera_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}