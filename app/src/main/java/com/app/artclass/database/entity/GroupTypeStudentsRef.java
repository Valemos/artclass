package com.app.artclass.database.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.OnConflictStrategy;
import androidx.room.Relation;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"groupTypeId","studentId"},
        indices = @Index(value = {"groupTypeId","studentId"}, unique = true))
public class GroupTypeStudentsRef {
    public long groupTypeId;

    @ForeignKey(entity = Student.class, parentColumns = "studentId", childColumns = "studentId", onDelete = CASCADE)
    public long studentId;

    public GroupTypeStudentsRef(long groupTypeId, long studentId) {
        this.groupTypeId = groupTypeId;
        this.studentId = studentId;
    }
}
