package com.app.artclass.fragments;


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

import com.app.artclass.DialogHandler;
import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.Student;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class AllStudentsListFragment extends Fragment {


    private FragmentManager fragmentManager;


    public AllStudentsListFragment() {
        this.fragmentManager = this.getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_all_students_list, container, false);


        StudentsRecyclerAdapter adapter = new StudentsRecyclerAdapter(this, R.layout.item_all_students_list, R.id.name_view, R.id.balance_view, new ArrayList<Student>());
        RecyclerView list_st_view = mainView.findViewById(R.id.all_students_list);
        list_st_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list_st_view.setAdapter(adapter);

        FloatingActionButton btn_floating_addnew = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnew.setTag(adapter);
        btn_floating_addnew.setOnClickListener(view -> DialogHandler.getInstance().AddNewStudent(this.getContext(),(StudentsRecyclerAdapter)view.getTag()));

        FloatingActionButton btn_floating_delete = mainView.findViewById(R.id.fab_help);
        btn_floating_delete.setTag(adapter);
        btn_floating_delete.setOnClickListener(view -> {
            StudentsRecyclerAdapter curAdapter = (StudentsRecyclerAdapter)view.getTag();
            curAdapter.deleteCheckedStudents();
        });

        StudentsRepository.getInstance().getAllStudents().observe(this,studentList -> {
            adapter.setStudents(studentList);
            adapter.notifyDataSetChanged();
        });
        return mainView;
    }


}
