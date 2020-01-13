package com.app.artclass.database;


import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.artclass.UserSettings;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class,Lesson.class,Abonement.class,GroupType.class}, version = 4, exportSchema = false)
public abstract class DatabaseStudents extends RoomDatabase {

    public static final String DATABASE_NAME = "studentsData";
    private static volatile DatabaseStudents instance;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract StudentDao studentDao();
    public abstract LessonDao lessonDao();
    public abstract AbonementDao abonementDao();
    public abstract GroupTypeDao groupTypeDao();

    static DatabaseStudents getDatabase(final Context context) {
        if (instance == null) {
            synchronized (DatabaseStudents.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),DatabaseStudents.class, "word_database")
                            .addCallback(DatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback DatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
