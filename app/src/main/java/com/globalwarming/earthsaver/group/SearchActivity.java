package com.globalwarming.earthsaver.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private FirebaseFirestore db;
    private List<User> users = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = findViewById(R.id.button_search);
        editText = findViewById(R.id.edit_text);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new SearchAdapter();

        adapter.setListener(user -> {
            Intent intent = new Intent();
            intent.putExtra("user", user);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        button.setOnClickListener(v -> {

            String text = editText.getText().toString().trim();

            db.collection("users")
                    .whereEqualTo("email", text)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            users.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                user.setId(document.getId());
                                users.add(user);
                            }

                            adapter.setList(users);

                        } else {

                        }
                    });

        });

    }

}