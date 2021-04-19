package com.globalwarming.earthsaver.group

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.account.ProfileActivity
import com.globalwarming.earthsaver.databinding.LayoutUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GroupUserAdapter(
    private val group: Group
) : RecyclerView.Adapter<GroupUserAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val users = ArrayList<String>()

    fun setList(users: List<String>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun removeUser(userId: String) {
        val index = users.indexOfFirst {
            it.contains(userId)
        }
        if (index > -1) {
            users.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = users[position].split("|")[1]
        db.document(userId).get().addOnCompleteListener { task ->
            val user = task.result?.toObject(User::class.java)
            if (user != null) {
                holder.binding.textViewName.text = user.name
                holder.binding.textViewPoints.text = "Points : ${user.points}"
                holder.binding.textViewAddress.text = user.location
                Glide.with(holder.binding.imageView).load(user.image)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.binding.imageView)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.USER_ID, userId)
            intent.putExtra(ProfileActivity.GROUP, group)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(val binding: LayoutUserBinding) : RecyclerView.ViewHolder(binding.root)

}