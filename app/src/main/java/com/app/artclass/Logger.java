package com.app.artclass;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * this class writes to file error notes
 */
public class Logger {

    private static Logger instance;

    private static String logFilePath;

    public static Logger getInstance(MainActivity mainActivity) {
        if(instance == null){
            instance = new Logger(mainActivity);
        }
        return instance;
    }

    @NonNull
    public static Logger getInstance(){return instance;}

    private Logger(MainActivity mainActivity) {
        try {
            logFilePath = mainActivity.getFilesDir().getAbsolutePath() + File.separator +"logs/log.txt";
        } catch (Exception e) {
            Log.w("logger_error", "Error occured in Logger during initialization", e);
        }
    }

    public void appendLog(String logText)
    {
        File logFile = new File(logFilePath);
        if (!logFile.exists())
        {
            try
            {
                logFile.mkdir();
                logFile.createNewFile();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(logText);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
