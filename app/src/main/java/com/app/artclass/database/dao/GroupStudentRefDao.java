package com.app.artclass.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.app.artclass.database.entity.GroupTypeStudentsRef;

@Dao
public interface GroupStudentRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GroupTypeStudentsRef ref);

    @Update
    void update(GroupTypeStudentsRef ref);

    @Delete
    void delete(GroupTypeStudentsRef ref);
}
