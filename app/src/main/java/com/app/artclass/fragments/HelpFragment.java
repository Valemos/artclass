package com.app.artclass.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.artclass.Logger;
import com.app.artclass.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {


    public HelpFragment() {
        Logger.getInstance().appendLog(getClass(),"init");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

}
