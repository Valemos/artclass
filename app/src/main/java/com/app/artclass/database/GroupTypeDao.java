package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface GroupTypeDao {

    @Insert
    void insert(GroupType groupType);

    @Update
    void update(GroupType groupType);

    @Delete
    void delete(GroupType groupType);
}
