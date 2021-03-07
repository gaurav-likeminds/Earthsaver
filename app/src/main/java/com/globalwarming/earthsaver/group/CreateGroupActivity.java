package com.globalwarming.earthsaver.group;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.User;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText editText;
    private RecyclerView recyclerView;
    private Button buttonAddUser;
    private Button buttonCreateGroup;

    private List<User> userList = new ArrayList<>();
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editText = findViewById(R.id.edit_text_group_name);
        recyclerView = findViewById(R.id.recycler_view);
        buttonAddUser = findViewById(R.id.button_add_user);
        buttonCreateGroup = findViewById(R.id.button_create_group);

        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        buttonAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(CreateGroupActivity.this, SearchActivity.class);
            startActivityForResult(intent, 100);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra("user");
            if (user != null) {
                if (userList.contains(user)) {
                    Toast.makeText(this, "User is already added", Toast.LENGTH_SHORT).show();
                } else {
                    userList.add(user);
                    adapter.setList(userList);
                }
            }
        }
    }
}