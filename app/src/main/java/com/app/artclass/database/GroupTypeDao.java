package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

@Dao
public interface GroupTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GroupType groupType);

    @Update
    void update(GroupType groupType);

    @Delete
    void delete(GroupType groupType);
}
