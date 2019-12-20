package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {

    @Query("SELECT * FROM student")
    List<Student> getAll();

    @Query("SELECT * FROM student WHERE name=:name")
    Student get(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);
}
