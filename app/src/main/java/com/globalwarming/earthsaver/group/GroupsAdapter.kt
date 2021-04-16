package com.globalwarming.earthsaver.group

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.Util
import com.globalwarming.earthsaver.databinding.LayoutGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
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
        holder.binding.textViewAcceptance.visibility = if (group.isAccepted) {
            View.GONE
        } else {
            View.VISIBLE
        }
        db.collection("users").document(group.created_by!!).get()
            .addOnCompleteListener { task ->
                val user = task.result?.toObject(User::class.java)
                holder.binding.textViewCreatedBy.text = if (user != null) {
                    "Created by : ${user.name}"
                } else {
                    "Created by : User"
                }
            }
        holder.itemView.setOnClickListener {
            if (!group.isAccepted) {
                val dialog = AlertDialog.Builder(it.context)
                    .setTitle(group.name)
                    .setMessage("Accept the joining request for this group ?")
                    .setPositiveButton("Yes, Join") { dialog, _ ->
                        val users = group.users?.toMutableList()
                        val index = users?.indexOfFirst {
                            it.contains(mAuth.uid!!)
                        } ?: -1
                        if (index > -1) {
                            users?.set(index, "TRUE|${mAuth.uid!!}")
                        }
                        db.collection("groups")
                            .document(group.id!!)
                            .update("users", users)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    group.users = users
                                    group.isAccepted = true
                                    groups[position] = group
                                    notifyItemChanged(position)
                                } else {
                                    Toast.makeText(it.context, "Some error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                        dialog.dismiss()
                    }
                    .setNegativeButton("No, Cancel") { dialog, _ ->
                        val users = group.users?.toMutableList()
                        val index = users?.indexOfFirst {
                            it.contains(mAuth.uid!!)
                        } ?: -1
                        if (index > -1) {
                            users?.removeAt(index)
                        }
                        db.collection("groups")
                            .document(group.id!!)
                            .update("users", users)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    groups.removeAt(position)
                                    notifyItemChanged(position)
                                } else {
                                    Toast.makeText(it.context, "Some error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
                return@setOnClickListener
            }
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