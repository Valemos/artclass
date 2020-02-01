package com.app.artclass.list_adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentManager;

import com.app.artclass.R;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.StudentCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends CursorAdapter implements View.OnClickListener {

    private final FragmentManager fragmentManager;
    private final SearchView parentSearchView;
    private final int itemResource;
    private final LayoutInflater cursorInflater;
    private final MatrixCursor suggestionsCursor;
    private final String nameField;
    private final int name_field_id;
    private final MenuItem parentSearchMenuItem;
    private Map<String, Student> mStudentMap;

    public SearchAdapter(Context context, FragmentManager fragmentManager, MenuItem parentSearchMenuItem, int itemResource, MatrixCursor suggestionsCursor, String nameField, int name_field_id, List<Student> studentList) {
        super(context, suggestionsCursor, false);
        this.fragmentManager = fragmentManager;
        this.parentSearchView = (SearchView) parentSearchMenuItem.getActionView();
        this.parentSearchMenuItem = parentSearchMenuItem;
        this.itemResource = itemResource;
        this.suggestionsCursor = suggestionsCursor;
        this.nameField = nameField;
        this.name_field_id = name_field_id;

        // map for faster interaction
        this.mStudentMap = new HashMap<>();
        studentList.forEach(student -> mStudentMap.put(student.getName(), student));

        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(itemResource, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = view.findViewById(name_field_id);
        String curName = cursor.getString(cursor.getColumnIndex(nameField));
        nameView.setText(curName);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView nameView = v.findViewById(name_field_id);
        Student student = mStudentMap.get(nameView.getText().toString());
        if(student!=null){
            if(fragmentManager.findFragmentByTag("StudentCardSearch")==null){
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content_id,
                                new StudentCard(student),"StudentCardSearch").addToBackStack(null).commit();
                parentSearchView.post(() -> {
                    parentSearchView.setQuery("", false);
                    parentSearchView.clearFocus();
                    parentSearchMenuItem.collapseActionView();
                    fragmentManager.findFragmentByTag("StudentCardSearch").getView().requestFocus();
                });
            }
        }
    }
}
