package com.app.artclass.list_adapters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.app.artclass.ApplicationViewModel;
import com.app.artclass.UserSettings;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.fragments.DialogHandler;
import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LessonsAdapter extends ArrayAdapter<Lesson> implements LocalAdapter{

    private final int mResource; // layout/item_students_presentlist.xml
    @NonNull
    private final Fragment fragment;
    private List<Lesson> lessonsList;

    public LessonsAdapter(@NonNull Fragment fragment, int resource, @NonNull List<Lesson> lessons) {
        super(Objects.requireNonNull(fragment.getContext()), resource, lessons);
        this.fragment = fragment;

        lessonsList = lessons;
        changeData(lessons, null, null);

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

        nameView.setText(lessonsList.get(position).getStudent().getName());
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

        refreshField(convertView, lessonsList.get(position));

        return convertView;
    }

    private View.OnClickListener decrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson) ;

        incrementHoursWorked(-1, lesson);

        //buttons located in additional container and we need to get outside it
        //then we need to find in parent out text views
        refreshField(((View)view.getParent().getParent()), lesson);
    };

    private View.OnClickListener incrementListener = view -> {
        Lesson lesson = (Lesson) view.getTag(R.id.lesson);

        incrementHoursWorked(1, lesson);

        //buttons located in additional container and we need to get outside it
        //then we need to find in parent out text views
        refreshField(((View)view.getParent().getParent()), lesson);
    };

    private void refreshField(View view, Lesson lesson) {
        TextView hoursLeftText = view.findViewById(R.id.hours_left_view);
        TextView hoursWorkedText = view.findViewById(R.id.hours_worked_view);

        hoursLeftText.setText(String.format(Locale.getDefault(), UserSettings.getInstance().getHoursTextFormat(), lesson.getStudent().getHoursBalance()));
        hoursWorkedText.setText(String.format(Locale.getDefault(), UserSettings.getInstance().getHoursTextFormat(), lesson.getHoursWorked()));
    }

    private void incrementHoursWorked(int howMuchWorked, Lesson lesson){
        Student student = lesson.getStudent();
        int finHoursToWork = student.getHoursBalance() - howMuchWorked;
        int finHoursWorkedToday = lesson.getHoursWorked() + howMuchWorked;

        if(finHoursToWork <= 0){
            DialogHandler.getInstance().AlertDialog(fragment.getContext(), fragment.getContext().getString(R.string.title_alert_abonement), "", null);
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

    public void changeData(List<Lesson> updateLessons, ApplicationViewModel mAppViewModel, GroupType spinnerItem) {
        lessonsList.clear();
        lessonsList.addAll(updateLessons);
        notifyDataSetChanged();

        if(mAppViewModel != null && spinnerItem != null) {
            if (mAppViewModel.getTodayGroupsMap() != null) {
                mAppViewModel.getTodayGroupsMap().get(spinnerItem).removeObservers(fragment.getViewLifecycleOwner());
            }
        }
    }

    public void updateRepository(){
        assert StudentsRepository.getInstance() != null;

        StudentsRepository.getInstance().update(lessonsList);
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }
}
