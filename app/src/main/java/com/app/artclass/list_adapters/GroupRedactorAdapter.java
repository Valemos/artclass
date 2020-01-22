package com.app.artclass.list_adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.fragments.StudentCard;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupRedactorAdapter extends LocalAdapter<Lesson> implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

    private final Fragment fragment;
    private FragmentManager fragmentManager;
    private List<Lesson> lessonsList;
    private List<View> elementViews;
    private int elemLayout;
    private Context mContext;
    private StudentsRepository studentsRepository;

    public GroupRedactorAdapter(@NonNull Context context, int resource, @NonNull List<Lesson> objects, Fragment fragment) {
        super(context, resource, objects);
        this.fragment = fragment;
        studentsRepository = StudentsRepository.getInstance();
        this.fragmentManager = fragment.getFragmentManager();

        mContext = context;
        elemLayout = resource;
        lessonsList = objects;
        elementViews=new ArrayList<>();
    }

    @Override
    public void refreshFields() {
        for(int i = 0; i < lessonsList.size(); i++) {
            refreshElementView(elementViews.get(i), lessonsList.get(i));
        }
    }

    private void refreshElementView(View view, Lesson lesson){
        TextView nameView = view.findViewById(R.id.name_view);
        TextView hoursTextView = view.findViewById(R.id.hours_left_view);
        nameView.setText(lesson.getStudent().getName());
        hoursTextView.setText(String.valueOf(lesson.getHoursWorked()));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(elemLayout, parent, false);

        convertView.setTag(R.id.lesson,lessonsList.get(position));

        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView hoursTextView = convertView.findViewById(R.id.hours_left_view);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        nameView.setText(lessonsList.get(position).getStudent().getName());
        hoursTextView.setText(String.valueOf(lessonsList.get(position).getHoursWorked()));

        convertView.setOnLongClickListener(this);
        convertView.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);

        elementViews.add(convertView);
        return convertView;
    }

    @Override
    public boolean onLongClick(View v) {
        Lesson lesson = (Lesson) v.getTag(R.id.lesson);
        studentsRepository.getStudent(lesson.getStudent().getName()).observe(fragment,student -> {
            if(student!=null){
                StudentCard studentCard = new StudentCard(student);
                fragmentManager.beginTransaction().replace(R.id.contentmain, studentCard).addToBackStack(null).commit();
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onClick(View v) {
        CheckBox chBox = v.findViewById(R.id.checkBox);
        chBox.setChecked(!chBox.isChecked());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(mContext, "checked", Toast.LENGTH_SHORT).show();
    }
}
