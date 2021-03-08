package com.globalwarming.earthsaver.group;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.User;
import com.globalwarming.earthsaver.Util;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private CollectionReference db;
    private List<Group> groups = new ArrayList<>();

    public GroupsAdapter() {
        db = FirebaseFirestore.getInstance().collection("users");
    }

    public void setGroups(List<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);

        holder.groupName.setText(group.getName());
        holder.groupCreatedAt.setText("Created on " + Util.getTime(group.getTimestamp()));

        db.document(group.getCreated_by()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                holder.groupCreatedBy.setText("Created by : " + user.getName());
            } else {
                holder.groupCreatedBy.setText("Created by : User");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GroupsActivity.class);
            intent.putExtra("group", group);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView groupName;
        private TextView groupCreatedBy;
        private TextView groupCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_view_group_name);
            groupCreatedBy = itemView.findViewById(R.id.text_view_created_by);
            groupCreatedAt = itemView.findViewById(R.id.text_view_created_at);
        }
    }

}
