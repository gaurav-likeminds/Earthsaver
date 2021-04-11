package com.globalwarming.earthsaver.group

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.*
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.databinding.ActivityGroupBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    private var loader: Loader? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var group: Group
    private lateinit var adapter: GroupUserAdapter
    private val users = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group)
        setSupportActionBar(binding.toolbar)

        binding.recyclerView.setHasFixedSize(true)
        adapter = GroupUserAdapter()
        binding.recyclerView.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        group = intent.getParcelableExtra("group")!!
        binding.toolbar.title = group.name
        users.addAll(group.users ?: emptyList())
        adapter.setList(users)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(group.created_by!!).get()
            .addOnCompleteListener { task ->
                val adminUser = task.result?.toObject(User::class.java)
                if (adminUser != null) {
                    binding.toolbar.subtitle = "Created by ${adminUser.name}"
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_group, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_user) {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, PICK_USER)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_USER && resultCode == RESULT_OK) {
            val user = data?.getParcelableExtra("user") as? User ?: return
            if (users.contains(user.id)) {
                Toast.makeText(this, "User is already added in this group", Toast.LENGTH_SHORT).show()
            } else {
                if (loader == null) {
                    loader = Loader(this, "Adding ${user.name}")
                } else {
                    loader?.setText("Adding ${user.name}")
                }
                loader?.show()
                db.collection("groups").document(group.id!!)
                    .update("users", FieldValue.arrayUnion(user.id))
                    .addOnCompleteListener { task ->
                        loader?.dismiss()
                        if (task.isSuccessful) {
                            users.add(user.id)
                            adapter.setList(users)
                        } else {
                            Toast.makeText(
                                this@GroupActivity,
                                "Failed to add the user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    override fun onDestroy() {
        if (loader != null && loader?.isShowing == true) {
            loader?.dismiss()
        }
        super.onDestroy()
    }

    companion object {
        private const val PICK_USER = 101
    }
}