package com.globalwarming.earthsaver.group

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.*
import com.globalwarming.earthsaver.databinding.ActivityCreateGroupBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var loader: Loader
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        loader = Loader(this, "Creating group ...")
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.buttonCreateGroup.setOnClickListener {
            Util.hideKeyboard(it)
            val groupName = binding.editTextGroupName.text.toString().trim()
            if (groupName.isEmpty()) {
                Snackbar.make(it, "Group name cannot be empty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loader.show()
            val group = Group()
            group.name = groupName
            group.timestamp = System.currentTimeMillis()
            group.created_by = mAuth.uid!!
            val users = ArrayList<String>()
            users.add(mAuth.uid!!)
            group.users = users
            val id = db.collection("groups").document().id
            db.collection("groups").document(id).set(group)
                .addOnCompleteListener { task ->
                    loader.dismiss()
                    if (task.isSuccessful) {
                        group.id = id
                        Toast.makeText(
                            this@CreateGroupActivity,
                            "$groupName group created successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@CreateGroupActivity, GroupActivity::class.java)
                        intent.putExtra("group", group)
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(it, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroy() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onDestroy()
    }
}