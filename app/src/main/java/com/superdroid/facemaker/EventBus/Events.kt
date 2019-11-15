package com.superdroid.facemaker.EventBus

import java.io.File

class Events {

    class FileListRequest()
    class FileListBack(var filelist:ArrayList<String>)
    class LoadToMain(var fileName:String)
    class FileFromLoad(var fileName:String)
    class FileNameRequest()
    class FileNameBack(var fileName: String)

    class ImgRequest(var fileName:String)
    class imgBack(var img: File)

}