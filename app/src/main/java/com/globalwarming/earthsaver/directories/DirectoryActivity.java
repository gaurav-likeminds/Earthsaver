package com.globalwarming.earthsaver.directories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.globalwarming.earthsaver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button buttonSubmit;
    private RecyclerView recyclerView;
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

        progressBar = findViewById(R.id.progress_bar);
        buttonSubmit = findViewById(R.id.button_submit);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchAllPrevious();

        buttonSubmit.setOnClickListener(v -> {
            HashMap<String, Pair<Boolean, String>> entries = adapter.getEntries();

            String userId = mAuth.getCurrentUser().getUid();

            progressBar.setVisibility(View.VISIBLE);

            WriteBatch batch = db.batch();

            for (Map.Entry<String, Pair<Boolean, String>> entry : entries.entrySet()) {

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

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {

                        Snackbar.make(v, "Successfully uploaded", Snackbar.LENGTH_SHORT).show();

                    } else {
                        Snackbar.make(v, "Some error occurred", Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

        });

    }

    private void fetchAllPrevious() {
        Long minTimestamp = System.currentTimeMillis() - (60*60*24*1000*30);
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

}