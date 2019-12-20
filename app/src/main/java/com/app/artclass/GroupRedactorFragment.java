package com.app.artclass;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.artclass.database.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupRedactorFragment extends Fragment {

    private int dateValue;
    private String timeValue;
    private List<Lesson> lessonList;
    private FragmentManager fragmentManager;
    private DatabaseManager databaseManager;

    public GroupRedactorFragment() {
        // Required empty public constructor
    }

    public GroupRedactorFragment(int date, String time, FragmentManager fragmentManager) {
        this.dateValue = date;
        this.timeValue = time;
        this.databaseManager = DatabaseManager.getInstance();
        this.lessonList = databaseManager.getLessonStudentsList(date, time);
        this.fragmentManager = fragmentManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_redactor, container, false);

        // set date and time
        TextView dateText = view.findViewById(R.id.date_redactor_view);
        TextView timeText = view.findViewById(R.id.time_redactor_view);

        dateText.setText(
                String.valueOf(Converters.extrDay(dateValue))+getString(R.string.date_separator) +
                        String.valueOf(Converters.extrMonth(dateValue))+getString(R.string.date_separator) +
                        String.valueOf(Converters.extrYear(dateValue)));
        timeText.setText(timeValue);

        StudentsRecyclerAdapter adapter = new StudentsRecyclerAdapter(R.layout.item_group_redactor, R.id.name_view, R.id.hours_left_view, databaseManager.getStudentsForLesson(dateValue,timeValue), fragmentManager);
        RecyclerView list = view.findViewById(R.id.group_redactor_list);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list.setAdapter(adapter);

        FloatingActionButton btn_add_dialog = view.findViewById(R.id.fab_add_students);
        btn_add_dialog.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DialogHandler dialogHandler = new DialogHandler(getContext());
                dialogHandler.CreateNewGroup((RecyclerView.Adapter) v.getTag(), dateValue, timeValue, ((StudentsRecyclerAdapter) v.getTag()).getItems(), null);
            }
        });
        btn_add_dialog.setTag(adapter);

        FloatingActionButton btn_delete_selected = view.findViewById(R.id.fab_delete_selected);
        btn_delete_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentsRecyclerAdapter adapter = (StudentsRecyclerAdapter) v.getTag();
                adapter.deleteCheckedFromLesson(dateValue, timeValue);
            }
        });
        btn_delete_selected.setTag(adapter);

        return view;
    }
}
