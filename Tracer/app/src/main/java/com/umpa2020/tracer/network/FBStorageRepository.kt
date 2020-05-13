package com.umpa2020.tracer.network

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * 스토리지에 파일을 업로드, 다운로드하는 Repository
 */
class FBStorageRepository : BaseFB() {

  /**
   * 파일 Uri와 Path만 넘겨줘서 올리는 작업 처리
   */
  fun uploadFile(uri: Uri, path: String) {
    storage.reference.child(path).putFile(uri)
  }

  /**
   * storage에서 path를 넘겨주면 해당 파일을 uri로 반환
   */
  suspend fun downloadFile(path: String): Uri? {
    return  storage.reference.child(path).downloadUrl.await()
  }
  suspend fun getFile(path: String):String{
    val localFile = File.createTempFile("routeGpx", "gpx")
    storage.reference.child(path).getFile(localFile).await()
    return localFile.path
  }
}