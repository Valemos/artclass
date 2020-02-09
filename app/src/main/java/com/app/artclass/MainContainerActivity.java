package com.app.artclass;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.app.artclass.database.BackupManager;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;

public class MainContainerActivity extends AppCompatActivity {

    private int signInRequestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // very important
        // must be called before using singleton classes
        initBaseClasses(getApplication());

        // start working with application
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account==null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
            startActivityForResult(signInIntent, signInRequestCode);
        }
        else{
            UserSettings.getInstance().initGoogleAccount(account);
            Intent switchToMainApp = new Intent(this, MainActivity.class);
            switchToMainApp.putExtra(MainActivity.signInSuccessfullTag, true);
            startActivity(switchToMainApp);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        UserSettings.getInstance().initGoogleAccount(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == signInRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            UserSettings.getInstance().initGoogleAccount(account);

            Intent switchToMainApp = new Intent(this,MainActivity.class);
            switchToMainApp.putExtra(MainActivity.signInSuccessfullTag, true);
            startActivity(switchToMainApp);
        } catch (ApiException e) {
            Log.println(Log.ERROR, "Sign In problem","signInResult:failed code=" + e.getStatusCode());
            Logger.getInstance().appendLog("Sign In problem: signInResult:failed code=" + e.getStatusCode());
            Intent switchToMainApp = new Intent(this,MainActivity.class);
            switchToMainApp.putExtra(MainActivity.signInSuccessfullTag, false);
            startActivity(switchToMainApp);
        }
    }

    private void initBaseClasses(Application application) {
        StudentsRepository.getInstance(application);
        Logger.getInstance(this);
        Logger.getInstance(this).appendLog(LocalDateTime.now().format(DatabaseConverters.getDateTimeFormatter())+": start complete");
    }
}
