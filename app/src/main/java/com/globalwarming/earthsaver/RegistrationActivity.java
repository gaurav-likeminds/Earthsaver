package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmailAddress;
    private EditText editTextAge;
    private EditText editTextLocation;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private RadioGroup genderGroup;
    private Button buttonSubmit;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName);
        buttonSubmit = findViewById(R.id.button);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        buttonSubmit = findViewById(R.id.button);
        editTextAge = findViewById(R.id.editTextAge);
        buttonSubmit = findViewById(R.id.button);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSubmit = findViewById(R.id.button);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSubmit = findViewById(R.id.button);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        buttonSubmit = findViewById(R.id.button);
        genderGroup = findViewById(R.id.genderGroup);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Registering ...");

        buttonSubmit.setOnClickListener(v -> {
            Util.hideKeyboard(v);
            if (validate()) {
                String name = editTextName.getText().toString();
                String email = editTextEmailAddress.getText().toString();
                int age = 0;
                if (!editTextAge.getText().toString().isEmpty()) {
                    age = Integer.parseInt(editTextAge.getText().toString());
                }
                String location = editTextLocation.getText().toString();
                String password = editTextPassword.getText().toString();
                int genderButtonId = genderGroup.getCheckedRadioButtonId();
                String gender;
                if (genderButtonId == R.id.radioButtonMale) {
                    gender = "MALE";
                } else if (genderButtonId == R.id.radioButtonFemale) {
                    gender = "FEMALE";
                } else {
                    gender = "OTHER";
                }
                progressDialog.show();

                int finalAge = age;
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();

                        Map<String, Object> dataToUpload = new HashMap<>();
                        dataToUpload.put("name", name);
                        dataToUpload.put("email", email);
                        dataToUpload.put("age", finalAge);
                        dataToUpload.put("gender", gender);
                        dataToUpload.put("location", location);

                        db.collection("users")
                                .document(uid)
                                .set(dataToUpload)
                                .addOnCompleteListener(task1 -> {
                                    progressDialog.dismiss();
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        RegistrationActivity.this.finish();
                                    } else {
                                        Snackbar.make(v, task1.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(v, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

            }

        });


    }

    private Boolean validate() {
        String name = editTextName.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        int age = 0;
        if (!editTextAge.getText().toString().isEmpty()) {
            age = Integer.parseInt(editTextAge.getText().toString());
        }
        String password = editTextPassword.getText().toString();
        String Repassword = editTextRePassword.getText().toString();

        //Name
        if (name.isEmpty() || name.length() < 5) {
            Snackbar.make(editTextName, "Name is invalid. Must be a 5 character name", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(editTextEmailAddress, "Email is invalid.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //Age
        if (age < 10) {
            Snackbar.make(editTextAge, "Minimum age is 10 years", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //Password
        if (password.length() < 6) {
            Snackbar.make(editTextPassword, "Password has to e of 6 characters", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //New Password
        if (!password.equals(Repassword)) {
            Snackbar.make(editTextPassword, "Password do not match", Snackbar.LENGTH_SHORT).show();
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

