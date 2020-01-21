package com.app.artclass.list_adapters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.app.artclass.fragments.DialogHandler;
import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.Lesson;
import com.app.artclass.database.Student;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LessonsAdapter extends LocalAdapter<Lesson> {

    private final int mResource; // layout/item_students_presentlist.xml
    @NonNull
    private final Fragment fragment;
    private List<Lesson> lessonsList;
    private SparseArray<Student> studentArray = new SparseArray<>();
    private LiveData<List<Student>> allStudentsListData;
    private SparseArray<View> listViews = new SparseArray<>();

    public LessonsAdapter(@NonNull Fragment fragment, int resource, @NonNull List<Lesson> lessons) {
        super(Objects.requireNonNull(fragment.getContext()), resource, lessons);
        this.fragment = fragment;

        lessonsList = lessons;
        updateData(lessons);

        mResource = resource;
    }

    @SuppressLint({"DefaultLocale", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView hoursWorkedTextView = convertView.findViewById(R.id.hours_worked_view);

        nameView.setText(String.valueOf(lessonsList.get(position).getStudentName()));
        hoursWorkedTextView.setText(String.valueOf(lessonsList.get(position).getHoursWorked()));

        // set buttons
        ImageButton btn_decrement = convertView.findViewById(R.id.minus_hour_btn);
        ImageButton btn_increment = convertView.findViewById(R.id.plus_hour_btn);
        btn_decrement.setOnClickListener(decrementListener);
        btn_increment.setOnClickListener(incrementListener);

        //set tags for buttons
        btn_decrement.setTag(R.id.lesson,lessonsList.get(position));
        btn_decrement.setTag(R.id.student_pos, position);
        btn_increment.setTag(R.id.lesson,lessonsList.get(position));
        btn_increment.setTag(R.id.student_pos, position);

        listViews.put(position, convertView);
        return convertView;
    }

    private View.OnClickListener decrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson) ;
        int student_pos = (int) view.getTag(R.id.student_pos);

        incrementHoursWorked(-1, lesson, studentArray.get(student_pos));

        refreshField(((View)view.getParent().getParent()), lesson, studentArray.get(student_pos));
    };

    private View.OnClickListener incrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson) ;
        int student_pos = (int) view.getTag(R.id.student_pos);

        incrementHoursWorked(1, lesson, studentArray.get(student_pos));

        refreshField(((View)view.getParent().getParent()), lesson, studentArray.get(student_pos));
    };

    private void refreshField(View view, Lesson lesson, Student student) {

        //buttons located in additional container and we need to get outside it
        //then we need to find in parent out text views
        TextView hoursLeftText = view.findViewById(R.id.hours_left_view);
        TextView hoursWorkedText = view.findViewById(R.id.hours_worked_view);

        hoursLeftText.setText(String.format(Locale.getDefault(), "%d h", student.getHoursBalance()));
        hoursWorkedText.setText(String.format(Locale.getDefault(), "%d h", lesson.getHoursWorked()));
    }

    private void incrementHoursWorked(int howMuchWorked, Lesson lesson, Student student){

        int finHoursToWork = student.getHoursBalance() - howMuchWorked;
        int finHoursWorkedToday = lesson.getHoursWorked() + howMuchWorked;

        if(finHoursToWork <= 0){
            DialogHandler.getInstance().AlertDialog(fragment.getContext(), fragment.getContext().getString(R.string.title_alert_abonement), fragment.getContext().getString(R.string.message_abonement_finished));
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
    }

    public void updateData(List<Lesson> updateLessons) {
        lessonsList.clear();
        lessonsList.addAll(updateLessons);

        if(lessonsList.size() > 0){
            SparseArray<String> studentNames = new SparseArray<>();
            for (int i = 0; i < lessonsList.size(); i++) {
                studentNames.put(i, lessonsList.get(i).getStudentName());
            }

            allStudentsListData = StudentsRepository.getInstance().getAllStudents();
            allStudentsListData.observe(fragment.getViewLifecycleOwner(), allStudents -> {
                for (int i = 0; i < studentNames.size(); i++) {
                    int key = studentNames.keyAt(i);
                    studentArray.put(key,
                            allStudents.stream().filter(student -> student.getName().equals(studentNames.get(key)))
                                    .findFirst().orElse(null));
                }

                for (int i = 0; i < listViews.size() && i < lessonsList.size(); i++) {
                    int key = listViews.keyAt(i);
                    refreshField(listViews.get(key), lessonsList.get(key), studentArray.get(key));
                }
            });
        }
        notifyDataSetChanged();
    }

    public void updateRepository(){
        for (int i = 0; i < studentArray.size(); i++) {
            int key = studentArray.keyAt(i);

            if(studentArray.get(key)!=null)
                StudentsRepository.getInstance().update(studentArray.get(key));
        }
        lessonsList.forEach(lesson -> StudentsRepository.getInstance().update(lesson));
    }
}
