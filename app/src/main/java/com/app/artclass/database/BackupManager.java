package com.app.artclass.database;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import com.app.artclass.Logger;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;

public class BackupManager extends BackupAgent {

    private static BackupManager classInstance;
    private static GoogleSignInAccount userGoogleAccount;

    public static BackupManager getInstance(){

        if(classInstance==null){
            classInstance = new BackupManager();
        }

        return classInstance;
    }

    public BackupManager() {
        Logger.getInstance().appendLog(getClass(),"init");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        Logger.getInstance().appendLog(getClass(),"backup");


    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        Logger.getInstance().appendLog(getClass(),"restore");


    }

//    public void initGoogleAccount(GoogleSignInAccount account){
//        if(account!=null) {
//            userGoogleAccount = account;
//            Logger.getInstance().appendLog(getClass(),"account updated");
//        }else{
//            Logger.getInstance().appendLog(getClass(),"account not found");
//        }
//    }
}
