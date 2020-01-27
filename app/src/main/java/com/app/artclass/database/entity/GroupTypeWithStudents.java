package com.app.artclass.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class GroupTypeWithStudents {
    @Embedded
    public GroupType groupType;

    @Relation(
            parentColumn = "groupTypeId",
            entityColumn = "studentId",
            associateBy = @Junction(GroupTypeStudentsRef.class))
    public List<Student> studentList;
}
