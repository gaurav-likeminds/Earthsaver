package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmailAddress;
    EditText editTextAge;
    EditText editTextLocation;
    EditText editTextPassword;
    EditText editTextRePassword;
    RadioGroup genderGroup;

    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    //This block will only run when all the data are validated
                    Snackbar.make(v, "All data are correct", Snackbar.LENGTH_SHORT).show();
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

