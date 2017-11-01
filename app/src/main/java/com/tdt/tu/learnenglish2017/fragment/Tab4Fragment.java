package com.tdt.tu.learnenglish2017.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab4Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment4_layout, container, false);

        return view;
    }
}
