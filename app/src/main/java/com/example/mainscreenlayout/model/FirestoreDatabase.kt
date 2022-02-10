package com.example.mainscreenlayout.model

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreDatabase(private val context: FragmentActivity?) {

    private val TAG: String = this.javaClass.simpleName

    init {
        val auth = Firebase.auth
        if (context == null) {
            Log.d(TAG, "init:contextIsNull")
        } else {
            auth.signInAnonymously()
                .addOnCompleteListener(context) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                    } else {
                        Log.d(TAG, "signInAnonymously:failure", task.exception)
                    }
                }
        }
    }

    fun get(query: String) : LiveData<List<String>> {

        val data = MediatorLiveData<List<String>>()
        val db = Firebase.firestore

        val parts = query.split("/")

        db.collection(parts[0])
            .get()
            .addOnSuccessListener { result ->
                val response = ArrayList<String>()
                for (document in result) {
                    response.add(document.get(parts[1]).toString())
                }
                data.postValue(response)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }

        return data
    }
}