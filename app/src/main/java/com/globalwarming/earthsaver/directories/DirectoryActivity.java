package com.globalwarming.earthsaver.directories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryActivity extends AppCompatActivity {

    private ConstraintLayout parent;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private DirectoryQuestionAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<DirectoryQuestion> list = new ArrayList<>();
    private List<DirectoryQuestion> oldRespondedQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        String category = getIntent().getStringExtra("category");

        parent = findViewById(R.id.parent);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        toolbar.setTitle(category);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        recyclerView.setHasFixedSize(true);
        adapter = new DirectoryQuestionAdapter();
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching questions ...");
        progressDialog.show();

        //fetchAllPrevious();
        db.collection("directories")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DirectoryQuestion directoryQuestion = document.toObject(DirectoryQuestion.class);
                            directoryQuestion.setId(document.getId());
                            list.add(directoryQuestion);
                        }
                        adapter.setList(list);
                    }
                });

    }

    private void fetchAllPrevious() {
        Long minTimestamp = System.currentTimeMillis() - (60 * 60 * 24 * 1000 * 30);
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("entries")
                .whereGreaterThan("timestamp", minTimestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DirectoryQuestion directoryQuestion = document.toObject(DirectoryQuestion.class);
                            oldRespondedQuestions.add(directoryQuestion);
                        }
                    }


                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_directory, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            Util.hideKeyboard(parent);
            progressDialog.setMessage("Submitting answers ...");
            progressDialog.show();

            int points = 0;
            HashMap<String, Pair<Boolean, String>> entries = adapter.getEntries();
            String userId = mAuth.getUid();
            WriteBatch batch = db.batch();
            for (Map.Entry<String, Pair<Boolean, String>> entry : entries.entrySet()) {
                if (entry.getValue().first) {
                    points++;
                }
                DirectoryEntry directoryEntry = new DirectoryEntry();
                directoryEntry.setId(entry.getKey());
                directoryEntry.setTimestamp(System.currentTimeMillis());
                directoryEntry.setAnswer(entry.getValue().first);
                if (entry.getValue().first) {
                    directoryEntry.setPoint(1L);
                    directoryEntry.setNote(entry.getValue().second);
                } else {
                    directoryEntry.setPoint(0L);
                }
                batch.set(db.collection("users")
                        .document(userId)
                        .collection("entries")
                        .document(entry.getKey() + "#" + directoryEntry.getTimestamp()), directoryEntry);
            }
            batch.update(db.collection("users").document(mAuth.getUid()), "points", FieldValue.increment(points));

            batch.commit().addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(DirectoryActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Snackbar.make(parent, "Some error occurred", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!adapter.getEntries().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to go back? Your answers won't be submitted if you go back!")
                    .setNegativeButton("Yes", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        super.onBackPressed();
                    })
                    .setPositiveButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}