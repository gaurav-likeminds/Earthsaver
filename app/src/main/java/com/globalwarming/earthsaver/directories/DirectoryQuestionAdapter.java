package com.globalwarming.earthsaver.directories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.globalwarming.earthsaver.R;
import java.util.List;

public class DirectoryQuestionAdapter extends RecyclerView.Adapter<DirectoryQuestionAdapter.ViewHolder> {

    private List<DirectoryQuestion> list;

    public DirectoryQuestionAdapter(List<DirectoryQuestion> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_directory_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DirectoryQuestion directoryQuestion = list.get(position);

        holder.textViewQuestion.setText(directoryQuestion.getQuestion());
        holder.textViewCategory.setText(directoryQuestion.getCategory());
        holder.textViewPoint.setText(directoryQuestion.getPoints() + " point");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestion;
        TextView textViewCategory;
        TextView textViewPoint;
        TextView checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQuestion = itemView.findViewById(R.id.text_question);
            textViewCategory = itemView.findViewById(R.id.text_category);
            textViewPoint = itemView.findViewById(R.id.text_points);
            checkBox = itemView.findViewById(R.id.check_box);

        }

    }

}
