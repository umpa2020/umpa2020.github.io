package com.umpa2020.tracer.network

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.tasks.await

/**
 * 스토리지에 파일을 업로드하는 Repository
 */
class FBStorageRepository : BaseFB() {

  /**
   * 파일 Uri와 Path만 넘겨줘서 올리는 작업 처리
   */
  fun uploadFile(uri: Uri, path: String) {
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜 경로 만들기
    storage.reference.child(path).putFile(uri)
  }

  suspend fun downloadFile(path: String): Uri? {
    return  storage.reference.child(path).downloadUrl.await()
  }

  fun downloadFile1(path: String): Task<Uri> {
    return storage.reference.child(path).downloadUrl
  }
}