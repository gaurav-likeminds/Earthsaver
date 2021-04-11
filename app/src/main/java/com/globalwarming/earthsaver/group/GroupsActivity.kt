package com.globalwarming.earthsaver.group

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.databinding.ActivityGroupsBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupsBinding
    private lateinit var loader: Loader
    private lateinit var adapter: GroupsAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_groups)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.recyclerView.setHasFixedSize(true)
        adapter = GroupsAdapter()
        binding.recyclerView.adapter = adapter
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loader = Loader(this, "Fetching groups ...")
        loader.show()

        db.collection("groups")
            .whereArrayContains("users", mAuth.uid!!)
            .get()
            .addOnCompleteListener { task ->
                loader.dismiss()
                if (task.isSuccessful) {
                    val groups = ArrayList<Group>()
                    for (ds in task.result!!) {
                        val group = ds.toObject(Group::class.java)
                        group.id = ds.id
                        groups.add(group)
                    }
                    if (groups.isEmpty()) {
                        Snackbar.make(
                            binding.recyclerView,
                            "You are not in any group yet",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.setGroups(groups)
                    }
                } else {
                    Toast.makeText(this@GroupsActivity, "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
    }

    override fun onBackPressed() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onBackPressed()
    }
}