package com.umpa2020.tracer.util.gpx;

import android.net.Uri;
import android.util.Log;

import com.umpa2020.tracer.dataClass.RouteGPX;
import io.jenetics.jpx.*

import java.io.File;
import java.lang.Exception
import java.nio.file.Path;

class GPXConverter {
    fun classToGpx(routeGPX: RouteGPX, folderPath: String): Uri {
        try {
            Log.d("Save", "make gpx file")
            var gpxBuilder = GPX.builder()
            var track = Track.builder()
            track.addSegment(TrackSegment.of(routeGPX.trkList))
            gpxBuilder.addTrack(track.build())
            routeGPX.wptList.forEach{ gpxBuilder.addWayPoint(it)}
            var gpx = gpxBuilder.build()
            var saveFolder = File(folderPath) // 저장 경로
            if (!saveFolder.exists()) {       //폴더 없으면 생성
                saveFolder.mkdir()
            }
            var path = "route" + saveFolder.list().size + ".gpx"
            var myfile = File(saveFolder, path)         //로컬에 파일저장
            GPX.write(gpx, (myfile.path))
            Log.d("Save", "start upload gpx")
            return Uri.fromFile(myfile)
        } catch (e: Exception) {
            Log.d("save", e.toString());
        }
        return Uri.EMPTY
    }

    fun GpxToClass(path: String):RouteGPX{
        var gpx =GPX.read(path)
        return RouteGPX("test","Test",gpx.wayPoints,gpx.tracks[0].segments[0].points)
    }
}
