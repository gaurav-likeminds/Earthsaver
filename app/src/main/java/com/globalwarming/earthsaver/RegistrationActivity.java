package com.globalwarming.earthsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmailAddress;
    EditText editTextAge;
    EditText editTextLocation;
    EditText editTextPassword;
    EditText editTextRePassword;
    RadioGroup genderGroup;

    Button buttonSubmit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Here we need to write the business logic
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

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This block will run when the user taps on the submit button
                String name = editTextName.getText().toString();
                String email = editTextEmailAddress.getText().toString();
                int age = 0;
                if (!editTextAge.getText().toString().isEmpty()) {
                    age = Integer.parseInt(editTextAge.getText().toString());
                }
                String location = editTextLocation.getText().toString();
                String password = editTextPassword.getText().toString();
                String Repassword = editTextRePassword.getText().toString();

                int genderButtonId = genderGroup.getCheckedRadioButtonId();
                String gender = "";
                if (genderButtonId == R.id.radioButtonMale) {
                    gender = "MALE";
                } else if (genderButtonId == R.id.radioButtonFemale) {
                    gender = "FEMALE";
                } else {
                    gender = "OTHER";
                }

                if (validate()) {

                    int finalAge = age;
                    String finalGender = gender;
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        String uid = task.getResult().getUser().getUid();

                                        Map<String, Object> dataToUpload = new HashMap<>();
                                        dataToUpload.put("name", name);
                                        dataToUpload.put("email", email);
                                        dataToUpload.put("age", finalAge);
                                        dataToUpload.put("gender", finalGender);
                                        dataToUpload.put("location", location);

                                        db.collection("users")
                                                .document(uid)
                                                .set(dataToUpload)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                                            startActivity(intent);
                                                            RegistrationActivity.this.finish();
                                                        } else {
                                                            Snackbar.make(v, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                    } else {

                                        Snackbar.make(v, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                                    }
                                }
                            });

                }

            }
        });


    }

    private Boolean validate() {
        boolean isValid = true;

        String name = editTextName.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        int age = 0;
        if (!editTextAge.getText().toString().isEmpty()) {
            age = Integer.parseInt(editTextAge.getText().toString());
        }
        String location = editTextLocation.getText().toString();
        String password = editTextPassword.getText().toString();
        String Repassword = editTextRePassword.getText().toString();

        //Name
        if (name.isEmpty() || name.length() < 5) {
            isValid = false;
            Snackbar.make(editTextName, "Name is invalid. Must be a 5 character name", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            Snackbar.make(editTextEmailAddress, "Email is invalid.", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        //Age
        if (age < 10) {
            isValid = false;
            Snackbar.make(editTextAge, "Minimum age is 10 years", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        //Password
        if (password.length() < 6) {
            isValid = false;
            Snackbar.make(editTextPassword, "Password has to e of 6 characters", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        //New Password
        if (!password.equals(Repassword)) {
            isValid = false;
            Snackbar.make(editTextPassword, "Password do not match", Snackbar.LENGTH_SHORT).show();
            return isValid;
        }

        return isValid;
    }

}

