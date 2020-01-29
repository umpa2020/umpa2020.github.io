package com.korea50k.tracer.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class GetMapData() {
    val TAG = "ssmm11"
    val db = FirebaseFirestore.getInstance()

    fun getMapData() {
        db.collection("mapData")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /*fun getSpecificMapData(maptitle: String) {
        db.collection("maps").document(maptitle)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }*/
}