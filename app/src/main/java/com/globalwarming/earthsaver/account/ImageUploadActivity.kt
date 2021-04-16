package com.globalwarming.earthsaver.account

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.Image
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.databinding.ActivityImageUploadBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ImageUploadActivity : AppCompatActivity() {

    private val PICK_IMAGE = 101

    private var loader: Loader? = null
    private lateinit var binding: ActivityImageUploadBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_upload)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.imageViewUpload.setOnClickListener {
            chooseImage()
        }

        binding.imageView.setOnClickListener {
            chooseImage()
        }

        binding.buttonUpload.setOnClickListener {

            if (loader == null) {
                loader = Loader(this, "Uploading image ...")
            } else {
                loader?.setText("Uploading image ...")
            }
            loader?.show()

            val text = binding.editTextTitle.editText!!.text.toString().trim()
            val id = db.collection("users")
                .document(mAuth.uid!!)
                .collection("images")
                .document().id
            val image = Image(id, imageUrl, text, System.currentTimeMillis())

            db.collection("users")
                .document(mAuth.uid!!)
                .collection("images")
                .document(id)
                .set(image)
                .addOnCompleteListener { task ->
                    loader?.dismiss()
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Snackbar.make(it, "Some error occurred", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun chooseImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            if (loader == null) {
                loader = Loader(this, "Uploading image ...")
            } else {
                loader?.setText("Uploading image ...")
            }
            loader?.show()
            val storageRef = FirebaseStorage.getInstance().reference
                .child("photos")
                .child("${System.currentTimeMillis()}.png")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                loader?.dismiss()
                if (task.isSuccessful) {
                    binding.imageViewUpload.visibility = View.GONE
                    imageUrl = task.result.toString()
                    Glide.with(binding.imageView)
                        .load(uri)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(binding.imageView)
                } else {
                    Snackbar.make(binding.root, "Unable to upload image", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}