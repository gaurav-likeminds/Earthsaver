package com.globalwarming.earthsaver

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.globalwarming.earthsaver.databinding.ActivityForgotPasswordBinding
import com.globalwarming.earthsaver.util.Loader
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val loader by lazy { Loader(this) }
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()

        binding.toolbar.setNavigationOnClickListener { _ ->
            onBackPressed()
        }

        binding.buttonResetPassword.setOnClickListener { v ->
            val email = binding.etEmail.editText?.text?.toString()?.trim()
            if (!email.isNullOrEmpty()) {
                Util.hideKeyboard(v)
                loader.show()
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void?>? ->
                    loader.dismiss()
                    if (task?.isSuccessful == true) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Reset link sent to registered email",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Snackbar.make(v, task?.exception?.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (loader.isShowing) {
            loader.dismiss()
        }
        super.onDestroy()
    }
}