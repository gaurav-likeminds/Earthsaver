package com.globalwarming.earthsaver.account

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.HomeActivity
import com.globalwarming.earthsaver.Image
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.databinding.ActivityProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityProfileBinding
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = ImageAdapter()
        binding.recyclerView.adapter = adapter

        db.collection("users").document(mAuth.uid!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val user = task.result!!.toObject(User::class.java)
                    if (user != null) {
                        user.id = task.result!!.id
                        updateUI(user)
                    }
                } else {
                    Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                }
            }

        db.collection("users")
            .document(mAuth.uid!!)
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

    private fun updateUI(user: User) {
        Glide.with(binding.profileImage)
            .load(user.image)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(binding.profileImage)
        binding.textName.text = user.name
        binding.textPoints.text = "${user.points} Points"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_account, menu)
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
        }
        return super.onOptionsItemSelected(item)
    }

}