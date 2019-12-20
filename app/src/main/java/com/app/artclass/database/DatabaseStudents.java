package com.app.artclass.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Student.class,Lesson.class}, version = 1)
public abstract class DatabaseStudents extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract LessonDao lessonDao();
}
