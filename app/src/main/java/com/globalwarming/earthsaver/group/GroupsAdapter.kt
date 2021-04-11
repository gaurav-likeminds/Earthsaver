package com.globalwarming.earthsaver.group

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.Util
import com.globalwarming.earthsaver.databinding.LayoutGroupBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private var db = FirebaseFirestore.getInstance().collection("users")
    private val groups = ArrayList<Group>()

    fun setGroups(groups: List<Group>) {
        this.groups.clear()
        this.groups.addAll(groups)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.binding.textViewGroupName.text = group.name
        holder.binding.textViewCreatedAt.text = "Created on ${Util.getTime(group.timestamp ?: 0)}"
        db.document(group.created_by!!).get()
            .addOnCompleteListener { task ->
                val user = task.result?.toObject(User::class.java)
                holder.binding.textViewCreatedBy.text = if (user != null) {
                    "Created by : ${user.name}"
                } else {
                    "Created by : User"
                }
            }
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, GroupActivity::class.java)
            intent.putExtra("group", group)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class ViewHolder(val binding: LayoutGroupBinding) : RecyclerView.ViewHolder(binding.root)

}