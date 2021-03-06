package com.globalwarming.earthsaver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.account.ProfileActivity
import com.globalwarming.earthsaver.databinding.ActivityHomeBinding
import com.globalwarming.earthsaver.directories.DirectoryActivity
import com.globalwarming.earthsaver.group.CreateGroupActivity
import com.globalwarming.earthsaver.group.GroupsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityHomeBinding

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        db = FirebaseFirestore.getInstance()

        setSupportActionBar(binding.toolbar)

        db.collection("users").document(mAuth.uid!!)
            .addSnapshotListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("HomeActivity", "", error)
                }
                val user = value?.toObject(User::class.java) ?: return@addSnapshotListener
                binding.textViewMyPoints.text = "My Points : ${user.points}"
                binding.textViewWelcome.text = "Welcome, ${user.name}"
                val points = user.points.toInt()
                if (points in 1..100) {
                    binding.progressRed.progress = points
                }
                if (points in 101..300) {
                    binding.progressRed.progress = 100
                    val percentage = (points - 100) / 2
                    binding.progressYellow.progress = percentage
                }
                if (points >= 301) {
                    binding.progressRed.progress = 100
                    binding.progressYellow.progress = 100
                    val percentage = (points - 300) / 2
                    binding.progressGreen.progress = percentage
                }
            }

        binding.buttonViewProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.buttonViewGroups.setOnClickListener {
            val intent = Intent(this@HomeActivity, GroupsActivity::class.java)
            startActivity(intent)
        }
        binding.buttonCreateGroup.setOnClickListener {
            val intent = Intent(this@HomeActivity, CreateGroupActivity::class.java)
            startActivity(intent)
        }
        binding.categoryEnergy.setOnClickListener {
            val intent = Intent(this@HomeActivity, DirectoryActivity::class.java)
            intent.putExtra("category", Category.CATEGORY_ENERGY)
            startActivity(intent)
        }
        binding.categoryPersonalHabits.setOnClickListener {
            val intent = Intent(this@HomeActivity, DirectoryActivity::class.java)
            intent.putExtra("category", Category.CATEGORY_PERSONAL_HABIT)
            startActivity(intent)
        }
        binding.categoryTransportation.setOnClickListener {
            val intent = Intent(this@HomeActivity, DirectoryActivity::class.java)
            intent.putExtra("category", Category.CATEGORY_TRANSPORTATION)
            startActivity(intent)
        }
        binding.categoryRecycle.setOnClickListener {
            val intent = Intent(this@HomeActivity, DirectoryActivity::class.java)
            intent.putExtra("category", Category.CATEGORY_Recycle)
            startActivity(intent)
        }

        binding.buttonShare.setOnClickListener {
            try {
                val mailme = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_SUBJECT, "Let me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                }
                startActivity(mailme)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        if (item.itemId == R.id.menu_share) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                val shareMessage =
                    "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}