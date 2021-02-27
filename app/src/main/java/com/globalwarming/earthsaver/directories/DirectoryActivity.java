package com.globalwarming.earthsaver.directories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.globalwarming.earthsaver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DirectoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DirectoryQuestionAdapter adapter;

    private FirebaseFirestore db;

    private List<DirectoryQuestion> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        db = FirebaseFirestore.getInstance();

        db.collection("directories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DirectoryQuestion directoryQuestion = document.toObject(DirectoryQuestion.class);
                                list.add(directoryQuestion);
                            }

                            adapter = new DirectoryQuestionAdapter(list);
                            recyclerView.setAdapter(adapter);

                        } else  {

                            Snackbar.make(recyclerView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                });

    }

}