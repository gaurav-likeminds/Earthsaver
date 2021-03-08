package com.globalwarming.earthsaver.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Group group;
    private GroupUserAdapter adapter;
    private List<String> users;

    private static final int PICK_USER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter = new GroupUserAdapter();
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        group = (Group) getIntent().getSerializableExtra("group");
        toolbar.setTitle(group.getName());
        users = group.getUsers();
        adapter.setList(users);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(group.getCreated_by()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User adminUser = task.getResult().toObject(User.class);
                        toolbar.setSubtitle("Created by " + adminUser.getName());
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_user) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, PICK_USER);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_USER && resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra("user");
            if (users.contains(user.getId())) {
                Toast.makeText(this, "User is already added in this group", Toast.LENGTH_SHORT).show();
            } else {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                }
                progressDialog.setMessage("Adding " + user.getName());
                progressDialog.show();
                db.collection("groups").document(group.getId())
                        .update("users", FieldValue.arrayUnion(user.getId()))
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                users.add(user.getId());
                                adapter.setList(users);
                            } else {
                                Toast.makeText(GroupActivity.this, "Failed to add the user", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}