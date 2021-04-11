package com.globalwarming.earthsaver.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.databinding.LayoutUserBinding
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val users = ArrayList<User>()
    private var userClickListener: UserClickListener? = null

    fun setList(users: MutableList<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun setListener(clickListener: UserClickListener?) {
        this.userClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.textViewName.text = user.name
        holder.binding.textViewPoints.text = "Points : ${user.points}"
        holder.binding.textViewAddress.text = user.location
        holder.itemView.setOnClickListener {
            userClickListener?.onUserClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(val binding: LayoutUserBinding) : RecyclerView.ViewHolder(binding.root)
}

interface UserClickListener {
    fun onUserClick(user: User)
}