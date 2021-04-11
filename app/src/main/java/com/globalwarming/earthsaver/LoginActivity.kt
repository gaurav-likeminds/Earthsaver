package com.globalwarming.earthsaver

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.databinding.ActivityLoginBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var loader: Loader
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setSupportActionBar(binding.toolbar)

        loader = Loader(this, "Logging in ...")
        mAuth = FirebaseAuth.getInstance()

        binding.textRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        binding.textForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener {
            Util.hideKeyboard(it)
            if (validate()) {
                val email = binding.etEmail.editText!!.text.toString().trim()
                val password = binding.etPassword.editText!!.text.toString().trim()
                loader.show()
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task: Task<AuthResult?>? ->
                        loader.dismiss()
                        if (task?.isSuccessful == true) {
                            //Login is complete
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            //Login is not complete
                            Snackbar.make(
                                binding.etEmail,
                                task?.exception?.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun validate(): Boolean {
        val email = binding.etEmail.editText!!.text.toString().trim()
        val password = binding.etPassword.editText!!.text.toString().trim()

        //Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(binding.etEmail, "Email is invalid.", Snackbar.LENGTH_SHORT).show()
            return false
        }
        //Password
        if (password.length < 6) {
            Snackbar.make(binding.etPassword, "Password has to e of 6 characters", Snackbar.LENGTH_SHORT).show()
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