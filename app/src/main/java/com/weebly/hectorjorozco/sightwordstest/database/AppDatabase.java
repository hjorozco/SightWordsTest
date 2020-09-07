package com.weebly.hectorjorozco.sightwordstest.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.weebly.hectorjorozco.sightwordstest.utils.DateConverter;

// Class that defines a method to create a RoomDatabase that will contain two tables:
// "students" and "tests"

@Database(entities = {StudentEntry.class, TestEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "sightwordstestdb";
    private static AppDatabase sDatabaseInstance;

    public static AppDatabase getInstance(Context context) {
        if (sDatabaseInstance == null) {
            synchronized (LOCK) {
                // Create a new database instance
                sDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return sDatabaseInstance;
    }

    // Returns a Data Access Object used to access the "students" table.
    public abstract StudentDao studentDao();

    // Returns a Data Access Object used to access the "tests" table.
    public abstract TestDao testDao();

}
