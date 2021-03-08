package com.globalwarming.earthsaver.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.globalwarming.earthsaver.R;
import com.globalwarming.earthsaver.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.ViewHolder> {

    private CollectionReference db;
    private List<String> users = new ArrayList<>();

    public GroupUserAdapter() {
        db = FirebaseFirestore.getInstance().collection("users");
    }

    public void setList(List<String> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = users.get(position);
        db.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                String points = "Points : 0";
                if (user.getPoints() != null) {
                    points = "Points : " + user.getPoints();
                }
                holder.textViewName.setText(user.getName());
                holder.textViewPoints.setText(points);
                holder.textViewAddress.setText(user.getLocation());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPoints;
        TextView textViewAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPoints = itemView.findViewById(R.id.text_view_points);
            textViewAddress = itemView.findViewById(R.id.text_view_address);
        }

    }

}

