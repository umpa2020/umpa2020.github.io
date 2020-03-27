package com.umpa2020.tracer.util.gpx;

import android.net.Uri;
import android.util.Log;

import com.umpa2020.tracer.dataClass.RouteGPX;
import com.umpa2020.tracer.util.Logg
import io.jenetics.jpx.*

import java.io.File;
import java.lang.Exception
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder

class GPXConverter {
    fun classToGpx(routeGPX: RouteGPX, folderPath: String): Uri {
        try {
            Logg.d( "make gpx file")
            var gpxBuilder = GPX.builder()
            var track = Track.builder()
            track.addSegment(TrackSegment.of(routeGPX.trkList))
            gpxBuilder.addTrack(track.build())
            routeGPX.wptList.forEach{ gpxBuilder.addWayPoint(it)}
            gpxBuilder.
            val gpx = gpxBuilder.build()
            val saveFolder = File(folderPath) // 저장 경로
            if (!saveFolder.exists()) {       //폴더 없으면 생성
                saveFolder.mkdir()
            }
            val path = "route" + saveFolder.list()!!.size + ".gpx"
            val myfile = File(saveFolder, path)         //로컬에 파일저장
            GPX.write(gpx, (myfile.path))
            Logg.d("start upload gpx")
            return Uri.fromFile(myfile)
        } catch (e: Exception) {
            Logg.d(e.toString());
        }
        return Uri.EMPTY
    }

    fun GpxToClass(path: String):RouteGPX{
        val gpx =GPX.read(path)
        return RouteGPX("test","Test",gpx.wayPoints,gpx.tracks[0].segments[0].points)
    }
}
