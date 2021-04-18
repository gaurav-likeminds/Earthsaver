package com.globalwarming.earthsaver.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.HomeActivity
import com.globalwarming.earthsaver.Image
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.databinding.ActivityProfileBinding
import com.globalwarming.earthsaver.group.Group
import com.globalwarming.earthsaver.group.GroupActivity
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityProfileBinding
    private lateinit var adapter: ImageAdapter

    private var userId = ""
    private var isAdmin = false
    private var showMenu = true
    private var group: Group? = null

    private lateinit var loader: Loader

    companion object {

        const val USER_ID = "user_id"
        const val GROUP = "group"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = if (intent.hasExtra(USER_ID)) {
            group = intent.getParcelableExtra(GROUP)
            intent.getStringExtra(USER_ID)!!
        } else {
            mAuth.uid!!
        }

        if (group != null) {
            isAdmin = userId == mAuth.uid
        }

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = ImageAdapter()
        binding.recyclerView.adapter = adapter

        loader = Loader(this, "Fetching profile...")
        loader.show()

        db.collection("users").document(userId)
            .get()
            .addOnCompleteListener { task ->
                loader.dismiss()
                if (task.isSuccessful && task.result != null) {
                    val user = task.result!!.toObject(User::class.java)
                    if (user != null) {
                        user.id = task.result!!.id
                        updateUI(user, userId == mAuth.uid)
                    }
                } else {
                    Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                }
            }

        db.collection("users")
            .document(userId)
            .collection("images")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val list = mutableListOf<Image>()
                for (ds in value!!.documents) {
                    val image = ds.toObject(Image::class.java)!!
                    list.add(image)
                }
                adapter.setList(list)
            }

        binding.buttonUpload.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateUI(user: User, isMe : Boolean) {
        if (isMe) {
            binding.buttonUpload.visibility = View.VISIBLE
            showMenu = true
            invalidateOptionsMenu()
        } else {
            binding.toolbar.title = "Profile"
            binding.buttonUpload.visibility = View.GONE
            showMenu = false
            invalidateOptionsMenu()
        }
        Glide.with(binding.profileImage)
            .load(user.image)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(binding.profileImage)
        binding.textName.text = user.name
        binding.textEmail.text = user.email
        binding.textPoints.text = "${user.points} Points"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_account, menu)
        val menu1 = menu?.findItem(R.id.menu_edit_profile)
        val menu2 = menu?.findItem(R.id.menu_logout)
        val menu3 = menu?.findItem(R.id.menu_remove)
        if (showMenu) {
            menu1?.isVisible = true
            menu2?.isVisible = true
            menu3?.isVisible = false
        } else {
            menu1?.isVisible = false
            menu2?.isVisible = false
            menu3?.isVisible = isAdmin
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit_profile -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            R.id.menu_remove -> {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Remove user?")
                    .setMessage("Remove the current user?")
                    .setPositiveButton("No, Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Yes, Remove") { dialog, _ ->
                        dialog.dismiss()
                        if (group != null) {
                            val users = group!!.users?.toMutableList()
                            if (!users.isNullOrEmpty()) {
                                val index = users.indexOfFirst {
                                    it.contains(userId)
                                }
                                if (index > -1) {
                                    loader.setText("Removing this user ...")
                                    loader.show()
                                    users.removeAt(index)
                                    db.collection("groups")
                                        .document(group!!.id!!)
                                        .update("users", users)
                                        .addOnCompleteListener { task ->
                                            loader.dismiss()
                                            if (task.isSuccessful) {
                                                GroupActivity.REMOVED_USER = userId
                                                Toast.makeText(this, "User removed", Toast.LENGTH_SHORT).show()
                                                finish()
                                            } else {
                                                Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            }
                        }
                    }
                    .create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}