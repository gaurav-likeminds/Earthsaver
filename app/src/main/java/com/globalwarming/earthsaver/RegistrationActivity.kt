package com.globalwarming.earthsaver

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.databinding.ActivityRegistrationBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var loader: Loader
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        setContentView(R.layout.activity_registration)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loader = Loader(this, "Registering ...")

        binding.button.setOnClickListener { v ->
            Util.hideKeyboard(v)
            if (validate()) {
                val name = binding.editTextName.editText!!.text.toString()
                val email = binding.editTextEmailAddress.editText!!.text.toString()
                var age = 0
                if (binding.editTextAge.editText!!.text.toString().isNotEmpty()) {
                    age = binding.editTextAge.editText!!.text.toString().toIntOrNull() ?: 0
                }
                val location = binding.editTextLocation.editText!!.text.toString()
                val password = binding.editTextPassword.editText!!.text.toString()
                val gender = when (binding.genderGroup.checkedRadioButtonId) {
                    R.id.radioButtonMale -> {
                        "MALE"
                    }
                    R.id.radioButtonFemale -> {
                        "FEMALE"
                    }
                    else -> {
                        "OTHER"
                    }
                }
                loader.show()
                val finalAge = age
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result!!.user!!.uid
                            val dataToUpload: MutableMap<String, Any> = HashMap()
                            dataToUpload["name"] = name
                            dataToUpload["email"] = email
                            dataToUpload["age"] = finalAge
                            dataToUpload["gender"] = gender
                            dataToUpload["location"] = location
                            db.collection("users")
                                .document(uid)
                                .set(dataToUpload)
                                .addOnCompleteListener { task1 ->
                                    loader.dismiss()
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
                            loader.dismiss()
                            Snackbar.make(v, task.exception?.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
            }
        }
    }

    private fun validate(): Boolean {
        val name = binding.editTextName.editText!!.text.toString().trim()
        val email = binding.editTextEmailAddress.editText!!.text.toString().trim()
        var age = 0
        if (binding.editTextAge.editText!!.text.toString().isNotEmpty()) {
            age = binding.editTextAge.editText!!.text.toString().toIntOrNull() ?: 0
        }
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

        //Age
        if (age < 10) {
            Snackbar.make(binding.editTextAge, "Minimum age is 10 years", Snackbar.LENGTH_SHORT).show()
            return false
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

    override fun onDestroy() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onDestroy()
    }
}