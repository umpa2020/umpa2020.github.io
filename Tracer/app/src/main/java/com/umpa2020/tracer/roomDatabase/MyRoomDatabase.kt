package com.umpa2020.tracer.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.umpa2020.tracer.roomDatabase.Dao.GpsDao
import com.umpa2020.tracer.roomDatabase.Dao.RecordDao
import com.umpa2020.tracer.roomDatabase.entity.GPSData
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(MapRecordData::class, GPSData::class), version = 1)
abstract class MyRoomDatabase : RoomDatabase() {

  abstract fun recordDao(): RecordDao
  abstract fun gpsDao(): GpsDao

  private class RecordDatabaseCallback(private val scope: CoroutineScope) :
    RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
      super.onOpen(db)
      INSTANCE?.let { database ->
        scope.launch {
//          val recordDao = database.recordDao()

          // Delete all content here.
//          recordDao.deleteAll()

          Logg.d("지원지나?")
          // Add sample data.
//          // 초기값 설정
//          val mapRecordDao = MapRecordData(0, "", "", 0L, true, 0L)
//          Logg.d("처음에 데이터 삽입?")
//          recordDao.insert(mapRecordDao)
        }
      }
    }

  }

  companion object {
    // Singleton prevents multiple instances of database opening at the
    // same time.
    @Volatile
    private var INSTANCE: MyRoomDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): MyRoomDatabase {
      val tempInstance =
        INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }
      synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MyRoomDatabase::class.java,
          "record_database"
        ).addCallback(
          RecordDatabaseCallback(
            scope
          )
        ).addCallback(RecordDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        return instance
      }
    }
  }
}