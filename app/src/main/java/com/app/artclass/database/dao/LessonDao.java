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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Dao
public interface LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson")
    LiveData<List<Lesson>> getAll();

    @Transaction
    @Query("SELECT * FROM lesson WHERE date = :date_time AND group_name=:groupName")
    @TypeConverters({DatabaseConverters.class})
    LiveData<List<Lesson>> getForGroup(LocalDate date_time, String groupName);

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
    @Delete
    void delete(List<Lesson> lessons);

    @Transaction
    @Query("DELETE FROM lesson WHERE date=:date")
    @TypeConverters({DatabaseConverters.class})
    void delete(LocalDate date);

    @Transaction
    @Query("DELETE FROM lesson WHERE date=:date AND group_name=:groupName")
    @TypeConverters({DatabaseConverters.class})
    void delete(LocalDate date, String groupName);

    @Transaction
    @Query("DELETE FROM lesson WHERE date=:date AND group_name=:groupName AND stud_id = :studentId")
    @TypeConverters({DatabaseConverters.class})
    void delete(LocalDate date, String groupName, long studentId);

    @Transaction
    @Query("DELETE FROM lesson WHERE date=:date AND group_name=:groupName AND stud_id IN (:studentIds)")
    @TypeConverters({DatabaseConverters.class})
    void delete( LocalDate date, String groupName, List<Long> studentIds);
}
