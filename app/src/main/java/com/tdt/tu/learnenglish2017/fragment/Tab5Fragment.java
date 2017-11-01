package com.tdt.tu.learnenglish2017.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab5Fragment extends Fragment {
    private static final String TAG="Tab5Fragment";

    private Button button5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment5_layout,container,false);


        return view;
    }
}
