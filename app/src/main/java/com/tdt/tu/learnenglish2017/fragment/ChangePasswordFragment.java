package com.tdt.tu.learnenglish2017.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.oldPassword)
    EditText oldPassword;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseUser firebaseUser;
    private View view;
    private String currentPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        init();
        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                getFragmentManager().popBackStack();
                break;

            case R.id.btnConfirm:
                currentPassword = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("password", "");

                if (TextUtils.isEmpty(oldPassword.getText().toString())) {
                    oldPassword.setError("Enter old password");
                    oldPassword.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(newPassword.getText().toString())) {
                    newPassword.setError("Enter new password");
                    newPassword.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Enter confirm password");
                    confirmPassword.requestFocus();
                    return;
                } else if (!confirmPassword.getText().toString().equals(newPassword.getText().toString())) {
                    confirmPassword.setError("Confirm password doesn't match");
                    confirmPassword.requestFocus();
                    return;
                } else if (newPassword.getText().toString().trim().length() < 6) {
                    newPassword.setError("Password too short, enter minimum 6 characters");
                    newPassword.requestFocus();
                    return;
                } else if (!oldPassword.getText().toString().equals(currentPassword)) {
                    oldPassword.setError("Old password doesn't match");
                    oldPassword.requestFocus();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseUser.updatePassword(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.success(view.getContext(), "New password is updated", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
                                editor.putString("password", newPassword.getText().toString().trim());
                                editor.apply();
                            } else {
                                Toasty.success(view.getContext(), "Failed to update new password", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                break;
        }
    }
}
