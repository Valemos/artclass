package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.database.DatabaseConverters;

import java.time.LocalTime;

@Entity(indices = @Index(value = "name",unique = true))
public class GroupType {
    @TypeConverters({DatabaseConverters.class})
    private LocalTime time;

    @NonNull
    @PrimaryKey
    private String name;

    public GroupType(LocalTime time, String name) {
        this.time = time;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTime() { return time; }

    @Override
    public String toString() {
        return name;
    }

    public static GroupType getNoGroup(){
        return new GroupType(LocalTime.of(0,0), "нет группы");
    }
}
