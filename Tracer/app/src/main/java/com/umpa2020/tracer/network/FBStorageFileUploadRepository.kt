package com.umpa2020.tracer.network

import android.net.Uri
import com.umpa2020.tracer.util.UserInfo

/**
 * 스토리지에 파일을 업로드하는 Repository
 */
class FBStorageFileUploadRepository:BaseFB() {

  /**
   * 파일 Uri와 Path만 넘겨줘서 올리는 작업 처리
   */
  fun uploadFile(uri: Uri, path: String) {
    // 현재 날짜를 프로필 이름으로 nickname/Profile/현재날짜 경로 만들기

    val ref = storage.reference.child(path)
    // 이미지
    ref.putFile(uri)
  }
}