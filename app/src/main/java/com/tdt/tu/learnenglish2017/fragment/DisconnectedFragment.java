package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tdt.tu.learnenglish2017.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 27-Dec-17.
 */

public class DisconnectedFragment extends Fragment {

    View view;

    @BindView(R.id.btnRetry)
    Button btnRetry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_disconnected, container, false);

        init();

        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("retry_connect");
                view.getContext().sendBroadcast(intent);
            }
        });
    }
}
