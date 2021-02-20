package com.globalwarming.earthsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmailAddress; //TODO declare email
    EditText editTextAge;//TODO declare age
    EditText editTextLocation;//TODO declare location
    EditText editTextPassword;//TODO declare password
    EditText editTextRePassword; //TODO declare password again

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
        //TODO assign the id to all the elements

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This block will run when the user taps on the submit button
                String name = editTextName.getText().toString();
                //TODO similarly get the other data here and store it in variables
                String email = editTextEmailAddress.getText().toString();
                //TODO upload the data to cloud
                double age = editTextAge.getText().toString();
                String location = editTextLocation.getText().toString();
                String password = editTextPassword.getText().toString();
                String Repassword = editTextRePassword.getText().toString();
                Snackbar.make(v, name, Snackbar.LENGTH_LONG).show();
            }
        });


    }

}

