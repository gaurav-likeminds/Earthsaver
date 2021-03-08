package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button buttonLogin;
    private TextView textRegister;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            //The user is already logged in
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        //If the value of the current user is null, it means the user is not logged in
        //and if it is not null, then the user is logged in.

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(v -> {
            Util.hideKeyboard(v);
            if (validate()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
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
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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