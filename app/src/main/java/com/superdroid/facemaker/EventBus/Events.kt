package com.superdroid.facemaker.EventBus

class Events {

    class FileListRequest()
    class FileListBack(var filelist:ArrayList<String>)
    class LoadToMain(var fileName:String)
    class FileFromLoad(var fileName:String)
    class FileNameRequest()
    class FileNameBack(var fileName: String)

}