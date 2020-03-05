package com.app.artclass.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.artclass.Logger;
import com.app.artclass.database.entity.GroupTypeStudentsRef;

public class DatabaseMigrations {

    public static final Migration[] getAllMigrations(){
        return new Migration[]{};
    }

    static Migration DB_Migration_delete_tables = new Migration(18,19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE Abonement");
            database.execSQL("DROP TABLE GroupTypeStudentsRef");
        }
    };
}
