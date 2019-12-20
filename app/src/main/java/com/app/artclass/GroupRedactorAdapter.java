package com.app.artclass;

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
import androidx.fragment.app.FragmentManager;

import com.app.artclass.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class GroupRedactorAdapter extends LocalAdapter<Lesson> implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

    private FragmentManager fragmentManager;
    private List<Lesson> lessonsList;
    private List<View> elementViews;
    private int elemLayout;
    private Context mContext;
    private DatabaseManager databaseManager;

    public GroupRedactorAdapter(@NonNull Context context, int resource, @NonNull List<Lesson> objects, FragmentManager fragmentManager) {
        super(context, resource, objects);
        databaseManager = DatabaseManager.getInstance();
        this.fragmentManager = fragmentManager;

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
        nameView.setText(lesson.getStudentName());
        hoursTextView.setText(String.valueOf(lesson.getHoursWorked()));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(elemLayout, parent, false);

        convertView.setTag(lessonsList.get(position));

        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView hoursTextView = convertView.findViewById(R.id.hours_left_view);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        nameView.setText(String.valueOf(lessonsList.get(position).getStudentName()));
        hoursTextView.setText(String.valueOf(lessonsList.get(position).getHoursWorked()));

        convertView.setOnLongClickListener(this);
        convertView.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);

        elementViews.add(convertView);
        return convertView;
    }

    @Override
    public boolean onLongClick(View v) {
        TextView nameView = v.findViewById(R.id.name_view);
        Student student = databaseManager.getStudent(nameView.getText().toString());
        StudentCard studentCard = new StudentCard(student, this);
        fragmentManager.beginTransaction().replace(R.id.contentmain, studentCard).addToBackStack(null).commit();
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
