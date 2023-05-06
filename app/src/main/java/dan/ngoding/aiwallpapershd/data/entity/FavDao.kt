package dan.ngoding.aiwallpapershd.data.entity

import androidx.room.*
import dan.ngoding.aiwallpapershd.data.dao.Fav

@Dao
interface FavDao {
    @Query("SELECT * FROM fav ORDER BY uid ASC")
    fun getAll(): List<Fav>

    @Query("SELECT * FROM fav WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Fav>

    @Insert
    fun insert(fav: Fav)

    @Delete
    fun delete(fav: Fav)

    @Query("SELECT * FROM fav WHERE uid = :uid")
    fun get(uid: Int): Fav

}