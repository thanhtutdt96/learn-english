package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.FirstQuizActivity;
import com.tdt.tu.learnenglish2017.activity.LoginActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    @BindView(R.id.circleProgress)
    DonutProgress circleProgress;
    @BindView(R.id.txtProgress)
    TextView txtProgress;
    @BindView(R.id.txtRanking)
    TextView txtRanking;

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
        setContent();
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

    private void setContent() {
        HashMap<String, String> params = new HashMap();
        params.put("email", email);

        GetTotalProgress getTotalProgress = new GetTotalProgress(Constants.URL_GET_TOTAL_PROGRESS, params, Constants.CODE_POST_REQUEST);
        getTotalProgress.execute();

        GetScoreRank getScoreRank = new GetScoreRank(Constants.URL_GET_SCORE_RANK, params, Constants.CODE_POST_REQUEST);
        getScoreRank.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
    }

    public class GetTotalProgress extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        GetTotalProgress(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    int progress = object.getJSONArray("courses").getJSONObject(0).getInt("total_progress");
                    circleProgress.setProgress(progress);
                    txtProgress.setText(progress + "%");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    public class GetScoreRank extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        GetScoreRank(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    int scoreOrder = object.getJSONArray("results").getJSONObject(0).getInt("score_order");
                    int totalResult = object.getJSONArray("results").getJSONObject(0).getInt("total_result");
                    txtRanking.setText("#" + scoreOrder + " / " + totalResult);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}
