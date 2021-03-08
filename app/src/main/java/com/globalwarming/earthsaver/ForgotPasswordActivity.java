package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout etEmail;
    private Button button;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.toolbar);
        etEmail = findViewById(R.id.etEmail);
        button = findViewById(R.id.buttonResetPassword);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting password");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(v -> {
            String email = etEmail.getEditText().getText().toString().trim();
            if (!email.isEmpty()) {
                Util.hideKeyboard(v);
                progressDialog.show();

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to registered email", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Snackbar.make(v, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}