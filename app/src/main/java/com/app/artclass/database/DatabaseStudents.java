package com.app.artclass.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Student.class,Lesson.class,Abonement.class,GroupType.class}, version = 1, exportSchema = false)
public abstract class DatabaseStudents extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract LessonDao lessonDao();
    public abstract AbonementDao abonementDao();
    public abstract GroupTypeDao groupTypeDao();
}
