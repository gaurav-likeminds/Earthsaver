package com.globalwarming.earthsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        //This function runs at the start of this screen
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
        progressBar = findViewById(R.id.progress_bar);
        textRegister = findViewById(R.id.textRegister);

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This block will run when the user will click login button

                if (validate()) {

                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        //Login is complete

                                        Snackbar.make(etEmail, "Wooooaaah, You are logged in", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        //Login is not complete
                                        Snackbar.make(etEmail, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });


                }

            }
        });

    }

    private Boolean validate() {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            Snackbar.make(etEmail, "Email is invalid.", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        //Password
        if (password.length() < 6) {
            isValid = false;
            Snackbar.make(etPassword, "Password has to e of 6 characters", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        return isValid;
    }

}