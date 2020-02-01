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
public class MainPageFragment extends Fragment {


    public MainPageFragment() {
        Logger.getInstance().appendLog(getClass(),"init fragment");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

}
