package com.app.artclass.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView logTextField = mainView.findViewById(R.id.log_text_view);
        Button showLogBtn = mainView.findViewById(R.id.show_log_btn);
        Button clearLogBtn = mainView.findViewById(R.id.delete_logs_btn);
        Button databaseTestBtn = mainView.findViewById(R.id.database_test_btn);

        showLogBtn.setOnClickListener(v -> logTextField.setText(Logger.getInstance().getLogFileContent()));
        clearLogBtn.setOnClickListener(v -> {
            Logger.getInstance().clearLogFile();
            logTextField.setText("logs cleared");
        });
        databaseTestBtn.setOnClickListener(v -> {
            StudentsRepository.getInstance().resetDatabase(getContext());
            StudentsRepository.getInstance().initDatabaseTest();
        });

        return mainView;
    }

}
