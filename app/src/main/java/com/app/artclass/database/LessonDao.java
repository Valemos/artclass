package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson")
    List<Lesson> getAll();

    @Transaction
    @Query("SELECT * FROM lesson WHERE idStudent = :studentId")
    List<Lesson> getForStudentId(int studentId);

    @Transaction
    @Query("SELECT * FROM lesson WHERE dateTime = :date_time")
    List<Lesson> getForDateTime(@TypeConverters({DatabaseConverters.class}) LocalDateTime date_time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

    @Transaction
    @Query("DELETE FROM lesson WHERE dateTime=:dateTime")
    void delete(@TypeConverters({DatabaseConverters.class}) LocalDateTime dateTime);

    @Transaction
    @Query("DELETE FROM lesson WHERE dateTime=:dateTime AND idStudent = :idStudent")
    void delete(@TypeConverters({DatabaseConverters.class}) LocalDateTime dateTime, int idStudent);
}
