package com.globalwarming.earthsaver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.globalwarming.earthsaver.directories.DirectoryActivity;
import com.globalwarming.earthsaver.group.CreateGroupActivity;
import com.globalwarming.earthsaver.group.GroupsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textViewMyPoints;
    private Button buttonViewGroups;
    private Button buttonCreateGroup;
    private TextView categoryPersonalHabit;
    private TextView categoryEnergy;
    private TextView categoryTransportation;
    private TextView categoryRecycle;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            HomeActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            HomeActivity.this.finish();
            return;
        }
        db = FirebaseFirestore.getInstance();

        textView = findViewById(R.id.text_view_welcome);
        textViewMyPoints = findViewById(R.id.text_view_my_points);
        buttonCreateGroup = findViewById(R.id.button_create_group);
        buttonViewGroups = findViewById(R.id.button_view_groups);
        categoryPersonalHabit = findViewById(R.id.categoryPersonalHabits);
        categoryEnergy = findViewById(R.id.categoryEnergy);
        categoryTransportation = findViewById(R.id.categoryTransportation);
        categoryRecycle = findViewById(R.id.categoryRecycle);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db.collection("users").document(mAuth.getUid()).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("HomeActivity", "", error);
            }
            User user = value.toObject(User.class);
            textViewMyPoints.setText("My Points : " + user.getPoints());
            textView.setText("Welcome, " + user.getName());
        });

        buttonViewGroups.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GroupsActivity.class);
            startActivity(intent);
        });

        buttonCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateGroupActivity.class);
            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId() == R.id.menu_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}