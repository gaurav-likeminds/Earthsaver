package com.globalwarming.earthsaver.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.databinding.LayoutUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GroupUserAdapter : RecyclerView.Adapter<GroupUserAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val users = ArrayList<String>()

    fun setList(users: List<String>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = users[position]
        db.document(userId).get().addOnCompleteListener { task ->
            val user = task.result?.toObject(User::class.java)
            if (user != null) {
                holder.binding.textViewName.text = user.name
                holder.binding.textViewPoints.text = "Points : ${user.points}"
                holder.binding.textViewAddress.text = user.location
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(val binding: LayoutUserBinding) : RecyclerView.ViewHolder(binding.root)

}