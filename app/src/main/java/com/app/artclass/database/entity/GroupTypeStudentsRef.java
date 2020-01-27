package com.app.artclass.database.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Relation;

import java.util.List;

@Entity(primaryKeys = {"groupTypeId","studentId"},
        indices = @Index(value = {"groupTypeId","studentId"}, unique = true))
public class GroupTypeStudentsRef {
    public long groupTypeId;
    public long studentId;

    public GroupTypeStudentsRef(long groupTypeId, long studentId) {
        this.groupTypeId = groupTypeId;
        this.studentId = studentId;
    }
}
