package com.app.artclass;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LessonsAdapter extends LocalAdapter<Lesson> {

    private final Context mContext;
    private final int mResource; // layout/item_students_presentlist.xml
    @NonNull
    private final Fragment fragment;
    private final List<Lesson> lessonsList;
    private List<Student> studentList;

    public LessonsAdapter(@NonNull Fragment fragment, int resource, @NonNull List<Lesson> lessons) {
        super(Objects.requireNonNull(fragment.getContext()), resource, lessons);
        this.fragment = fragment;
        lessonsList = lessons;
        mContext = fragment.getContext();
        mResource = resource;
    }

    @SuppressLint({"DefaultLocale", "ViewHolder"})
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

        List<String> studentNames = new ArrayList<>();
        lessonsList.forEach(lesson -> studentNames.add(lesson.getStudentName()));

        StudentsRepository.getInstance().getStudentsForNames(studentNames).observe(fragment,students -> {
            this.studentList = students;

            btn_increment.setTag(R.id.student,studentList.get(position));
            btn_increment.setTag(R.id.lesson,lessonsList.get(position));
            btn_decrement.setTag(R.id.student,studentList.get(position));
            btn_decrement.setTag(R.id.lesson,lessonsList.get(position));

            // button listener 1
            btn_decrement.setOnClickListener(decrementListener);

            // button listener 2
            btn_increment.setOnClickListener(incrementListener);

            nameView.setText(studentList.get(position).getName().split(" ", 2)[0]);
            hoursTextView.setText(String.format("%d h", studentList.get(position).getHoursBalance()));
            hoursWorkedTextView.setText(String.format("%d h", lessonsList.get(position).getHoursWorked()));
        });

        return convertView;
    }

    private View.OnClickListener decrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson) ;
        Student student= (Student) view.getTag(R.id.student);

        incrementHoursWorked(-1, lesson, student);

        refreshFields(view, lesson, student);
    };

    private View.OnClickListener incrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson) ;
        Student student= (Student) view.getTag(R.id.student);

        incrementHoursWorked(1, lesson,student);

        refreshFields(view, lesson, student);
    };

    private void refreshFields(View view, Lesson lesson, Student student) {

        //buttons located in additional container and we need to get outside it
        //then we need to find in parent out text views
        TextView hoursLeftText = ((View) view.getParent().getParent()).findViewById(R.id.hours_left_view);
        TextView hoursWorkedText = ((View) view.getParent().getParent()).findViewById(R.id.hours_worked_view);

        hoursLeftText.setText(String.format(Locale.getDefault(), "%d h", student.getHoursBalance()));
        hoursWorkedText.setText(String.format(Locale.getDefault(), "%d h", lesson.getHoursWorked()));
    }

    private void incrementHoursWorked(int howMuchWorked, Lesson lesson,Student student){

        int finHoursToWork = student.getHoursBalance() - howMuchWorked;
        int finHoursWorkedToday = lesson.getHoursWorked() + howMuchWorked;

        if(finHoursToWork <= 0){
            DialogHandler.getInstance().AlertDialog(fragment.getContext(),mContext.getString(R.string.title_alert_abonement),mContext.getString(R.string.message_abonement_finished));
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
        StudentsRepository.getInstance().update(student);
        StudentsRepository.getInstance().update(lesson);
    }
}
