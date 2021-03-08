package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button buttonLogin;
    private TextView textRegister;
    private TextView textForgotPassword;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        textForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(v -> {
            Util.hideKeyboard(v);
            if (validate()) {
                String email = etEmail.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString().trim();
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //Login is complete
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Login is not complete
                        Snackbar.make(etEmail, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private Boolean validate() {
        String email = etEmail.getEditText().getText().toString().trim();
        String password = etPassword.getEditText().getText().toString().trim();

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(etEmail, "Email is invalid.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        //Password
        if (password.length() < 6) {
            Snackbar.make(etPassword, "Password has to e of 6 characters", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}