package com.globalwarming.earthsaver.directories

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.globalwarming.earthsaver.databinding.LayoutDirectoryQuestionBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DirectoryQuestionAdapter : RecyclerView.Adapter<DirectoryQuestionAdapter.ViewHolder>() {

    private val list = ArrayList<DirectoryQuestion>()
    private val entries = HashMap<String, String>()

    fun setList(list: List<DirectoryQuestion>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun getEntries(): HashMap<String, String> {
        return entries
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutDirectoryQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val directoryQuestion = list[position]
        holder.binding.textQuestion.text = directoryQuestion.question
        holder.binding.textCategory.text = directoryQuestion.category
        holder.binding.textPoints.text = "${directoryQuestion.points} point"
        holder.binding.checkBox.isEnabled = directoryQuestion.canRespond
        holder.binding.textAlreadyResponded.visibility = if (directoryQuestion.canRespond) {
            View.GONE
        } else {
            View.VISIBLE
        }
        if (entries.containsKey(directoryQuestion.id)) {
            holder.binding.checkBox.isChecked = true
            holder.binding.editText.visibility = View.VISIBLE
            holder.binding.editText.setText(entries[directoryQuestion.id])
        } else {
            holder.binding.checkBox.isChecked = false
            holder.binding.editText.visibility = View.GONE
        }
        holder.binding.checkBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                holder.binding.editText.visibility = View.VISIBLE
                entries[directoryQuestion.id] = ""
            } else {
                holder.binding.editText.visibility = View.GONE
                entries.remove(directoryQuestion.id)
            }
        }
        holder.binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                entries[directoryQuestion.id] = s.toString()
            }
        })
        holder.binding.root.setOnClickListener {
            if (!directoryQuestion.canRespond) {
                Snackbar.make(
                    it,
                    "You have already engaged with this activity in the last 24 hours. Please try again in a day.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: LayoutDirectoryQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)
}