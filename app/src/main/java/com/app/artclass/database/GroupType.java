package com.app.artclass.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalTime;

@Entity(indices = @Index(value = "time",unique = true))
public class GroupType {

    @PrimaryKey(autoGenerate = true)
    int id;

    @TypeConverters({DatabaseConverters.class})
    private LocalTime time;

    String groupName;

    public GroupType(LocalTime time, String groupName) {
        this.time = time;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public LocalTime getTime() { return time; }
}
