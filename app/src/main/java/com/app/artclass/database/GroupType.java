package com.app.artclass.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalTime;

@Entity(indices = @Index(value = "time",unique = true))
public class GroupType {

    @PrimaryKey(autoGenerate = true)
    int id;

    LocalTime time;

    public String getGroupName() {
        return group_name;
    }

    String group_name;

    public GroupType(LocalTime time, String group_name) {
        this.time = time;
        this.group_name = group_name;
    }
}
