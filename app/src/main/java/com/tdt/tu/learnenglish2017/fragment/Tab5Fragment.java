package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.FirstQuizActivity;
import com.tdt.tu.learnenglish2017.activity.LoginActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.SQLiteHandler;
import com.tdt.tu.learnenglish2017.helper.SessionManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab5Fragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.name)
    TextView txtName;
    @BindView(R.id.email)
    TextView txtEmail;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.cvDoQuiz)
    CardView cvDoQuiz;

    private SQLiteHandler db;
    private SessionManager session;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment5_layout, container, false);
        ButterKnife.bind(this, view);

        cvDoQuiz.setOnClickListener(this);
        // SqLite database handler
        db = new SQLiteHandler(view.getContext());

        // session manager
        session = new SessionManager(view.getContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        SharedPreferences prefs = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", name);
        editor.putString("email", email);
        editor.commit();

        txtName.setText(name);
        txtEmail.setText(email);

        return view;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                logoutUser();
            case R.id.cvDoQuiz:
                Intent intent = new Intent(view.getContext(), FirstQuizActivity.class);
                startActivity(intent);
        }
    }
}
