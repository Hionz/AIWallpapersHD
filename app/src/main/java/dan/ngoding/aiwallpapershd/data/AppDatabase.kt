package dan.ngoding.aiwallpapershd.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dan.ngoding.aiwallpapershd.data.entity.FavDao
import dan.ngoding.aiwallpapershd.data.dao.Fav

@Database(entities = [Fav::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favDao(): FavDao


    companion object{
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            if (instance==null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }


            return instance!!
        }
    }
}