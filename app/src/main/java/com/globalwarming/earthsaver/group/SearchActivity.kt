package com.globalwarming.earthsaver.group

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.*
import com.globalwarming.earthsaver.databinding.ActivitySearchBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var db: FirebaseFirestore
    private val users = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        adapter = SearchAdapter()
        adapter.setListener(object : UserClickListener {
            override fun onUserClick(user: User) {
                val intent = Intent()
                intent.putExtra("user", user)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        db = FirebaseFirestore.getInstance()

        binding.buttonSearch.setOnClickListener {
            Util.hideKeyboard(it)
            val text = binding.editText.text.toString().trim()
            if (text.isNotEmpty()) {
                db.collection("users")
                    .whereEqualTo("email", text)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            users.clear()
                            for (document in task.result!!) {
                                val user = document.toObject(User::class.java)
                                user.id = document.id
                                users.add(user)
                            }
                            adapter.setList(users)
                        } else {
                            Snackbar.make(it, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}