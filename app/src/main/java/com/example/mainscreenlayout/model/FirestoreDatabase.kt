package com.example.mainscreenlayout.model

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object FirestoreDatabase {

    private val TAG: String = this.javaClass.simpleName

    fun getTask(query : String): Task<DocumentSnapshot> {
        val parts = query.split("/")
        val doc = Firebase.firestore.collection(parts[0]).document(parts[1])
        return doc.get()
    }

    fun getQuestions(query: String) : LiveData<List<Question>> {
        val data = MutableLiveData<List<Question>>()
        val db = Firebase.firestore
        val parts = query.split("/")

        db.collection(parts[0])
            .get()
            .addOnSuccessListener { result ->
                val response = ArrayList<Question>()
                for (document in result) {
                    val content = document.get("content").toString()
                    val answers = document.get("answers") as ArrayList<String>
                    response.add(Question(content, answers))
                }
                data.postValue(response)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }

        return data
    }

    fun getAny(query: String) : LiveData<Any> {
        val data = MutableLiveData<Any>()
        val db = Firebase.firestore
        val parts = query.split("/")

        db.collection(parts[0])
            .get()
            .addOnSuccessListener { result ->
                var response : Any? = null
                if (parts.size == 2) {
                    val response1 = ArrayList<MarkableItem>()
                    for (document in result) {
                        val item = MarkableItem(document.id, document.get(parts[1]).toString(), false)
                        response1.add(item)
                    }
                    response = response1
                }
                else if (parts.size == 3) {
                    for (document in result) {
                        if (document.id == parts[1]) {
                            response = document.get(parts[2])
                            break
                        }
                    }
                }
                else if (parts.size == 4) {
                    for (document in result) {
                        if (document.get("id") == parts[1]) {
                            response = document.get(parts[2])
                            break
                        }
                    }
                }
                if (response != null) {
                    data.postValue(response!!)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }

        return data
    }

    fun auth(activity: Activity) {
        val auth = Firebase.auth
        auth.signInAnonymously()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInAnonymously:success")
                } else {
                    Log.d(TAG, "signInAnonymously:failure", task.exception)
                }
            }
    }
}