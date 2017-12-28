package com.tdt.tu.learnenglish2017.activity;

/**
 * Created by 1stks on 28-Dec-17.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tdt.tu.learnenglish2017.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.btnResetPassword)
    Button btnReset;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btnBack.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;

            case R.id.btnResetPassword:
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toasty.warning(getApplication(), "Enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(ResetPasswordActivity.this, "We have sent you instructions to reset your password", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(ResetPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
                break;
        }
    }
}

