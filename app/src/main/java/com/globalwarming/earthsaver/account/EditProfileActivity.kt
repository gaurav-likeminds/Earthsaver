package com.globalwarming.earthsaver.account

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ashraf007.expandableselectionview.adapter.BasicStringAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.globalwarming.earthsaver.R
import com.globalwarming.earthsaver.User
import com.globalwarming.earthsaver.Util
import com.globalwarming.earthsaver.databinding.ActivityEditProfileBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE = 101

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityEditProfileBinding

    private val genders = listOf("Select Gender...", "Male", "Female", "Other")
    private val ages = listOf(
        "Select Age...", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
        "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
        "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51",
        "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66",
        "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80"
    )

    private var imageUrl = ""
    private var gender = ""
    private var age = 0

    private var loader: Loader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        UserUtil.getUser(db, mAuth.uid!!) { user ->
            if (user != null) {
                updateUI(user)
            } else {
                Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT).show()
            }
        }


        val genderAdapter = BasicStringAdapter(genders, "Select Gender..")
        binding.genderGroup.setAdapter(genderAdapter)
        binding.genderGroup.selectionListener = { index ->
            gender = when (index) {
                1 -> "MALE"
                2 -> "FEMALE"
                3 -> "OTHER"
                else -> ""
            }
        }

        val ageAdapter = BasicStringAdapter(ages, "Select Age..")
        binding.ageGroup.setAdapter(ageAdapter)
        binding.ageGroup.selectionListener = { index ->
            age = if (index != null && index != 0) {
                ages[index].toInt()
            } else {
                0
            }
        }

        binding.fabUpdateProfileImage.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

        binding.buttonUpdate.setOnClickListener {
            Util.hideKeyboard(it)
            if (validate()) {
                if (loader == null) {
                    loader = Loader(this, "Updating profile ...")
                } else {
                    loader?.setText("Updating profile ...")
                }
                loader?.show()

                val name = binding.editTextName.editText!!.text.toString().trim()
                val email = binding.editTextEmailAddress.editText!!.text.toString().trim()

                db.collection("users")
                    .document(mAuth.uid!!)
                    .update(
                        "name",
                        name,
                        "email",
                        email,
                        "age",
                        age,
                        "gender",
                        gender,
                        "image",
                        imageUrl
                    )
                    .addOnCompleteListener { task ->
                        loader?.dismiss()
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        } else {
                            Snackbar.make(it, "Some error occurred", Snackbar.LENGTH_SHORT).show()
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

    private fun validate(): Boolean {
        val name = binding.editTextName.editText!!.text.toString().trim()
        val email = binding.editTextEmailAddress.editText!!.text.toString().trim()

        //Name
        if (name.isEmpty() || name.length < 5) {
            Snackbar.make(
                binding.editTextName,
                "Name is invalid. Must be a 5 character name",
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(binding.editTextEmailAddress, "Email is invalid.", Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        if (age == 0) {
            binding.ageGroup.setError("Please choose your age")
            return false
        } else {
            binding.ageGroup.setError(null)
        }

        if (gender.isEmpty()) {
            binding.genderGroup.setError("Please choose your gender")
            return false
        } else {
            binding.genderGroup.setError(null)
        }

        return true
    }

    private fun updateUI(user: User) {
        Glide.with(binding.profileImage)
            .load(user.image)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(binding.profileImage)
        binding.editTextName.editText!!.setText(user.name)
        binding.editTextEmailAddress.editText!!.setText(user.email)
        binding.editTextLocation.editText!!.setText(user.location)
        when (user.gender) {
            "MALE" -> binding.genderGroup.selectIndex(1)
            "FEMALE" -> binding.genderGroup.selectIndex(2)
            else -> binding.genderGroup.selectIndex(3)
        }
        if (user.age != 0L) {
            binding.ageGroup.selectIndex(user.age.toInt() - 9)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            if (loader == null) {
                loader = Loader(this, "Uploading profile image ...")
            } else {
                loader?.setText("Uploading profile image ...")
            }
            loader?.show()
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
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
                    imageUrl = task.result.toString()
                    Glide.with(binding.profileImage)
                        .load(uri)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(binding.profileImage)
                } else {
                    Snackbar.make(binding.root, "Unable to upload image", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}