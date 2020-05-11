package com.umpa2020.tracer.network

import com.umpa2020.tracer.util.Logg

class FBUserInfoRepository : BaseFB() {
  fun createUserInfo(data: HashMap<String, String?>) {
    db.collection(USERS).document(data[USER_ID]!!).set(data)
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Logg.w("Error writing document$e") }
  }
}