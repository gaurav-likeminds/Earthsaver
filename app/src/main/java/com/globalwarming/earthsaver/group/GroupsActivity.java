package com.globalwarming.earthsaver.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.globalwarming.earthsaver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private GroupsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        recyclerView.setHasFixedSize(true);
        adapter = new GroupsAdapter();
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching groups ...");
        progressDialog.show();

        db.collection("groups")
                .whereArrayContains("users", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        List<Group> groups = new ArrayList<>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            Group group = ds.toObject(Group.class);
                            groups.add(group);
                        }
                        adapter.setGroups(groups);
                    } else {
                        Toast.makeText(GroupsActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onBackPressed();
    }
}