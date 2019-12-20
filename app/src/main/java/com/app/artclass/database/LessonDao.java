package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface LessonDao {

    @Query("SELECT * FROM lesson")
    List<Lesson> getAll();

    @Query("SELECT * FROM lesson WHERE student_id = :student_id")
    List<Lesson> getForStudentId(int student_id);

    @Query("SELECT * FROM lesson WHERE date_time = :date_time")
    List<Lesson> getForDateTime(LocalDateTime date_time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

    @Query("DELETE FROM lesson WHERE date_time=:dateTime")
    void delete(LocalDateTime dateTime);
}
