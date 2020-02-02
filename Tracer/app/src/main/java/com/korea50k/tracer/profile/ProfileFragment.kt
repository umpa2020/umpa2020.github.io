package com.korea50k.tracer.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.korea50k.tracer.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    // firebase DB
    private var mFirestoreDB : FirebaseFirestore? = null

    //  firebase Storage
    private var mStorage : FirebaseStorage? = null
    private var mStorageReference : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         *  Firestore 초기화
         */

        mFirestoreDB = FirebaseFirestore.getInstance()

        /**
         *  Firebase Storage 초기화
         */

        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


}
