package com.app.artclass.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.app.artclass.database.entity.GroupTypeWithStudents;
import com.app.artclass.database.entity.GroupType;

import java.util.List;

@Dao
public interface GroupTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GroupType groupType);

    @Update
    void update(GroupType groupType);

    @Delete
    void delete(GroupType groupType);

    @Query("SELECT * FROM GroupType")
    LiveData<List<GroupType>> getAll();

    @Transaction
    @Query("SELECT * FROM GroupType WHERE groupTypeId=:id")
    LiveData<GroupTypeWithStudents> getGroupTypeWithStudents(long id);
}
