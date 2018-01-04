package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.FirstQuizActivity;
import com.tdt.tu.learnenglish2017.activity.LoginActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab5Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.email)
    TextView txtEmail;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.cvDoQuiz)
    CardView cvDoQuiz;
    @BindView(R.id.cvDownloadQuality)
    CardView cvDownloadQuality;
    @BindView(R.id.cvDownloadedContent)
    CardView cvDownloadedContent;
    @BindView(R.id.cvAbout)
    CardView cvAbout;
    @BindView(R.id.cvChangePassword)
    CardView cvChangePassword;

    private View view;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String name;
    private String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment5, container, false);
        init();
        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);

        firebaseAuth = FirebaseAuth.getInstance();

        name = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("username", "");
        email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
        txtEmail.setText(email);

        cvDoQuiz.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        cvChangePassword.setOnClickListener(this);
        cvDownloadQuality.setOnClickListener(this);
        cvDownloadedContent.setOnClickListener(this);
        cvAbout.setOnClickListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };
    }

    private void logout() {
        firebaseAuth.signOut();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                logout();
                break;

            case R.id.cvDoQuiz:
                Intent intent = new Intent(view.getContext(), FirstQuizActivity.class);
                startActivity(intent);
                break;

            case R.id.cvDownloadQuality:
                Fragment downloadedQualityFragment = new DownloadedQualityFragment();
                replaceFragment(downloadedQualityFragment);
                break;

            case R.id.cvDownloadedContent:
                Fragment downloadedContentFragment = new DownloadedContentFragment();
                replaceFragment(downloadedContentFragment);
                break;

            case R.id.cvAbout:
                Fragment aboutFragment = new AboutFragment();
                replaceFragment(aboutFragment);
                break;

            case R.id.cvChangePassword:
                Fragment changePasswordFragment = new ChangePasswordFragment();
                replaceFragment(changePasswordFragment);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment5_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
