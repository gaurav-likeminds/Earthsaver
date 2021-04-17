package com.globalwarming.earthsaver

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ashraf007.expandableselectionview.adapter.BasicStringAdapter
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.databinding.ActivityRegistrationBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private val genders = listOf("Select Gender...", "Male", "Female", "Other")
    private val ages = listOf(
        "Select Age...", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
        "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
        "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51",
        "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66",
        "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80"
    )

    private lateinit var binding: ActivityRegistrationBinding
    private var loader: Loader? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var imageUrl = ""
    private var gender = ""
    private var age = 0
    private val PICK_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        Glide.with(binding.profile).load(R.drawable.ic_account).into(binding.profile)

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

        binding.profile.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

        binding.button.setOnClickListener { v ->
            Util.hideKeyboard(v)
            if (validate()) {
                val name = binding.editTextName.editText!!.text.toString()
                val email = binding.editTextEmailAddress.editText!!.text.toString()
                val location = binding.editTextLocation.editText!!.text.toString()
                val password = binding.editTextPassword.editText!!.text.toString()
                if (loader == null) {
                    loader = Loader(this, "Registering account ...")
                } else {
                    loader?.setText("Registering account ...")
                }
                loader?.show()
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result!!.user!!.uid
                            val dataToUpload: MutableMap<String, Any> = HashMap()
                            dataToUpload["name"] = name
                            dataToUpload["email"] = email
                            dataToUpload["age"] = age
                            dataToUpload["gender"] = gender
                            dataToUpload["location"] = location
                            dataToUpload["image"] = imageUrl
                            db.collection("users")
                                .document(uid)
                                .set(dataToUpload)
                                .addOnCompleteListener { task1 ->
                                    loader?.dismiss()
                                    if (task1.isSuccessful) {
                                        val intent = Intent(
                                            this@RegistrationActivity,
                                            HomeActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Snackbar.make(
                                            v,
                                            task1.exception?.message.toString(),
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            loader?.dismiss()
                            Snackbar.make(
                                v,
                                task.exception?.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun validate(): Boolean {
        val name = binding.editTextName.editText!!.text.toString().trim()
        val email = binding.editTextEmailAddress.editText!!.text.toString().trim()
        val password = binding.editTextPassword.editText!!.text.toString()

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
            Snackbar.make(binding.editTextEmailAddress, "Email is invalid.", Snackbar.LENGTH_SHORT).show()
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

        //Password
        if (password.length < 6) {
            Snackbar.make(
                binding.editTextPassword,
                "Password has to e of 6 characters",
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        return true
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
                    Glide.with(binding.profile)
                        .load(uri)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(binding.profile)
                } else {
                    Snackbar.make(binding.root, "Unable to upload image", Snackbar.LENGTH_SHORT).show()
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
}