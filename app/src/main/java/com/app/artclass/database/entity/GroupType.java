package com.app.artclass.database.entity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.WEEKDAY;
import com.app.artclass.database.DatabaseConverters;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity(indices = @Index(value = {"time","weekday","name"},unique = true))
public class GroupType implements Comparable {

    @PrimaryKey(autoGenerate = true)
    private long groupTypeId;

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private LocalTime time;

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private WEEKDAY weekday;

    @NonNull
    private String name;

    public GroupType(LocalTime time, @NonNull WEEKDAY weekday, String name) {
        this.time = time;
        this.weekday = weekday;
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

    @NonNull
    public WEEKDAY getWeekday() {
        return weekday;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setWeekday(@NonNull WEEKDAY weekday) {
        this.weekday = weekday;
    }

    public void setGroupTypeId(long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public long getGroupTypeId() {
        return groupTypeId;
    }

    public static GroupType getNoGroup(){
        return UserSettings.getInstance().getNoGroup();
    }

    public static GroupType getNoGroup(Context context){
        return UserSettings.getInstance().getNoGroup(context);
    }

    @Override
    public int compareTo(Object o) {
        try{
            return time.compareTo(((GroupType)o).time) + weekday.compareTo(((GroupType)o).getWeekday())*100;
        }catch (Exception e){
            return 0;
        }
    }
}
