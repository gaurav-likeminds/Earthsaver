package com.globalwarming.earthsaver.directories;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.globalwarming.earthsaver.R;

import java.util.HashMap;
import java.util.List;

public class DirectoryQuestionAdapter extends RecyclerView.Adapter<DirectoryQuestionAdapter.ViewHolder> {

    private List<DirectoryQuestion> list;
    private HashMap<String, Pair<Boolean, String>> entries = new HashMap<>();

    public DirectoryQuestionAdapter(List<DirectoryQuestion> list) {
        this.list = list;
    }

    public HashMap<String, Pair<Boolean, String>> getEntries() {
        return entries;
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

        if (entries.containsKey(directoryQuestion.getId())) {
            holder.checkBox.setChecked(true);
            holder.editText.setVisibility(View.VISIBLE);
            holder.editText.setText(entries.get(directoryQuestion.getId()).second);
        } else  {
            holder.checkBox.setChecked(false);
            holder.editText.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.editText.setVisibility(View.VISIBLE);
                entries.put(directoryQuestion.getId(), Pair.create(true, ""));
            } else {
                holder.editText.setVisibility(View.GONE);
                entries.remove(directoryQuestion.getId());
            }
        });

        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                entries.put(directoryQuestion.getId(), Pair.create(entries.get(directoryQuestion.getId()).first, s.toString()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestion;
        TextView textViewCategory;
        TextView textViewPoint;
        CheckBox checkBox;
        EditText editText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQuestion = itemView.findViewById(R.id.text_question);
            textViewCategory = itemView.findViewById(R.id.text_category);
            textViewPoint = itemView.findViewById(R.id.text_points);
            checkBox = itemView.findViewById(R.id.check_box);
            editText = itemView.findViewById(R.id.edit_text);

        }

    }

}
