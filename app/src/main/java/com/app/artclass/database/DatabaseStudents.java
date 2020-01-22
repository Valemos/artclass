package com.app.artclass.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.artclass.UserSettings;
import com.app.artclass.database.dao.AbonementDao;
import com.app.artclass.database.dao.GroupTypeDao;
import com.app.artclass.database.dao.LessonDao;
import com.app.artclass.database.dao.StudentDao;
import com.app.artclass.database.entity.Abonement;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class, Lesson.class, Abonement.class, GroupType.class}, version = 8, exportSchema = false)
public abstract class DatabaseStudents extends RoomDatabase {

    public static final String DATABASE_NAME = "students_database.db";
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
                    instance = Room.databaseBuilder(context.getApplicationContext(),DatabaseStudents.class, DatabaseStudents.DATABASE_NAME)
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

            assert StudentsRepository.getInstance() != null;
            UserSettings.getInstance().writeSettingsToRepository(StudentsRepository.getInstance());
        }
    };
}
