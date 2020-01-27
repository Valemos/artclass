package com.app.artclass.list_adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

public class SearchAdapter extends CursorAdapter {

    String[] fields = new String[]{};

    public SearchAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        cursor.getColumnNames();
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
