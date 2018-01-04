package com.tdt.tu.learnenglish2017.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 1stks on 15-Nov-17.
 */

public class AboutCourseFragment extends Fragment {
    View view;
    String content;
    @BindView(R.id.txtContent)
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_course, container, false);
        init();
        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);
        SharedPreferences preferences = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        textView.setText(preferences.getString("description", ""));
    }
}
