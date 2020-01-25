package com.app.artclass.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.artclass.Logger;

public class DatabaseMigrations {

    public static final Migration[] getAllMigrations(){
        return new Migration[]{};
    }

    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE new_student (id TEXT, dismissCount REAL, PRIMARY KEY(id))\"");
            Logger.getInstance().appendLog(this.getClass().getName()+": migration complete");
        }
    };
}
