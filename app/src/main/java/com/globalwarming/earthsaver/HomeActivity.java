package com.globalwarming.earthsaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.globalwarming.earthsaver.directories.DirectoryActivity;
import com.globalwarming.earthsaver.group.CreateGroupActivity;
import com.globalwarming.earthsaver.group.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView textView;
    private Button buttonLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView categoryPersonalHabit;
    private TextView categoryEnergy;
    private TextView categoryTransportation;
    private TextView categoryRecycle;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            //This finish() function will basically destroy the current screen
            HomeActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView = findViewById(R.id.textView);
        buttonLogout = findViewById(R.id.buttonLogout);
        categoryPersonalHabit = findViewById(R.id.categoryPersonalHabits);
        categoryEnergy = findViewById(R.id.categoryEnergy);
        categoryTransportation = findViewById(R.id.categoryTransportation);
        categoryRecycle = findViewById(R.id.categoryRecycle);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            //This finish() function will basically destroy the current screen
            HomeActivity.this.finish();
            return;
        }

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            Long age = document.getLong("age");
                            String email = document.getString("email");
                            String location = document.getString("location");
                            String gender = document.getString("gender");

                            textView.setText("Name : " + name + "\nEmail : " + email + "\nGender : " + gender + "\nAge : " + age + "\nLocation : " + location);

                        }
                    }
                });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, CreateGroupActivity.class);
                startActivity(intent);

                //It will logout the user
//                FirebaseAuth.getInstance().signOut();
//
//                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                startActivity(intent);
//                //This finish() function will basically destroy the current screen
//                finish();

            }
        });

        categoryEnergy.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DirectoryActivity.class);
            intent.putExtra("category", Category.CATEGORY_ENERGY);
            startActivity(intent);
        });
        categoryPersonalHabit.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DirectoryActivity.class);
            intent.putExtra("category", Category.CATEGORY_PERSONAL_HABIT);
            startActivity(intent);
        });
        categoryTransportation.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DirectoryActivity.class);
            intent.putExtra("category", Category.CATEGORY_TRANSPORTATION);
            startActivity(intent);
        });
        categoryRecycle.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DirectoryActivity.class);
            intent.putExtra("category", Category.CATEGORY_Recycle);
            startActivity(intent);
        });

    }

}