package com.app.artclass.fragments.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.UserSettings;
import com.app.artclass.WEEKDAY;
import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Student;
import com.app.artclass.list_adapters.LocalAdapter;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogCreationHandler {

    private Runnable proc_positive = null;
    private Runnable proc_negative = null;

    public DialogCreationHandler() {
    }

    // default listeners
    private DialogInterface.OnClickListener defaultPositiveClickListener = (dialog, which) -> {
        if(proc_positive!=null) {
            proc_positive.run();
            Logger.getInstance().appendLog(getClass(),"handled positive runnable");
        }
    };

    private DialogInterface.OnClickListener defaultNegativeClickListener = (dialog, which) -> {
        if(proc_negative!=null) {
            proc_negative.run();
            Logger.getInstance().appendLog(getClass(),"handled negative runnable");
        }
    };

    public AlertDialog ConfirmDeleteObject(Context context, String objectName, Runnable positiveProcedure, Runnable negativeProcedure) {

        proc_positive = positiveProcedure;
        proc_negative = negativeProcedure;

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(context.getString(R.string.title_confirm_delete_student));
        dialog.setMessage(String.format(context.getString(R.string.message_confirm_delete_placeholder),objectName));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK),defaultPositiveClickListener);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),defaultNegativeClickListener);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        return dialog;
    }

    public AlertDialog AlertDialog(Context context, String title, String message, Runnable positive_action){
        proc_positive = positive_action;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setPositiveButton(R.string.label_OK,defaultPositiveClickListener)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .create();
    }


    public TimePickerDialog TimePicker(Context context, TextView timeSelectorView, LocalTime time, TimePickerDialog.OnTimeSetListener timeSetListener) {
        return new TimePickerDialog(context, timeSetListener, time.getHour(), time.getMinute(), true);
    }

    public DatePickerDialog DatePicker(Context context, final TextView outputTextView, @Nullable LocalDate startDate) {
        if(startDate == null)
            startDate = LocalDate.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, null, startDate.getYear(), startDate.getMonthValue()-1, startDate.getDayOfMonth());
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.label_OK), (dialog, which) -> {
                LocalDate outputDate = LocalDate.of(
                        datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth()+1,
                        datePickerDialog.getDatePicker().getDayOfMonth());
                outputTextView.setText(outputDate.format(DatabaseConverters.getDateFormatter()));
                outputTextView.setTag(R.id.date,outputDate);
            });

        Logger.getInstance().appendLog(getClass(),"DatePicker showed dialog");

        return datePickerDialog;
    }

}