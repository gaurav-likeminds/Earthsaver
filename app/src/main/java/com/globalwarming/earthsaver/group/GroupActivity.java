package com.globalwarming.earthsaver.group;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.globalwarming.earthsaver.R;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Group group = (Group) getIntent().getSerializableExtra("group");



    }

}