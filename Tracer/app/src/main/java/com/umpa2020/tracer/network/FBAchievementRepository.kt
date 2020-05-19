package com.umpa2020.tracer.network

import com.google.firebase.firestore.FieldValue
import com.umpa2020.tracer.dataClass.AchievementData
import com.umpa2020.tracer.dataClass.InfoData
import kotlinx.coroutines.tasks.await

class FBAchievementRepository : BaseFB() {
  fun createAchievement(userId: String) {
    val achievementData = AchievementData(0.0, 0, 0)
    achievementCollectionRef.document(userId).set(achievementData)
  }

  suspend fun getAchievement(userId: String): AchievementData? {
    return achievementCollectionRef.document(userId).get().await().toObject(AchievementData::class.java)
  }

  fun incrementTrackMake(userId: String) {
    achievementCollectionRef.document(userId).update(TRACK_MAKE, FieldValue.increment(1))
  }

  fun incrementDistance(userId: String, distance: Double) {
    achievementCollectionRef.document(userId).update(DISTANCE, FieldValue.increment(distance))
  }

  fun incrementPlays(userId: String) {
    achievementCollectionRef.document(userId).update(PLAYS, FieldValue.increment(1))
  }
}