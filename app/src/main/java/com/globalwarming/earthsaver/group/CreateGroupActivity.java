package com.globalwarming.earthsaver.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText;
    private Button buttonCreateGroup;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.edit_text_group_name);
        buttonCreateGroup = findViewById(R.id.button_create_group);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Creating group ...");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonCreateGroup.setOnClickListener(v -> {
            Util.hideKeyboard(v);
            String groupName = editText.getText().toString().trim();
            if (groupName.isEmpty()) {
                Snackbar.make(v, "Group name cannot be empty", Snackbar.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            Group group = new Group();
            group.setName(groupName);
            group.setTimestamp(System.currentTimeMillis());
            group.setCreated_by(mAuth.getUid());
            List<String> users = new ArrayList<>();
            users.add(mAuth.getUid());
            group.setUsers(users);

            String id = db.collection("groups").document().getId();

            db.collection("groups").document(id).set(group).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    group.setId(id);
                    Toast.makeText(CreateGroupActivity.this, groupName + " group created successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateGroupActivity.this, GroupActivity.class);
                    intent.putExtra("group", group);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(v, "Some error occurred", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    protected void onDestroy() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}