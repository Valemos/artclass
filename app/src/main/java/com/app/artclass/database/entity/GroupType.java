package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.database.DatabaseConverters;

import java.time.LocalTime;

@Entity(indices = @Index(value = "groupName",unique = true))
public class GroupType {
    @TypeConverters({DatabaseConverters.class})
    private LocalTime time;

    @NonNull
    @PrimaryKey
    String groupName;

    public GroupType(LocalTime time, String groupName) {
        this.time = time;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public LocalTime getTime() { return time; }

    @Override
    public String toString() {
        return groupName;
    }
}
