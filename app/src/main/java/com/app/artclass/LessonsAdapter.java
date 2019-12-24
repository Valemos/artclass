package com.app.artclass;

import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.app.artclass.database.DatabaseManager;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LessonsAdapter extends LocalAdapter<Lesson> {

    private final Context mContext;
    private final int mResource; // layout/item_students_presentlist.xml
    private final List<Pair<Lesson, Student>> lessonStudentPairList;
    DatabaseManager databaseManager;

    public LessonsAdapter(@NonNull Context context, int resource, @NonNull List<Lesson> objects) {
        super(context, resource, objects);

        // needs to be initialized before lessonStudentPairList
        // because it is used by init cycle
        databaseManager = DatabaseManager.getInstance();

        mContext = context;
        mResource = resource;

        lessonStudentPairList = new ArrayList<>();
        for (Lesson lessonIter : objects) {
            lessonStudentPairList.add(
                    new Pair<>(lessonIter, databaseManager.getStudent(lessonIter.getName())));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView hoursTextView = convertView.findViewById(R.id.hours_left_view);
        TextView hoursWorkedTextView = convertView.findViewById(R.id.hours_worked_view);

        // set buttons
        ImageButton btn_decrement = convertView.findViewById(R.id.minus_hour_btn);
        ImageButton btn_increment = convertView.findViewById(R.id.plus_hour_btn);

        btn_increment.setTag(lessonStudentPairList.get(position));
        btn_decrement.setTag(lessonStudentPairList.get(position));

        // button listener 1
        btn_decrement.setOnClickListener(decrementListener);

        // button listener 2
        btn_increment.setOnClickListener(incrementListener);

        nameView.setText(lessonStudentPairList.get(position).second
                .getName().split(" ", 2)[0]);
        hoursTextView.setText(String.format("%d h", lessonStudentPairList.get(position).second.getHoursBalance()));
        hoursWorkedTextView.setText(String.format("%d h", lessonStudentPairList.get(position).first.getHoursWorked()));

        return convertView;
    }

    View.OnClickListener decrementListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Pair<Lesson,Student>lessonStudent= (Pair<Lesson,Student>) view.getTag();
            FragmentManager fragmentManager = (FragmentManager) ((View) view.getParent().getParent()).getTag();

            incrementHoursWorked(-1, lessonStudent, fragmentManager);

            refreshFields(view, lessonStudent);
        }
    };

    View.OnClickListener incrementListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Pair<Lesson,Student>lessonStudent= (Pair<Lesson,Student>) view.getTag();
            FragmentManager fragmentManager = (FragmentManager) ((View) view.getParent().getParent()).getTag();

            incrementHoursWorked(1, lessonStudent, fragmentManager);

            refreshFields(view, lessonStudent);
        }
    };

    private void refreshFields(View view, Pair<Lesson, Student> lessonStudent) {

        //buttons located in additional container and we need to get outside it
        //then we need to find in parent out text views
        TextView hoursLeftText = ((View) view.getParent().getParent()).findViewById(R.id.hours_left_view);
        TextView hoursWorkedText = ((View) view.getParent().getParent()).findViewById(R.id.hours_worked_view);

        hoursLeftText.setText(String.format(Locale.getDefault(), "%d h", lessonStudent.second.getHoursBalance()));
        hoursWorkedText.setText(String.format(Locale.getDefault(), "%d h", lessonStudent.first.getHoursWorked()));
    }

    private void incrementHoursWorked(int howMuchWorked, Pair<Lesson,Student> lessonStudentPair, FragmentManager fManager){
        Lesson lesson = lessonStudentPair.first;
        Student student = lessonStudentPair.second;

        int finHoursToWork = student.getHoursBalance() - howMuchWorked;
        int finHoursWorkedToday = lesson.getHoursWorked() + howMuchWorked;

        if(finHoursToWork <= 0){
            DialogHandler dialogHandler = new DialogHandler(mContext);
            dialogHandler.AlertDialog(mContext.getString(R.string.title_alert_abonement),mContext.getString(R.string.message_abonement_finished));
        }

        if (finHoursToWork < 0) {
            finHoursToWork = 0;
            finHoursWorkedToday = lesson.getHoursWorked();
        }

        if (finHoursWorkedToday < 0){
            finHoursToWork = student.getHoursBalance();
            finHoursWorkedToday = 0;
        }

        lesson.setHoursWorked(finHoursWorkedToday);
        student.setHoursBalance(finHoursToWork);
        databaseManager.update(student);
        databaseManager.update(lesson);
    }
}
