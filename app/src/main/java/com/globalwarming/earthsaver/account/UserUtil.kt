package com.globalwarming.earthsaver.account

import com.globalwarming.earthsaver.User
import com.google.firebase.firestore.FirebaseFirestore

object UserUtil {

    fun getUser(db: FirebaseFirestore, uid: String, cb: (User?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val user = task.result!!.toObject(User::class.java)
                    if (user != null) {
                        user.id = task.result!!.id
                        cb(user)
                    } else {
                        cb(null)
                    }
                } else {
                    cb(null)
                }
            }
    }

}