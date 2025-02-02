package com.app.artclass.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.artclass.Logger;
import com.app.artclass.R;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.dialog.AddNewStudentDialog;
import com.app.artclass.fragments.dialog.ConfirmDeleteObjectDialog;
import com.app.artclass.fragments.dialog.DialogCreationHandler;
import com.app.artclass.recycler_adapters.StudentsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class AllStudentsListFragment extends Fragment {

    private List<Student> startStudentsList = null;

    public AllStudentsListFragment() {
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }

    public AllStudentsListFragment(List<Student> studentList){
        this.startStudentsList = studentList;
        Logger.getInstance().appendLog(getClass(),"init with list");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_all_students_list, container, false);


        StudentsRecyclerAdapter adapter = new StudentsRecyclerAdapter(
                this,
                R.layout.item_all_students_list,
                R.id.name_view,
                R.id.balance_view,
                false,
                new ArrayList<>());
        RecyclerView list_st_view = mainView.findViewById(R.id.all_students_list);
        list_st_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list_st_view.setAdapter(adapter);

        FloatingActionButton btn_floating_addnew = mainView.findViewById(R.id.fab_add_new_group);
        btn_floating_addnew.setTag(adapter);
        btn_floating_addnew.setOnClickListener(view -> {
            AddNewStudentDialog addNewStudentDialog = new AddNewStudentDialog((StudentsRecyclerAdapter)view.getTag());
            addNewStudentDialog.show(getFragmentManager(), "AddNewStudentDialog");
        });

        FloatingActionButton btn_floating_delete = mainView.findViewById(R.id.fab_secondary);
        btn_floating_delete.setOnClickListener(view -> {
            ConfirmDeleteObjectDialog deleteObjectDialog = new ConfirmDeleteObjectDialog("", () -> adapter.deleteCheckedStudents(), () -> adapter.clearChecked());
            deleteObjectDialog.show(getFragmentManager(), "ConfirmDeleteObjectDialog");
        });

        if(startStudentsList != null){
            adapter.setStudents(startStudentsList);
            adapter.notifyDataSetChanged();
        }else {
            StudentsRepository.getInstance().getAllStudents().observe(this, studentList -> {
                adapter.setStudents(studentList);
                adapter.notifyDataSetChanged();
            });
        }
        return mainView;
    }


}
