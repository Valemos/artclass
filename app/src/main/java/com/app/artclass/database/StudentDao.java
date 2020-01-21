package com.app.artclass.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {

    @Transaction
    @Query("SELECT * FROM student")
    LiveData<List<Student>> getAll();

    @Transaction
    @Query("SELECT * FROM student WHERE name=:name LIMIT 1")
    LiveData<Student> get(String name);

    @Transaction
    @Query("SELECT * FROM student WHERE name IN (:names)")
    LiveData<List<Student>> getAllByNames(List<String> names);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);
}
