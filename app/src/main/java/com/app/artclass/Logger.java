package com.app.artclass;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.artclass.fragments.dialog.ConfirmDeleteObjectDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this class writes to file error notes
 */
public class Logger {

    private static Logger instance;

    private static File logFileDir;
    private File logFile;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService logWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Logger getInstance(Context context) {
        if(instance == null){
            instance = new Logger(context);
        }
        return instance;
    }

    @NonNull
    public static Logger getInstance(){return instance;}

    private Logger(Context context) {
        try {
            initDirectory(context);
        } catch (Exception e) {
            Log.w("logger_error", "Error occured in Logger during initialization", e);
        }
    }

    public void initDirectory(Context context){
        logFileDir = new File (context.getApplicationInfo().dataDir + "/logs");
        if(!logFileDir.exists())
            logFileDir.mkdirs();
    }

    public void appendLog(String logText){
        try {
            logWriteExecutor.execute(() -> {
                try {
                    logFile = new File(logFileDir, "logs.txt");
                    if (!logFile.exists())
                        logFile.createNewFile();

                    // debug commands
                    //adb pull /data/user/0/com.artclass.students_manager/logs/logs.txt D:\coding\Android\ArtClass\test_files
                    //adb push D:\coding\Android\ArtClass\test_files\logs.txt /storage/emulated/0/logs/logs.txt

                    //BufferedWriter for performance, true to set append to file flag
                    String s = logFile.getAbsolutePath();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
                    bw.write(logText);
                    bw.newLine();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void appendLog(Class aClass, String message) {
        appendLog(aClass.getName()+" : "+message);
    }

    public String getLogFileContent() {
        try {
            logFile = new File(logFileDir, "logs.txt");
            if (!logFile.exists())
                logFile.createNewFile();

            Scanner sc = new Scanner(new FileReader(logFile));
            StringBuilder outStr = new StringBuilder();
            while (sc.hasNextLine()) {
                outStr.append(sc.nextLine());
                outStr.append('\n');
            }
            sc.close();
            return outStr.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void clearLogFile() {
        try {
            logFile = new File(logFileDir, "logs.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }else {
                PrintWriter writer = new PrintWriter(logFile);
                writer.print("");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
