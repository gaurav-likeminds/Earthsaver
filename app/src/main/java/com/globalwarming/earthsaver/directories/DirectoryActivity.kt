package com.globalwarming.earthsaver.directories

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.*
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.databinding.ActivityDirectoryBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.*

class DirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDirectoryBinding
    private lateinit var loader: Loader
    private lateinit var adapter: DirectoryQuestionAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val list = mutableListOf<DirectoryQuestion>()
    private val oldRespondedQuestions = mutableListOf<DirectoryQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_directory)
        setSupportActionBar(binding.toolbar)

        val category = intent.getStringExtra("category")

        binding.toolbar.title = category
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.recyclerView.setHasFixedSize(true)
        adapter = DirectoryQuestionAdapter()
        binding.recyclerView.adapter = adapter
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loader = Loader(this, "Fetching questions ...")
        loader.show()

        //fetchAllPrevious();
        db.collection("directories")
            .whereEqualTo("category", category)
            .get()
            .addOnCompleteListener { task ->
                loader.dismiss()
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val directoryQuestion = document.toObject(DirectoryQuestion::class.java)
                        directoryQuestion.id = document.id
                        list.add(directoryQuestion)
                    }
                    adapter.setList(list)
                }
            }
    }

    private fun fetchAllPrevious() {
        val minTimestamp = System.currentTimeMillis() - 60 * 60 * 24 * 1000 * 30
        db.collection("users")
            .document(mAuth.currentUser!!.uid)
            .collection("entries")
            .whereGreaterThan("timestamp", minTimestamp)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val directoryQuestion = document.toObject(DirectoryQuestion::class.java)
                        oldRespondedQuestions.add(directoryQuestion)
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_directory, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_submit) {
            Util.hideKeyboard(binding.parent)
            loader.setText("Submitting answers ...")
            loader.show()
            var points = 0
            val entries = adapter.getEntries()
            val userId = mAuth.uid!!
            val batch = db.batch()
            for ((key, value) in entries) {
                if (value.first) {
                    points++
                }
                val directoryEntry = DirectoryEntry()
                directoryEntry.id = key
                directoryEntry.timestamp = System.currentTimeMillis()
                directoryEntry.answer = value.first
                if (value.first) {
                    directoryEntry.point = 1L
                    directoryEntry.note = value.second
                } else {
                    directoryEntry.point = 0L
                }
                batch[db.collection("users")
                    .document(userId)
                    .collection("entries")
                    .document(key.toString() + "#" + directoryEntry.timestamp)] = directoryEntry
            }
            batch.update(
                db.collection("users").document(mAuth.uid!!),
                "points",
                FieldValue.increment(points.toLong())
            )
            batch.commit().addOnCompleteListener { task ->
                loader.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@DirectoryActivity,
                        "Successfully uploaded",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Snackbar.make(binding.parent, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (adapter.getEntries().isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to go back? Your answers won't be submitted if you go back!")
                .setNegativeButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                .setPositiveButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onDestroy()
    }
}