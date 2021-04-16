package com.globalwarming.earthsaver.group

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
            .whereArrayContains("users", "TRUE|${mAuth.uid!!}")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    loader.dismiss()
                    Toast.makeText(this@GroupsActivity, "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    return@addSnapshotListener
                }
                val groups = ArrayList<Group>()
                for (ds in value!!.documents) {
                    val group = ds.toObject(Group::class.java)!!
                    group.id = ds.id
                    group.isAccepted = true
                    groups.add(group)
                }

                db.collection("groups")
                    .whereArrayContains("users", "FALSE|${mAuth.uid!!}")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (ds in task.result!!) {
                                val group = ds.toObject(Group::class.java)
                                group.id = ds.id
                                group.isAccepted = false
                                groups.add(group)
                            }
                        }

                        loader.dismiss()
                        if (groups.isEmpty()) {
                            Snackbar.make(
                                binding.recyclerView,
                                "You are not in any group yet",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            adapter.setGroups(groups)
                        }

                    }
            }
    }

    override fun onBackPressed() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_groups, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_create_group) {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}