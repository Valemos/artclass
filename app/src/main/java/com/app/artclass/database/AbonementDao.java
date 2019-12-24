package com.app.artclass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface AbonementDao {

    @Insert
    void insert(Abonement abonement);

    @Update
    void update(Abonement abonement);

    @Delete
    void delete(Abonement abonement);
}
