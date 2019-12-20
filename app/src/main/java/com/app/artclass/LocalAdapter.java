package com.app.artclass;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class LocalAdapter<S> extends ArrayAdapter<S> {

    public LocalAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public void refreshFields(){
        // needed for refreshing student balances
    }
}
