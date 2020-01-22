package com.app.artclass.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.entity.Lesson;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson")
    LiveData<List<Lesson>> getAll();

    @Transaction
    @Query("SELECT * FROM lesson WHERE dateTime = :date_time")
    LiveData<List<Lesson>> getForDateTime(@TypeConverters({DatabaseConverters.class}) LocalDateTime date_time);

    @Transaction
    @Query("SELECT * FROM lesson WHERE stud_name=:name")
    LiveData<List<Lesson>> getForStudent(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

    @Transaction
    @Query("DELETE FROM lesson WHERE dateTime=:dateTime")
    void delete(@TypeConverters({DatabaseConverters.class}) LocalDateTime dateTime);

    @Transaction
    @Query("DELETE FROM lesson WHERE dateTime=:dateTime AND stud_name = :name")
    void delete(@TypeConverters({DatabaseConverters.class}) LocalDateTime dateTime, String name);

    @Transaction
    @Query("DELETE FROM lesson WHERE dateTime=:dateTime AND stud_name IN (:studentNames)")
    void delete(@TypeConverters({DatabaseConverters.class}) LocalDateTime dateTime, List<String> studentNames);
}
