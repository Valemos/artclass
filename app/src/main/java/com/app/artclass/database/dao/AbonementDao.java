package com.app.artclass.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.artclass.database.entity.Abonement;

import java.util.List;

@Dao
public interface AbonementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Abonement abonement);

    @Update
    void update(Abonement abonement);

    @Delete
    void delete(Abonement abonement);

    @Query("SELECT * FROM abonement")
    LiveData<List<Abonement>> getAll();
}
